package hr.java.web.javawebproject.service;



import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "paypal.baseUrl=http://localhost:8089",
        "paypal.clientId=testClientId",
        "paypal.secret=testSecret"
})
@AutoConfigureWireMock(port = 8089)
public class PayPalPaymentServiceTest {

    @Autowired
    private PayPalPaymentService payPalPaymentService;

    @BeforeEach
    public void setup() {
        WireMock.reset();
    }

    @Test
    public void testObtainAccessToken() {
        // Stub the token endpoint
        stubFor(post(urlEqualTo("/v1/oauth2/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"access_token\":\"dummyAccessToken\"}")));

        // Call the method to obtain token
        payPalPaymentService.obtainAccessToken();

        // Use ReflectionTestUtils to get the private accessToken field
        String token = (String) ReflectionTestUtils.getField(payPalPaymentService, "accessToken");
        assertEquals("dummyAccessToken", token, "Access token should match the dummy token");
    }

    @Test
    public void testCreateOrder() {
        // Stub the create order endpoint
        stubFor(post(urlEqualTo("/v2/checkout/orders"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"ORDER123\"}")));

        // Ensure an access token is set (simulate token already obtained)
        ReflectionTestUtils.setField(payPalPaymentService, "accessToken", "dummyAccessToken");

        BigDecimal totalAmount = new BigDecimal("100.00");
        Map<String, Object> response = payPalPaymentService.createOrder(totalAmount, "USD");

        assertNotNull(response, "Response should not be null");
        assertTrue(response.containsKey("id"), "Response should contain an 'id' field");
        assertEquals("ORDER123", response.get("id"), "Order ID should be 'ORDER123'");
    }

    @Test
    public void testCaptureOrder() {
        String orderId = "ORDER123";
        // Stub the capture order endpoint
        stubFor(post(urlEqualTo("/v2/checkout/orders/" + orderId + "/capture"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"COMPLETED\"}")));

        // Ensure an access token is set
        ReflectionTestUtils.setField(payPalPaymentService, "accessToken", "dummyAccessToken");

        Map<String, Object> response = payPalPaymentService.captureOrder(orderId);
        assertNotNull(response, "Capture response should not be null");
        assertEquals("COMPLETED", response.get("status"), "Order capture status should be COMPLETED");
    }
}
