package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.SignataireCertificatDto;
import com.yulcomtechnologies.drtssms.dtos.SignatureLocationDto;
import com.yulcomtechnologies.drtssms.entities.SignatureScanner;
import com.yulcomtechnologies.drtssms.entities.UtilisateursDrtss;
import com.yulcomtechnologies.drtssms.enums.FileStoragePath;
import com.yulcomtechnologies.drtssms.repositories.SignatureCertificatRepository;
import com.yulcomtechnologies.drtssms.repositories.SignatureScannerRepository;
import com.yulcomtechnologies.drtssms.repositories.UtilisateursDrtssRepository;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class SignatureDocumentService {

    private UtilisateursDrtssRepository utilisateursDrtssRepository;
    private SignatureCertificatRepository signatureCertificatRepository;
    private SignatureScannerRepository signatureScannerRepository;

    @Autowired
    private CertificateService certificateService;

    /**
     *
     * @param page
     * @param size
     * @return
     */
    public Page<SignatureScanner> listSignatory(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return signatureScannerRepository.findAll(pageable);
    }


    /**
     * @param file
     * @param userId
     * @return
     */
    @Transactional
    public SignatureScanner createSignatory(MultipartFile file, Long userId) {
        String uploadPath = FileStoragePath.SCAN_SIGN_PATH.getPath();

        UtilisateursDrtss utilisateur = utilisateursDrtssRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + userId));
        try {
            Path directory = Paths.get(uploadPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = directory.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            SignatureScanner signatureScan = new SignatureScanner();
            signatureScan.setCheminImage(fileName);
            signatureScan.setUtilisateur(utilisateur);
            //signatureScan.setDate_created(LocalDateTime.now());
            SignatureScanner signatureScanSave = signatureScannerRepository.save(signatureScan);

            SignataireCertificatDto certificatDTO = new SignataireCertificatDto();
            certificatDTO.setAlias(utilisateur.getPrenom() + '.' + utilisateur.getId());
            certificatDTO.setPassword("password");
            certificatDTO.setCommonName(utilisateur.getNom() + " " + utilisateur.getPrenom());
            certificatDTO.setOrganization("DRTPS - BY:" + utilisateur.getEmail());
            certificatDTO.setOrganizationalUnit("Certificat GUMP-DRTPS - :" + utilisateur.getNom() + utilisateur.getPrenom());
            certificatDTO.setCountry("BF");
            certificatDTO.setEmailAddress(utilisateur.getEmail());
            certificatDTO.setSignataire(signatureScanSave);

            certificateService.generateP12Certificate(certificatDTO);

            return signatureScanSave;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du fichier : " + e.getMessage(), e);
        } catch (DataAccessException e) {

            throw new RuntimeException("Erreur lors de l'enregistrement en base de données : " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Une erreur inattendue s'est produite : " + e.getMessage(), e);
        }
    }


    /**
     * Signature electronique du document
     * @param attestationPath
     * @param signatoryId
     * @param keyStoreFile
     * @param keyStorePassword
     * @param alias
     * @param x
     * @param y
     * @return
     */
    @Transactional
    public ResponseEntity<byte[]> signAttestation(String attestationPath, Long signatoryId, File keyStoreFile, String keyStorePassword, String alias,float x,float y) {

         UtilisateursDrtss signatory = utilisateursDrtssRepository.findById(signatoryId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        try {
             File attestationFile = loadFileByPath(Path.of(attestationPath));
            if (!attestationFile.exists() || !attestationFile.canRead()) {
                throw new IllegalArgumentException("Fichier d'attestation introuvable ou non lisible : " + attestationPath);
            }

             File signatoryFileImg = getSignatoryFileImg(signatoryId);
            if (signatoryFileImg == null || !signatoryFileImg.exists()) {
                throw new IllegalArgumentException("Fichier de signature introuvable pour le signataire ID : " + signatoryId);
            }

            // Définir la localisation de la signature
            SignatureLocationDto signatureLocation = signatureLocation(attestationFile);
            if (signatureLocation == null) {
                throw new IllegalStateException("Emplacement de signature non trouvé dans le fichier PDF");
            }


            certificateService.addSignatureImgToFile(attestationFile, signatoryFileImg,
                    x, y,
                    signatureLocation.getWidth(), signatureLocation.getHeight(),
                    signatureLocation.getPageSelect(),signatory.getNom()+" "+signatory.getPrenom());

            // Certifier le document avec le certificat du signataire
            certificateService.certifyThedocument(attestationFile, keyStoreFile, keyStorePassword, alias);

             try (FileInputStream fis = new FileInputStream(attestationFile)) {
                byte[] data = fis.readAllBytes();
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attestationFile.getName() + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(data);
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur lors du chargement du fichier d'attestation : " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur lors de la signature de l'attestation : " + e.getMessage()).getBytes());
        }
    }


    /**
     * Telecharger un fichier par le file Path
     * @param filePath
     * @return
     * @throws IOException
     */
    public File loadFileByPath(Path filePath) throws IOException {
        Path path = Paths.get(filePath.toUri());
        if (!Files.exists(path) || !Files.isReadable(path)) {
            throw new RuntimeException("Fichier non trouvé ou non lisible : " + filePath);
        }
        return path.toFile();
    }

    /**
     *
     * @param signataireId
     * @return
     * @throws IOException
     */
    private   File  getSignatoryFileImg( Long signataireId) throws IOException {

        SignatureScanner signatureInfo=signatureScannerRepository.findById(signataireId).get();
        String fileName=signatureInfo.getCheminImage();


        final String FILE_DIRECTORY =  FileStoragePath.SCAN_SIGN_PATH.getPath();
        String filePath =FILE_DIRECTORY+'/'+signatureInfo.getCheminImage();

        // Charger le fichier
        File file = loadFileByPath(Path.of(filePath));
        Resource resource = new FileSystemResource(file);

         if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Fichier non trouvé ou non lisible : " + filePath);
        }

         String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

         HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mimeType));
        headers.setContentDispositionFormData("attachment", file.getName());

        return file ;
    }

    /**
     * Positionnement du fichier de signature
     * @param pdfFile
     * @return
     */
    private SignatureLocationDto signatureLocation(File pdfFile) {
        try (PDDocument document = PDDocument.load(pdfFile)) {
             PDFSignaturePositioner positioner = new PDFSignaturePositioner("Signature");
            positioner.findKeywordPosition(document);

             SignatureLocationDto signatureLocationDto = new SignatureLocationDto();
            signatureLocationDto.setXPosition(positioner.getXPosition());
            signatureLocationDto.setYPosition(positioner.getYPosition());
            signatureLocationDto.setWidth(125);
            signatureLocationDto.setHeight(50);
            signatureLocationDto.setPageSelect(positioner.getPageIndex());
            return signatureLocationDto;

        } catch (IOException e) {
            e.printStackTrace();
             return null;
        }
    }

}

