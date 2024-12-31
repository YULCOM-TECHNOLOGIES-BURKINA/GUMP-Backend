package com.yulcomtechnologies.drtssms.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Getter
@Setter
@Service
public class PdfQrCodeService {
    public PdfQrCodeService() {
    }
    public byte[] addQRCodeToPDF(byte[] pdfBytes, String qrCodeContent) throws Exception {
        PDDocument document = PDDocument.load(pdfBytes);
        BufferedImage qrImage = this.generateQRCode(qrCodeContent, 200, 200);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, baos.toByteArray(), "QR Code");
        PDPage lastPage = document.getPage(document.getNumberOfPages() - 1);
        float pageHeight = lastPage.getMediaBox().getHeight();
        float pageWidth = lastPage.getMediaBox().getWidth();
        float qrSize = 100.0F;
        float marginX = 50.0F; // Garde un petit espace à gauche
        float marginY = 50.0F; // Position verticale
        PDPageContentStream contentStream = new PDPageContentStream(document, lastPage, PDPageContentStream.AppendMode.APPEND, true, true);
        contentStream.drawImage(pdImage, marginX, marginY, qrSize, qrSize); // Déplace à gauche
        contentStream.close();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();
        return outputStream.toByteArray();
    }

    private BufferedImage generateQRCode(String content, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}