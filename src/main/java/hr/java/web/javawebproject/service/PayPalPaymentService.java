package hr.java.web.javawebproject.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
@Slf4j
public class PayPalPaymentService {

    @Value("${paypal.baseUrl}")            private String baseUrl;
    @Value("${paypal.clientId}")           private String clientId;
    @Value("${paypal.secret}")             private String secret;

    private WebClient webClient;
    private String accessToken;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        obtainAccessToken();
    }

    private void obtainAccessToken() {
        String creds = Base64.getEncoder()
                .encodeToString((clientId + ":" + secret)
                        .getBytes(StandardCharsets.UTF_8));

        Map<String,Object> resp = webClient.post()
                .uri("/v1/oauth2/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + creds)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null || !resp.containsKey("access_token")) {
            throw new RuntimeException("Failed to get PayPal token");
        }
        accessToken = (String) resp.get("access_token");
        log.debug("PayPal access token acquired");
    }

    public Map<String,Object> createOrder(BigDecimal totalAmount, String currency) {
        return callWithRetry(() -> doCreateOrder(totalAmount, currency));
    }

    private Map<String,Object> doCreateOrder(BigDecimal totalAmount, String currency) {
        Map<String,Object> orderReq = new HashMap<>();
        orderReq.put("intent", "CAPTURE");
        orderReq.put("purchase_units", List.of(Map.of(
                "amount", Map.of(
                        "currency_code", currency,
                        "value", totalAmount.toString()
                )
        )));

        @SuppressWarnings("unchecked")
        Map<String, Object> resp = webClient.post()
                .uri("/v2/checkout/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .bodyValue(orderReq)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // extract approval link
        if (resp != null && resp.containsKey("links")) {
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> links = (List<Map<String,Object>>) resp.get("links");
            links.stream()
                    .filter(l -> "approve".equals(l.get("rel")))
                    .findFirst()
                    .ifPresent(link -> resp.put("approvalUrl", link.get("href")));
        }
        return resp;
    }

    public Map<String,Object> captureOrder(String orderId) {
        return callWithRetry(() -> webClient.post()
                .uri("/v2/checkout/orders/{id}/capture", orderId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block()
        );
    }

    /** retry-on-401 helper **/
    private Map<String,Object> callWithRetry(Supplier<Map<String,Object>> call) {
        try {
            return call.get();
        } catch (WebClientResponseException.Unauthorized e) {
            log.warn("PayPal token expired, re‑obtaining and retrying");
            obtainAccessToken();
            return call.get();
        }
    }
}
