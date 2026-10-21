package com.phungvanlong.booking_hotel.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Getter
@Configuration
public class PayOSConfig {

    @Value("${client_id_payos}")
    private String clientId;

    @Value("${apikey_payos}")
    private String apiKey;

    @Value("${checksum_payos}")
    private String checksumKey;

    @Value("${payos.return-url:${app.backend-url:http://localhost:8080}/api/v1/payments/payos-return}")
    private String returnUrl;

    @Value("${payos.frontend-url:${app.frontend-url:http://localhost:4200}/payment/payos-result}")
    private String frontendUrl;

    @Bean
    public PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }
}

