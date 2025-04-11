package hr.java.web.javawebproject.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PayPalPaymentService {

    @Value("${paypal.baseUrl}")
    private String baseUrl;

    @Value("${paypal.clientId}")
    private String clientId;

    @Value("${paypal.secret}")
    private String secret;

    private WebClient webClient;
    private String accessToken;

    @PostConstruct
    public void init() {
        // Build a WebClient pointing to the PayPal sandbox base URL
        webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        // Obtain initial access token on startup
        obtainAccessToken();
    }

    /**
     * Obtain an OAuth2 access token from PayPal sandbox.
     */
    public void obtainAccessToken() {
        String auth = clientId + ":" + secret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        try {
            Mono<Map> responseMono = webClient.post()
                    .uri("/v1/oauth2/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                    .retrieve()
                    .bodyToMono(Map.class);

            Map<String, Object> response = responseMono.block();
            if (response == null || !response.containsKey("access_token")) {
                throw new RuntimeException("PayPal response did not contain an access token");
            }
            accessToken = (String) response.get("access_token");
            log.info("Obtained PayPal access token: {}", accessToken);
        } catch (Exception ex) {
            log.error("Error obtaining PayPal access token", ex);
            throw new RuntimeException("Failed to obtain PayPal access token", ex);
        }
    }

    /**
     * Create a PayPal order in sandbox.
     *
     * @param totalAmount The total order amount.
     * @param currency    The currency code (e.g., "USD").
     * @return A Map containing the PayPal order details, including the approval URL.
     */
    public Map<String, Object> createOrder(BigDecimal totalAmount, String currency) {
        try {
            Map<String, Object> orderRequest = new HashMap<>();
            orderRequest.put("intent", "CAPTURE");

            // Build the purchase_units object
            Map<String, Object> purchaseUnit = new HashMap<>();
            Map<String, String> amount = new HashMap<>();
            amount.put("currency_code", currency);
            amount.put("value", totalAmount.toString());
            purchaseUnit.put("amount", amount);

            orderRequest.put("purchase_units", new Object[]{purchaseUnit});

            Map<String, Object> response = webClient.post()
                    .uri("/v2/checkout/orders")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .bodyValue(orderRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Extract the approval URL from the "links" array in the response
            if (response != null && response.containsKey("links")) {
                List<Map<String, Object>> links = (List<Map<String, Object>>) response.get("links");
                for (Map<String, Object> link : links) {
                    if ("approve".equals(link.get("rel"))) {
                        response.put("approvalUrl", link.get("href"));
                        break;
                    }
                }
            }
            return response;
        } catch (Exception ex) {
            log.error("Failed to create PayPal order", ex);
            throw new RuntimeException("Unable to create PayPal order at this time", ex);
        }
    }

    /**
     * Capture a PayPal order once the buyer has approved it.
     *
     * @param orderId The ID of the PayPal order.
     * @return A Map containing the capture result.
     */
    public Map<String, Object> captureOrder(String orderId) {
        try {
            return webClient.post()
                    .uri("/v2/checkout/orders/{orderId}/capture", orderId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception ex) {
            log.error("Failed to capture PayPal order: {}", orderId, ex);
            throw new RuntimeException("Unable to capture PayPal order at this time", ex);
        }
    }
}
