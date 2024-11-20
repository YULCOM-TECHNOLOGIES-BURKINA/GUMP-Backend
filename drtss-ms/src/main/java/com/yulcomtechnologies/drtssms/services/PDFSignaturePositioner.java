package com.yulcomtechnologies.drtssms.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFSignaturePositioner extends PDFTextStripper {

    private float xPosition = 0;
    private float yPosition = 0;
    private int pageIndex = 0;
    private final String keyword;
    private final List<SignaturePosition> positions = new ArrayList<>();

    public PDFSignaturePositioner(String keyword) throws IOException {
        this.keyword = keyword;
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        String textContent = text.getUnicode();
        if (textContent != null && textContent.equals(keyword)) { // Correspondance exacte
            xPosition = text.getXDirAdj();
            yPosition = text.getYDirAdj();
            positions.add(new SignaturePosition(xPosition, yPosition, getCurrentPageNo() - 1));
        }
    }

    public void findKeywordPosition(PDDocument document) throws IOException {
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            this.setStartPage(i + 1);
            this.setEndPage(i + 1);
            this.processPage(document.getPage(i));
        }

        // Si plusieurs occurrences, choisir la dernière occurrence
        if (!positions.isEmpty()) {
            SignaturePosition lastPosition = positions.get(positions.size() - 1);
            xPosition = lastPosition.getX();
            yPosition = lastPosition.getY();
            pageIndex = lastPosition.getPageIndex();
        }
    }

    public float getXPosition() {
        return xPosition;
    }

    public float getYPosition() {
        return yPosition;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    private static class SignaturePosition {
        private final float x;
        private final float y;
        private final int pageIndex;

        public SignaturePosition(float x, float y, int pageIndex) {
            this.x = x;
            this.y = y;
            this.pageIndex = pageIndex;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public int getPageIndex() {
            return pageIndex;
        }
    }
}
