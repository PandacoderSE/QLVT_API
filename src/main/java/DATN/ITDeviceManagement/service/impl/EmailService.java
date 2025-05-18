package DATN.ITDeviceManagement.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Phương thức gửi email bất đồng bộ cho danh sách người nhận
    @Async
    public void sendBatchEmail(List<String> recipients, String subject, String content, LocalDateTime createdDate) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Thiết lập thông tin email
        helper.setSubject("[NMAXSOFT] " + subject);
        helper.setFrom("itdevisionnmaxsoft@gmail.com"); // Thay bằng email của bạn
        helper.setBcc(recipients.toArray(new String[0])); // Gửi BCC cho tất cả người nhận

        // Tạo nội dung HTML
        String htmlContent = buildHtmlContent(content, createdDate);
        helper.setText(htmlContent, true); // true = HTML

        // Gửi email
        mailSender.send(message);
    }

    // Phương thức gửi email cho một người (giữ lại nếu cần)
    public void sendEmail(String to, String subject, String content, LocalDateTime createdDate) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("no-reply@nmaxsoft.com"); // Thay bằng email của bạn

        String htmlContent = buildHtmlContent(content, createdDate);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    // Tách logic tạo HTML content ra để tái sử dụng
    private String buildHtmlContent(String content, LocalDateTime createdDate) {
        var createD = (createdDate.toString()).split("T")[0];
        return String.format(
                "<html>" +
                        "<head>" +
                        "<style>" +
                        "body {" +
                        "    font-family: 'Segoe UI', Arial, sans-serif;" +
                        "    background-color: #f7f9fc;" +
                        "    margin: 0;" +
                        "    padding: 30px;" +
                        "}" +
                        ".container {" +
                        "    max-width: 650px;" +
                        "    margin: 0 auto;" +
                        "    background-color: #ffffff;" +
                        "    padding: 30px;" +
                        "    border-radius: 10px;" +
                        "    box-shadow: 0 4px 12px rgba(0,0,0,0.1);" +
                        "    border-top: 4px solid #0078d4;" +
                        "}" +
                        "h2 {" +
                        "    color: #1a3c6e;" +
                        "    text-align: center;" +
                        "    font-size: 24px;" +
                        "    margin-bottom: 20px;" +
                        "    font-weight: 600;" +
                        "}" +
                        "p {" +
                        "    color: #444444;" +
                        "    line-height: 1.8;" +
                        "    font-size: 16px;" +
                        "    margin: 10px 0;" +
                        "}" +
                        ".content-label {" +
                        "    font-weight: bold;" +
                        "    color: #333333;" +
                        "}" +
                        ".signature {" +
                        "    margin-top: 30px;" +
                        "    padding-top: 20px;" +
                        "    border-top: 1px solid #e0e0e0;" +
                        "    font-size: 13px;" +
                        "    color: #777777;" +
                        "    text-align: left;" +
                        "}" +
                        ".signature .company {" +
                        "    font-weight: bold;" +
                        "    color: #0078d4;" +
                        "    font-size: 14px;" +
                        "}" +
                        ".signature .contact {" +
                        "    margin: 5px 0;" +
                        "}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class=\"container\">" +
                        "    <h2>Thông Báo từ Phòng ADMIN công ty NMAXSOFT!</h2>" +
                        "    <p><span class=\"content-label\">Nội Dung:</span> %s</p>" +
                        "    <p><span class=\"content-label\">Ngày gửi:</span> %s</p>" +
                        "    <div class=\"signature\">" +
                        "        <div class=\"company\">Công ty NMaxsoft</div>" +
                        "        <div class=\"contact\">Điện thoại: 0852608689</div>" +
                        "        <div class=\"contact\">Địa chỉ: QL32, Nam Từ Liêm, Hà Nội</div>" +
                        "    </div>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                content, createD
        );
    }
}