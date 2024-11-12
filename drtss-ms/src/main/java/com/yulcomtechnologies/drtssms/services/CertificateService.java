package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.SignataireCertificatDto;
import com.yulcomtechnologies.drtssms.enums.FileStoragePath;
import com.yulcomtechnologies.drtssms.repositories.SignatureCertificatRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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


    /**
     *
     * @param pdfFile
     * @param signatureImageFile
     * @param xPosition
     * @param yPosition
     * @param width
     * @param height
     * @param pageSigne
     */
    //Ajouter l'empreinte de la Signature
    public void addSignatureImgToFile(File pdfFile, File signatureImageFile, float xPosition, float yPosition, float width, float height, int pageSigne,String signatoryName ,String titleSignatory) {
        try (PDDocument document = PDDocument.load(pdfFile)) {

            PDPage page = document.getPage(pageSigne) ;
            BufferedImage bim = ImageIO.read(signatureImageFile);
            PDImageXObject pdImage = LosslessFactory.createFromImage(document, bim);
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);

            // Position text & Img
            contentStream.drawImage(pdImage, xPosition, yPosition, width, height);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            contentStream.newLineAtOffset(xPosition, yPosition-12);
            contentStream.showText(signatoryName.toUpperCase());

            if (titleSignatory!=null){
             contentStream.newLineAtOffset(xPosition, yPosition-14);
             contentStream.showText(titleSignatory);

            }

            contentStream.endText();

            contentStream.close();
             document.save(pdfFile);

        } catch (IOException e) {
            throw new RuntimeException("Echec de l'ajout de la signature au PDF", e);
        }
    }



    /**
     *Certifier le document
     * @param pdfFile
     * @param keyStoreFile
     * @param keyStorePassword
     * @param alias
     * @throws Exception
     */
    public void certifyThedocument(File pdfFile, File keyStoreFile, String keyStorePassword, String alias) throws Exception {
        // Charger le KeyStore et obtenir la clé privée et le certificat
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        PrivateKey privateKey;
        Certificate[] certificateChain;

        try (InputStream keyStoreStream = new FileInputStream(keyStoreFile)) {
            keyStore.load(keyStoreStream, keyStorePassword.toCharArray());
            privateKey = (PrivateKey) keyStore.getKey(alias, keyStorePassword.toCharArray());
            certificateChain = keyStore.getCertificateChain(alias);
        }

        if (privateKey == null || certificateChain == null) {
            throw new IllegalArgumentException("Clé privée ou chaîne de certificats introuvable pour l'alias spécifié.");
        }

        // Charger le document PDF
        try (PDDocument document = PDDocument.load(pdfFile)) {
            // Initialiser la signature PDF
            PDSignature pdSignature = createPDSignature("TEST", "Certifier par le Guichet unique des marchés publics",
                    "Validation & Certification du document, GUMP");

            // Ajouter la signature
            SignatureOptions signatureOptions = new SignatureOptions();
            signatureOptions.setPreferredSignatureSize(SignatureOptions.DEFAULT_SIGNATURE_SIZE);
            document.addSignature(pdSignature, createSignatureInterface(privateKey, certificateChain), signatureOptions);

            // Enregistrer le document signé
            try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
                document.saveIncremental(outputStream);
            }
        }
    }

    private PDSignature createPDSignature(String signerName, String location, String reason) {
        PDSignature pdSignature = new PDSignature();
        pdSignature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        pdSignature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        pdSignature.setName(signerName);
        pdSignature.setLocation(location);
        pdSignature.setReason(reason);
        pdSignature.setSignDate(Calendar.getInstance());
        return pdSignature;
    }

    private SignatureInterface createSignatureInterface(PrivateKey privateKey, Certificate[] certificateChain) {
        return content -> {
            try {
                CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
                List<Certificate> certList = Arrays.asList(certificateChain);
                JcaCertStore certs = new JcaCertStore(certList);
                gen.addCertificates(certs);

                ContentSigner sha384Signer = new JcaContentSignerBuilder("SHA384withRSA").build(privateKey);
                gen.addSignerInfoGenerator(
                        new JcaSignerInfoGeneratorBuilder(
                                new JcaDigestCalculatorProviderBuilder().build())
                                .build(sha384Signer, (X509Certificate) certificateChain[0])
                );

                CMSProcessableByteArray msg = new CMSProcessableByteArray(content.readAllBytes());
                CMSSignedData signedData = gen.generate(msg, false);
                return signedData.getEncoded();
            } catch (Exception e) {
                throw new IOException("Erreur lors de la génération de la signature numérique.", e);
            }
        };
    }

}
