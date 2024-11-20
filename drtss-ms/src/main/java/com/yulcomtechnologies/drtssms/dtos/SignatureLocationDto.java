package com.yulcomtechnologies.drtssms.dtos;

import lombok.*;

import java.io.File;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignatureLocationDto {
    private File pdfFile;
    private File signatureImageFile;
    private String qrCodeText;
    private String textSignature;
    private float xPosition;
    private float yPosition;
    private float width;
    private float height;
    private int pageSelect;

}
