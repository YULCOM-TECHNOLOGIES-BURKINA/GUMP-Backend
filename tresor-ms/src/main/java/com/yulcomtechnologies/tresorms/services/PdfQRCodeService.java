package com.yulcomtechnologies.tresorms.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class PdfQRCodeService {

    public byte[] addQRCodeToPDF(byte[] pdfBytes, String qrCodeContent) throws Exception {
        // Load PDF document
        PDDocument document = PDDocument.load(pdfBytes);

        // Generate QR code image
        BufferedImage qrImage = generateQRCode(qrCodeContent, 200, 200);

        // Convert BufferedImage to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);

        // Create PDImageXObject from byte array
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(
            document,
            baos.toByteArray(),
            "QR Code"
        );

        // Get the last page
        PDPage lastPage = document.getPage(document.getNumberOfPages() - 1);

        // Calculate positions (bottom right corner with margins)
        float pageHeight = lastPage.getMediaBox().getHeight();
        float pageWidth = lastPage.getMediaBox().getWidth();
        float qrSize = 100f; // QR code size in the PDF
        float marginX = 50f; // Margin from right
        float marginY = 50f; // Margin from bottom

        // Create content stream for adding QR code
        PDPageContentStream contentStream = new PDPageContentStream(
            document,
            lastPage,
            PDPageContentStream.AppendMode.APPEND,
            true,
            true
        );

        // Add QR code image to the page
        contentStream.drawImage(
            pdImage,
            pageWidth - qrSize - marginX, // X position
            marginY,                      // Y position
            qrSize,                       // Width
            qrSize                        // Height
        );

        // Close the content stream
        contentStream.close();

        // Save the modified document to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }

    private BufferedImage generateQRCode(String content, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
            content,
            BarcodeFormat.QR_CODE,
            width,
            height
        );

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
