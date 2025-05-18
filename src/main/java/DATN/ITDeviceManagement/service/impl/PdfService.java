package DATN.ITDeviceManagement.service.impl;

import DATN.ITDeviceManagement.exception.ErrorCode;
import DATN.ITDeviceManagement.repository.UserRepository;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import DATN.ITDeviceManagement.entity.Device;
import DATN.ITDeviceManagement.entity.DeviceAssignment;
import DATN.ITDeviceManagement.entity.User;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class PdfService {
    @Autowired
    private UserRepository userRepository;

    private PdfFont getFont() throws Exception {
        String fontPath = "src/main/resources/fonts/Arial.ttf";
        return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
    }

    public String generateHandoverPdf(DeviceAssignment assignment, List<Device> devices, String receiverName, String signName, String returnConfirm, boolean isFrom, boolean isTo) throws Exception {
        String fileName = "handover_" + assignment.getId() + ".pdf";
        String filePath = "Uploads/pdf/" + fileName;

        File directory = new File("Uploads/pdf");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        PdfFont font = getFont();

        Paragraph header = new Paragraph("NMAXSOFTCOMPANY - IT DEVICE MANAGEMENT")
                .setFont(font)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(header);

        Paragraph title = new Paragraph("BIÊN BẢN BÀN GIAO VẬT TƯ")
                .setFont(font)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                .setPadding(5)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setMarginBottom(20);
        document.add(title);

        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        infoTable.setWidth(UnitValue.createPercentValue(80));
        infoTable.setMarginBottom(20);
        infoTable.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

        infoTable.addCell(new Cell().add(new Paragraph("Mã bàn giao:").setFont(font).setBold()).setBorder(Border.NO_BORDER));
        infoTable.addCell(new Cell().add(new Paragraph(String.valueOf(assignment.getId())).setFont(font)).setBorder(Border.NO_BORDER));
        String handoverDate = assignment.getHandoverDate() != null ? assignment.getHandoverDate().toString() : "N/A";
        infoTable.addCell(new Cell().add(new Paragraph("Ngày bàn giao:").setFont(font).setBold()).setBorder(Border.NO_BORDER));
        infoTable.addCell(new Cell().add(new Paragraph(handoverDate.split("T")[0]).setFont(font)).setBorder(Border.NO_BORDER));
        infoTable.addCell(new Cell().add(new Paragraph("Người nhận:").setFont(font).setBold()).setBorder(Border.NO_BORDER));
        infoTable.addCell(new Cell().add(new Paragraph(receiverName != null ? receiverName : "Chưa xác nhận").setFont(font)).setBorder(Border.NO_BORDER));

        document.add(infoTable);

        float[] columnWidths = {1, 3, 3, 3};
        Table deviceTable = new Table(UnitValue.createPercentArray(columnWidths));
        deviceTable.setWidth(UnitValue.createPercentValue(100));
        deviceTable.setMarginBottom(20);

        String[] headers = {"STT", "Serial Number", "Tên vật tư", "Xác nhận trả đồ"};
        for (String headerText : headers) {
            deviceTable.addHeaderCell(new Cell()
                    .add(new Paragraph(headerText).setFont(font).setBold())
                    .setBackgroundColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 1)));
        }

        for (int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            deviceTable.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(i + 1)).setFont(font))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(i % 2 == 0 ? ColorConstants.WHITE : ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
            deviceTable.addCell(new Cell()
                    .add(new Paragraph(device.getSerialNumber() != null ? device.getSerialNumber() : "N/A").setFont(font))
                    .setBackgroundColor(i % 2 == 0 ? ColorConstants.WHITE : ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
            deviceTable.addCell(new Cell()
                    .add(new Paragraph(device.getManufacture() != null ? device.getManufacture() : "N/A").setFont(font))
                    .setBackgroundColor(i % 2 == 0 ? ColorConstants.WHITE : ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
            deviceTable.addCell(new Cell()
                    .add(new Paragraph(returnConfirm != null ? returnConfirm : "").setFont(font))
                    .setBackgroundColor(i % 2 == 0 ? ColorConstants.WHITE : ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f)));
        }

        document.add(deviceTable);

        Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        signatureTable.setWidth(UnitValue.createPercentValue(80));
        signatureTable.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

        User handoverUser = userRepository.findById(assignment.getHandoverPerson()).orElseThrow();
        User receiverUser = assignment.getToUser();

        Cell giverCell = new Cell()
                .add(new Paragraph("Người bàn giao").setFont(font).setBold().setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph("(Ký, ghi rõ họ tên)").setFont(font).setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph(handoverUser.getFirstname() + " " + handoverUser.getLastname()).setFont(font).setTextAlignment(TextAlignment.CENTER).setMarginTop(20))
                .setHeight(80)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1));

        if (handoverUser.getSignaturePath() != null && isFrom) {
            byte[] signatureBytes = decryptSignature(handoverUser.getSignaturePath(), handoverUser.getEncryptionKey());
            Image signatureImage = new Image(ImageDataFactory.create(signatureBytes))
                    .setWidth(100)
                    .setHeight(50)
                    .setAutoScale(false);

            // Create a Div to center the image
            Div imageDiv = new Div()
                    .add(signatureImage)
                    .setTextAlignment(TextAlignment.CENTER) // Center the image horizontally
                    .setVerticalAlignment(VerticalAlignment.MIDDLE) // Center the image vertically
                    .setMarginTop(4).setMarginLeft(60);// Add some spacing if needed

            giverCell.add(imageDiv);
        }
        signatureTable.addCell(giverCell);

        Cell receiverCell = new Cell()
                .add(new Paragraph("Người nhận").setFont(font).setBold().setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph("(Ký, ghi rõ họ tên)").setFont(font).setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph(signName != null ? signName : "Chưa xác nhận").setFont(font).setTextAlignment(TextAlignment.CENTER).setMarginTop(20))
                .setHeight(80)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1));

        if (receiverUser.getSignaturePath() != null && isTo) {
            byte[] signatureBytes = decryptSignature(receiverUser.getSignaturePath(), receiverUser.getEncryptionKey());
            Image signatureImage = new Image(ImageDataFactory.create(signatureBytes))
                    .setWidth(100)
                    .setHeight(50)
                    .setAutoScale(false);

            // Create a Div to center the image
            Div imageDiv = new Div()
                    .add(signatureImage)
                    .setTextAlignment(TextAlignment.CENTER) // Center the image horizontally
                    .setVerticalAlignment(VerticalAlignment.MIDDLE) // Center the image vertically
                    .setMarginTop(4)
            .setMarginLeft(60);// Add some spacing if needed

            receiverCell.add(imageDiv);
        }
        signatureTable.addCell(receiverCell);

        document.add(signatureTable);

        document.close();
        pdf.close();
        writer.close();

        return filePath;
    }

    public String updateHandoverPdf(DeviceAssignment assignment, List<Device> devices, String receiverName, String signName, String returnConfirm, boolean isFrom, boolean isTo) throws Exception {
        File oldFile = new File(assignment.getPdfPath());
        if (oldFile.exists()) {
            if (!oldFile.delete()) {
                throw new RuntimeException("Failed to delete old PDF file: " + oldFile.getAbsolutePath());
            }
        }
        return generateHandoverPdf(assignment, devices, receiverName, signName, returnConfirm, isFrom, isTo);
    }

    public User getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = Optional.ofNullable(userRepository.findByUsername(name))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.NON_EXISTING_ID_USER.getMessage()));
        return user;
    }

    private byte[] decryptSignature(String signaturePath, String encryptedKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(encryptedKey);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        File file = new File(signaturePath);
        byte[] fileBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileBytes);
        }

        byte[] ivBytes = new byte[16];
        byte[] encrypted = new byte[fileBytes.length - 16];
        System.arraycopy(fileBytes, 0, ivBytes, 0, 16);
        System.arraycopy(fileBytes, 16, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));
        return cipher.doFinal(encrypted);
    }
}