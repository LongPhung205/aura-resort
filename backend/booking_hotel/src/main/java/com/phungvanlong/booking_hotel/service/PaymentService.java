package com.phungvanlong.booking_hotel.service;

import java.util.Map;

public interface PaymentService {
    String createMoMoPayment(Long bookingId);
    void processMoMoReturn(Map<String, String> params);
    void processMoMoIpn(Map<String, String> params);
    void simulateMoMoSuccess(Long bookingId);
}
