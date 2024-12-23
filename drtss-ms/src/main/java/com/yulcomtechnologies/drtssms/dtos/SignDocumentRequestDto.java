package com.yulcomtechnologies.drtssms.dtos;

import com.yulcomtechnologies.drtssms.entities.SignatureScanner;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignDocumentRequestDto {
     public Long id;
     public String alias;
     public String keyStorePassword;
     public Long signatoryId;
     private String attestationPath;
 }
