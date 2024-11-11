package com.yulcomtechnologies.tresorms.services;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfFiligraneService {


    public byte[] addFiligraneToPDF(byte[] pdfBytes, String textFiligrane) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputStream), new PdfWriter(outputStream));
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            int nombrePages = pdfDoc.getNumberOfPages();

            for (int i = 1; i <= nombrePages; i++) {
                PdfCanvas canvas = new PdfCanvas(pdfDoc.getPage(i));
                Rectangle taillePage = pdfDoc.getPage(i).getPageSize();
                float pageWidth = taillePage.getWidth();
                float pageHeight = taillePage.getHeight();

                float fontSize = 31;
                float textWidth = font.getWidth(textFiligrane, fontSize);
                float textHeight = fontSize;

                float x = (pageWidth - textWidth) /2+140;
                float y = textHeight+120;
                canvas.saveState();
                canvas.setFillColor(new DeviceGray(0.5f));
                canvas.setStrokeColor(new DeviceGray(0.5f));
                canvas.setFontAndSize(font, fontSize);


                PdfExtGState gs1 = new PdfExtGState().setFillOpacity(0.3f);
                canvas.setExtGState(gs1);

                canvas.beginText();
                canvas.setFontAndSize(font, fontSize);

                canvas.concatMatrix(1, 0, 0, 1, x, y);
                canvas.concatMatrix((float) Math.cos(Math.toRadians(50)), (float) Math.sin(Math.toRadians(50)),
                        (float) -Math.sin(Math.toRadians(50)), (float) Math.cos(Math.toRadians(50)), 0, 0);

                canvas.showText(textFiligrane);
                canvas.endText();
                canvas.restoreState();
            }

            pdfDoc.close();
            return outputStream.toByteArray();
        }
    }


}
