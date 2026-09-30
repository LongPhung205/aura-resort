package com.phungvanlong.booking_hotel.service.impl;

import com.phungvanlong.booking_hotel.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${app.otp.expiration:5}")
    private long otpExpiration;

    @Override
    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Your OTP Code for Registration");
            
            String htmlContent = "<p>Hello,</p>"
                    + "<p>Your OTP code for registration is: <strong>" + otp + "</strong></p>"
                    + "<p>This code will expire in " + otpExpiration + " minutes.</p>";
            
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendPasswordResetOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

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
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
