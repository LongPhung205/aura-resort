package com.phungvanlong.booking_hotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDashboardStatsResponse {
    
    // 1. Tổng doanh thu (Gross Revenue)
    private BigDecimal totalRevenue;
    
    // 2. Doanh thu phòng / villa
    private BigDecimal roomRevenue;
    
    // 3. Doanh thu dịch vụ (Minibar, tour, spa, đưa đón Maybach)
    private BigDecimal serviceRevenue;
    
    // 4. Tiền đã thu thực tế (SUCCESS)
    private BigDecimal collectedAmount;
    
    // 5. Tiền chờ thu (PENDING)
    private BigDecimal pendingAmount;
    
    // 6. Tiền đã hoàn / hủy (REFUNDED)
    private BigDecimal refundedAmount;
    
    // 7. Tỷ lệ thanh toán thành công (%)
    private double successRate;
    
    // 8. Giá trị đơn hàng trung bình (AOV - Average Order Value)
    private BigDecimal aov;
    
    // 9. Tỷ lệ tăng trưởng so với kỳ trước (%)
    private double growthRate;
    
    // 10. Tổng số giao dịch
    private long totalTransactions;
    
    // 11. Doanh thu theo ngày (30 ngày gần nhất)
    private List<DailyRevenue> lineChartData;
    
    // 12. Doanh thu theo tháng (12 tháng gần nhất)
    private List<MonthlyRevenue> monthlyChartData;
    
    // 13. Cơ cấu phương thức thanh toán
    private Map<String, BigDecimal> donutChartData;
    
    // 14. Thống kê chi tiết theo cổng thanh toán
    private List<PaymentMethodStat> methodStats;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DailyRevenue {
        private String date; // dd/MM
        private BigDecimal revenue;
        private BigDecimal roomRevenue;
        private BigDecimal serviceRevenue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MonthlyRevenue {
        private String month; // T1, T2... hoặc MM/yyyy
        private BigDecimal revenue;
        private BigDecimal roomRevenue;
        private BigDecimal serviceRevenue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PaymentMethodStat {
        private String method;
        private long transactionCount;
        private BigDecimal actualRevenue;
        private BigDecimal pendingRevenue;
        private double percentage;
    }
}
