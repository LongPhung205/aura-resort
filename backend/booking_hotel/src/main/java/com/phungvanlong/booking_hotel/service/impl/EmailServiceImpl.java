package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:phungvanlong10925@gmail.com}")
    private String mailFrom;

    @Value("${app.otp.expiration:5}")
    private long otpExpiration;

    @Override
    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailFrom, "Aura Elite Resort");
            helper.setTo(to);
            helper.setSubject("Your OTP Code for Registration");
            
            String htmlContent = "<p>Hello,</p>"
                    + "<p>Your OTP code for registration is: <strong>" + otp + "</strong></p>"
                    + "<p>This code will expire in " + otpExpiration + " minutes.</p>";
            
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendPasswordResetOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailFrom, "Aura Elite Resort");
            helper.setTo(to);
            helper.setSubject("Mã OTP khôi phục mật khẩu tài khoản của bạn");
            
            String htmlContent = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;\">\n" +
                    "  <h2 style=\"color: #0ea5e9; text-align: center;\">Aura Elite - Khôi phục mật khẩu</h2>\n" +
                    "  <p>Xin chào,</p>\n" +
                    "  <p>Bạn vừa yêu cầu khôi phục mật khẩu cho tài khoản tại hệ thống Aura Elite. Vui lòng sử dụng mã OTP dưới đây để xác thực:</p>\n" +
                    "  <div style=\"background-color: #f3f4f6; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px; color: #333; margin: 20px 0; border-radius: 5px;\">\n" +
                    "    " + otp + "\n" +
                    "  </div>\n" +
                    "  <p>Mã OTP này có hiệu lực trong vòng <strong>" + otpExpiration + " phút</strong>. Tuyệt đối không chia sẻ mã này với bất kỳ ai.</p>\n" +
                    "  <p>Nếu bạn không yêu cầu khôi phục mật khẩu, vui lòng bỏ qua email này.</p>\n" +
                    "  <hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\" />\n" +
                    "  <p style=\"font-size: 12px; color: #888; text-align: center;\">Đây là email tự động, vui lòng không phản hồi.</p>\n" +
                    "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send password reset OTP email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendBookingSuccessEmail(String to, String bookingCode, String guestName) {
        sendBookingSuccessEmail(to, bookingCode, guestName, null, null, null);
    }

    @Override
    public void sendBookingSuccessEmail(String to, String bookingCode, String guestName, LocalDate checkInDate, LocalDate checkOutDate, BigDecimal totalAmount) {
        if (to == null || to.isBlank()) {
            log.warn("Cannot send booking success email: recipient email is blank for booking {}", bookingCode);
            return;
        }

        try {
            log.info("Sending booking success email to: {}, bookingCode: {}", to, bookingCode);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailFrom, "Aura Elite Resort");
            helper.setTo(to.trim());
            helper.setSubject("Xác nhận đặt phòng thành công - Mã Check-in: " + bookingCode);

            String formattedAmount = "";
            if (totalAmount != null) {
                NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
                formattedAmount = nf.format(totalAmount) + " VNĐ";
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String checkInStr = checkInDate != null ? checkInDate.format(dtf) : "Theo thông tin đặt";
            String checkOutStr = checkOutDate != null ? checkOutDate.format(dtf) : "Theo thông tin đặt";
            String displayName = (guestName != null && !guestName.isBlank()) ? guestName : "Quý khách";

            String htmlContent = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 620px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);\">\n" +
                    "  <div style=\"background: linear-gradient(135deg, #0369a1 0%, #0284c7 100%); padding: 32px 24px; text-align: center;\">\n" +
                    "    <h1 style=\"color: #ffffff; margin: 0; font-size: 24px; font-weight: 800; letter-spacing: 1px;\">AURA ELITE RESORT</h1>\n" +
                    "    <p style=\"color: #e0f2fe; margin: 8px 0 0 0; font-size: 13px; text-transform: uppercase; letter-spacing: 2px;\">Xác nhận đặt phòng thành công</p>\n" +
                    "  </div>\n" +
                    "  <div style=\"padding: 32px 24px;\">\n" +
                    "    <p style=\"font-size: 16px; color: #1e293b; margin-top: 0;\">Kính gửi <strong>" + displayName + "</strong>,</p>\n" +
                    "    <p style=\"font-size: 14px; color: #475569; line-height: 1.6;\">Cảm ơn Quý khách đã đặt kỳ nghỉ tại <strong>Aura Elite Resort</strong>. Đơn đặt phòng của Quý khách đã được xác nhận thành công với phương thức <strong>Thanh toán khi nhận phòng (Pay at Hotel)</strong>.</p>\n" +
                    "    \n" +
                    "    <div style=\"background-color: #f0f9ff; border: 2px dashed #0284c7; border-radius: 12px; padding: 20px; text-align: center; margin: 24px 0;\">\n" +
                    "      <div style=\"font-size: 12px; font-weight: 700; color: #0369a1; text-transform: uppercase; letter-spacing: 1.5px; margin-bottom: 6px;\">MÃ CHECK-IN TỨC THÌ TẠI QUẦY</div>\n" +
                    "      <div style=\"font-size: 32px; font-weight: 900; color: #0369a1; letter-spacing: 3px;\">" + bookingCode + "</div>\n" +
                    "      <div style=\"font-size: 12px; color: #64748b; margin-top: 8px;\">Khi đến nhận phòng, Quý khách chỉ cần đọc hoặc đưa mã này cho nhân viên lễ tân để làm thủ tục nhận phòng tức thì.</div>\n" +
                    "    </div>\n" +
                    "    \n" +
                    "    <h3 style=\"font-size: 15px; color: #0f172a; border-bottom: 2px solid #f1f5f9; padding-bottom: 8px; margin-bottom: 12px;\">Chi tiết kỳ nghỉ</h3>\n" +
                    "    <table style=\"width: 100%; border-collapse: collapse; font-size: 14px; color: #334155; margin-bottom: 24px;\">\n" +
                    "      <tr><td style=\"padding: 8px 0; color: #64748b;\">Khách hàng:</td><td style=\"padding: 8px 0; font-weight: 600; text-align: right;\">" + displayName + "</td></tr>\n" +
                    "      <tr><td style=\"padding: 8px 0; color: #64748b;\">Ngày nhận phòng:</td><td style=\"padding: 8px 0; font-weight: 600; text-align: right;\">" + checkInStr + " (từ 14:00)</td></tr>\n" +
                    "      <tr><td style=\"padding: 8px 0; color: #64748b;\">Ngày trả phòng:</td><td style=\"padding: 8px 0; font-weight: 600; text-align: right;\">" + checkOutStr + " (trước 12:00)</td></tr>\n" +
                    "      <tr><td style=\"padding: 8px 0; color: #64748b;\">Phương thức thanh toán:</td><td style=\"padding: 8px 0; font-weight: 600; text-align: right; color: #0284c7;\">Thanh toán khi nhận phòng</td></tr>\n" +
                    (formattedAmount.isEmpty() ? "" : "      <tr><td style=\"padding: 8px 0; color: #64748b; font-weight: 700;\">Tổng thanh toán:</td><td style=\"padding: 8px 0; font-weight: 800; font-size: 16px; color: #0f172a; text-align: right;\">" + formattedAmount + "</td></tr>\n") +
                    "    </table>\n" +
                    "    \n" +
                    "    <p style=\"font-size: 13px; color: #64748b; line-height: 1.6; background-color: #f8fafc; padding: 14px; border-radius: 8px;\">Nếu Quý khách cần hỗ trợ thêm dịch vụ đưa đón, tiệc nướng BBQ hoặc thay đổi lịch trình, vui lòng liên hệ hotline: <strong style=\"color: #0369a1;\">0961.472.686</strong>.</p>\n" +
                    "  </div>\n" +
                    "  <div style=\"background-color: #f8fafc; border-top: 1px solid #e2e8f0; padding: 16px 24px; text-align: center;\">\n" +
                    "    <p style=\"font-size: 12px; color: #94a3b8; margin: 0;\">Aura Elite Resort & Spa - Thiên đường nghỉ dưỡng đẳng cấp</p>\n" +
                    "  </div>\n" +
                    "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Booking success email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send booking success email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send booking success email: " + e.getMessage(), e);
        }
    }
}
