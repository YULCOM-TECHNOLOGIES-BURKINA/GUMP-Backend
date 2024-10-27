package com.yulcomtechnologies.tresorms.services;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

@Service
@AllArgsConstructor
public class TemplateProcessor {
    private final TemplateEngine templateEngine;

    public String fillVariables(String html, Map<String, Object> variables) {
        var context = new Context();

        for (Map.Entry<String, Object> entry: variables.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }

        return templateEngine.process(html, context);
    }

    public byte[] htmlToPdf(String html) throws IOException {
        var outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(new ByteArrayInputStream(html.getBytes()), outputStream);
        return outputStream.toByteArray();
    }
}
