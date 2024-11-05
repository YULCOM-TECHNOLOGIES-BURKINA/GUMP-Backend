package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.SignataireCertificatDto;
import com.yulcomtechnologies.drtssms.enums.FileStoragePath;
import com.yulcomtechnologies.drtssms.repositories.SignatureCertificatRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
@AllArgsConstructor
@Getter
@Setter
@Transactional
public class CertificateService {

    private SignatureCertificatRepository signataireCertificatRepository;

    public byte[] generateP12Certificate(SignataireCertificatDto certificateDTO) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();

        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, certificateDTO.commonName);
        builder.addRDN(BCStyle.O, certificateDTO.organization);
        builder.addRDN(BCStyle.OU, certificateDTO.organizationalUnit);
        builder.addRDN(BCStyle.C, certificateDTO.country);
        builder.addRDN(BCStyle.EmailAddress, certificateDTO.emailAddress);

        X500Name subjectName = builder.build();
        X500Name issuerName = subjectName;

        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
        Date notAfter = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 90);  //3 mois de Validite de l'attestation

        JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder("SHA256withRSA");
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(issuerName, serial, notBefore, notAfter, subjectName, keyPair.getPublic());

        X509CertificateHolder certHolder = certBuilder.build(signerBuilder.build(privateKey));
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certHolder);

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry(certificateDTO.alias, privateKey, certificateDTO.password.toCharArray(), new Certificate[]{cert});

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        keyStore.store(baos, certificateDTO.password.toCharArray());

        final String FILE_DIRECTORY =  FileStoragePath.CERTIFICAT_PATH.getPath();


        Path directory = Paths.get(FILE_DIRECTORY);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        Path outputPath = Paths.get(FILE_DIRECTORY, certificateDTO.alias + ".p12");
        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
            fos.write(baos.toByteArray());
        }
        certificateDTO.setCertificatFile(certificateDTO.alias + ".p12");
        certificateDTO.setCheminCertificat(String.valueOf(outputPath));
        signataireCertificatRepository.save(SignataireCertificatDto.toSignataireCertificatEntity(certificateDTO));
        return baos.toByteArray();
    }

}
