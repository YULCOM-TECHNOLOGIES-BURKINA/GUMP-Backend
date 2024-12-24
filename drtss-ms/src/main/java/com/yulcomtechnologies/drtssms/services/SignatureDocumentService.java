package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.SignataireCertificatDto;
import com.yulcomtechnologies.drtssms.dtos.SignatureLocationDto;
import com.yulcomtechnologies.drtssms.dtos.UserDto;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.entities.SignatureScanner;
import com.yulcomtechnologies.drtssms.entities.UtilisateursDrtss;
import com.yulcomtechnologies.drtssms.enums.FileStoragePath;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.repositories.*;
import com.yulcomtechnologies.sharedlibrary.enums.UserRole;
import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
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
import java.util.List;

@Service
@AllArgsConstructor
public class SignatureDocumentService {

    private UtilisateursDrtssRepository utilisateursDrtssRepository;
    private SignatureCertificatRepository signatureCertificatRepository;
    private SignatureScannerRepository signatureScannerRepository;
    private FileRepository fileRepository;
    private DocumentRequestRepository documentRequestRepository;

    @Autowired
    private CertificateService certificateService;
    @Autowired
    private UsersFeignClient usersFeignClient;
    @Autowired
    private DocumentRequestService documentRequestService;

    /**
     *
     * @param page
     * @param size
     * @return
     */
    public Page<SignatureScanner> listSignatory(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return signatureScannerRepository.findAll(pageable);
    }

    public List<SignatureScanner> listSignatoryByRegion(String region)
    {
        return signatureScannerRepository.getSignatoryByRegion(region);
    }


    /**
     *
     * @param email
     * @return
     */
    public SignatureScanner getSignatoryByEmail(String email) {
        return signatureScannerRepository
                .getSignatoryByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No signatory found for email: " + email));
    }

    /**
     * Active / desactive signataire
     * @param id
     * @return
     */
    public SignatureScanner updateSignatoryStatus(Long id) {
         SignatureScanner signatory = signatureScannerRepository.findSignatureScannerByUserId(id)
                .orElseThrow(() -> new RuntimeException("Signatory with ID " + id + " not found"));

         signatory.getSignatureCertificat().setActif(!signatory.getSignatureCertificat().isActif());

         usersFeignClient.toglleUserSignatoryState(String.valueOf(id));

         return signatureScannerRepository.save(signatory);
    }


    /**
     * @param file
     * @param userId
     * @return
     */
    @Transactional
    public SignatureScanner createSignatory(MultipartFile file, Long userId) {
        String uploadPath = FileStoragePath.SCAN_SIGN_PATH.getPath();

        UserDto utilisateur = usersFeignClient.getUser(String.valueOf(userId));
        System.out.println(utilisateur.getEmail());

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
            signatureScan.setUser_id(utilisateur.getId());
            signatureScan.setEmail(utilisateur.getEmail());
            signatureScan.setRegion(utilisateur.getRegion());
            signatureScan.setRole(UserRole.valueOf(utilisateur.getRole()));
           //signatureScan.setDate_created(LocalDateTime.now());
            SignatureScanner signatureScanSave = signatureScannerRepository.save(signatureScan);

            SignataireCertificatDto certificatDTO = new SignataireCertificatDto();
            certificatDTO.setAlias(utilisateur.getForename() + '.' + utilisateur.getId());
            certificatDTO.setPassword("password");
            certificatDTO.setCommonName(utilisateur.getForename() + " " + utilisateur.getLastname());
            certificatDTO.setOrganization("DRTPS - BY:" + utilisateur.getEmail());
            certificatDTO.setOrganizationalUnit("Certificat GUMP-DRTPS - :" + utilisateur.getForename() + utilisateur.getLastname());
            certificatDTO.setCountry("BF");
            certificatDTO.setEmailAddress(utilisateur.getEmail());
            certificatDTO.setSignataire(signatureScanSave);

            certificateService.generateP12Certificate(certificatDTO);
            usersFeignClient.toglleUserSignatoryState(String.valueOf(userId));
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
    public ResponseEntity<byte[]> signAttestation(String attestationPath, Long signatoryId, File keyStoreFile, String keyStorePassword, String alias, float x, float y) {


        SignatureScanner signatory = signatureScannerRepository.findById(signatoryId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        UserDto userInfo = usersFeignClient.findUserByEmail(signatory.getEmail());
        SignatureScanner signatureInfo=signatureScannerRepository.findById(signatoryId).orElseThrow(() -> new IllegalArgumentException("Signataire info  non trouvé"));


        String _alias=signatureInfo.getSignatureCertificat().getAlias();

        if (!signatory.getSignatureCertificat().isActif()) {
            String jsonError = "{\"error\": \"Utilisateur inactif, signature non autorisée\"}";
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonError.getBytes());
        }

        try {
            // Charger le fichier d'attestation
            File attestationFile = loadFileByPath(Path.of(attestationPath));
            if (!attestationFile.exists() || !attestationFile.canRead()) {
                String jsonError = "{\"error\": \"Fichier d'attestation introuvable ou non lisible : " + attestationPath + "\"}";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonError.getBytes());
            }

            // Charger le Certifaicat  de signature du signataire
            File _keyStoreFile=getSignatoryCertificat(signatoryId);
           if (_keyStoreFile == null || !_keyStoreFile.exists()) {
                String jsonError = "{\"error\": \"Le Certificat de signature introuvable pour le signataire ID : " + signatoryId + "\"}";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonError.getBytes());
            }

            // Charger le fichier de signature du signataire
            File signatoryFileImg = getSignatoryFileImg(signatoryId);
            if (signatoryFileImg == null || !signatoryFileImg.exists()) {
                String jsonError = "{\"error\": \"Fichier de signature introuvable pour le signataire ID : " + signatoryId + "\"}";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonError.getBytes());
            }

            SignatureLocationDto signatureLocation = signatureLocation(attestationFile);
            if (signatureLocation == null) {
                String jsonError = "{\"error\": \"Emplacement de signature non trouvé dans le fichier PDF\"}";
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonError.getBytes());
            }

            certificateService.addSignatureImgToFile(
                    attestationFile, signatoryFileImg, x, y,
                    signatureLocation.getWidth(), signatureLocation.getHeight(),
                    signatureLocation.getPageSelect(), userInfo.getForename() + " " + userInfo.getLastname(), userInfo.getTitre_honorifique()
                   // signatureLocation.getPageSelect(), signatory.getNom() + " " + signatory.getPrenom(), signatory.getTitre_honorifique()
            );


            // Certifier le document avec le certificat du signataire
            certificateService.certifyThedocument(attestationFile, _keyStoreFile, keyStorePassword, alias);

            try (FileInputStream fis = new FileInputStream(attestationFile)) {
                byte[] data = fis.readAllBytes();
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attestationFile.getName() + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(data);
            }

        } catch (IOException e) {
            String jsonError = "{\"error\": \"Erreur lors du chargement du fichier d'attestation : " + e.getMessage() + "\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonError.getBytes());
        } catch (Exception e) {
            String jsonError = "{\"error\": \"Erreur lors de la signature de l'attestation : " + e.getMessage() + "\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonError.getBytes());
        }
    }



    @Transactional
    public ResponseEntity<byte[]> signAttestation2(String attestationPath, Long signatoryId, Long id) {
        try {
            // Charger la demande
            DocumentRequest requestInfo = documentRequestRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

            // Charger le signataire
            SignatureScanner signatory = signatureScannerRepository.findSignatureScannerByUserId(signatoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            UserDto userInfo = usersFeignClient.findUserByEmail(signatory.getEmail());

            if (!signatory.getSignatureCertificat().isActif()) {
                String jsonError = "{\"error\": \"Utilisateur inactif, signature non autorisée\"}";
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonError.getBytes());
            }

             Path basePath = Paths.get(System.getProperty("user.dir"));
            Path absoluteAttestationPath = basePath.resolve(attestationPath);
            File attestationFile = absoluteAttestationPath.toFile();

            if (!attestationFile.exists() || !attestationFile.canRead()) {
                String jsonError = "{\"error\": \"Fichier d'attestation introuvable ou non lisible : " + absoluteAttestationPath + "\"}";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonError.getBytes());
            }

             File signatoryFileImg = getSignatoryFileImg(signatoryId);
            if (signatoryFileImg == null || !signatoryFileImg.exists()) {
                String jsonError = "{\"error\": \"Fichier de signature introuvable pour le signataire ID : " + signatoryId + "\"}";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonError.getBytes());
            }

            SignatureLocationDto signatureLocation = signatureLocation(attestationFile);
            if (signatureLocation == null) {
                String jsonError = "{\"error\": \"Emplacement de signature non trouvé dans le fichier PDF\"}";
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonError.getBytes());
            }

             certificateService.addSignatureImgToFile(
                    attestationFile, signatoryFileImg, 70, 85,
                    signatureLocation.getWidth(), signatureLocation.getHeight(),
                    signatureLocation.getPageSelect(), userInfo.getForename() + " " + userInfo.getLastname(), userInfo.getTitre_honorifique()
            );

             String relativeCertPath = signatory.getSignatureCertificat().getCheminCertificat();
            Path absoluteCertPath = basePath.resolve(relativeCertPath);
            File keyStoreFile = absoluteCertPath.toFile();

            if (!keyStoreFile.exists() || !keyStoreFile.canRead()) {
                String jsonError = "{\"error\": \"Fichier de certificat introuvable ou non lisible : " + absoluteCertPath + "\"}";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonError.getBytes());
            }

             certificateService.certifyThedocument(
                    attestationFile,
                    keyStoreFile,
                    "password",
                    signatory.getSignatureCertificat().getAlias()
            );

             documentRequestService.signedDocumentRequest(id, signatory.getEmail());

             try (FileInputStream fis = new FileInputStream(attestationFile)) {
                byte[] data = fis.readAllBytes();
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attestationFile.getName() + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(data);
            }

        } catch (IOException e) {
            String jsonError = "{\"error\": \"Erreur lors du chargement du fichier d'attestation : " + e.getMessage() + "\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonError.getBytes());
        } catch (Exception e) {
            String jsonError = "{\"error\": \"Erreur lors de la signature de l'attestation : " + e.getMessage() + "\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonError.getBytes());
        }
    }


    @Transactional
    public ResponseEntity<String> signAttestation3(String attestationPath, Long signatoryId, Long id, File keyStoreFile) {
        try {
            // Charger la demande
            DocumentRequest requestInfo = documentRequestRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));

            // Charger le signataire
            SignatureScanner signatory = signatureScannerRepository.findSignatureScannerByUserId(signatoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            UserDto userInfo = usersFeignClient.findUserByEmail(signatory.getEmail());

            if (!signatory.getSignatureCertificat().isActif()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\": \"Utilisateur inactif, signature non autorisée\"}");
            }

            Path basePath = Paths.get(System.getProperty("user.dir"));
            Path absoluteAttestationPath = basePath.resolve(attestationPath);
            File attestationFile = absoluteAttestationPath.toFile();

            if (!attestationFile.exists() || !attestationFile.canRead()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\": \"Fichier d'attestation introuvable ou non lisible : " + absoluteAttestationPath + "\"}");
            }

            File signatoryFileImg = getSignatoryFileImg(signatoryId);
            if (signatoryFileImg == null || !signatoryFileImg.exists()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\": \"Fichier de signature introuvable pour le signataire ID : " + signatoryId + "\"}");
            }

            SignatureLocationDto signatureLocation = signatureLocation(attestationFile);
            if (signatureLocation == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\": \"Emplacement de signature non trouvé dans le fichier PDF\"}");
            }

            certificateService.addSignatureImgToFile(
                    attestationFile, signatoryFileImg, 70, 85,
                    signatureLocation.getWidth(), signatureLocation.getHeight(),
                    signatureLocation.getPageSelect(), userInfo.getForename() + " " + userInfo.getLastname(), userInfo.getTitre_honorifique()
            );

            if (!keyStoreFile.exists() || !keyStoreFile.canRead()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\": \"Fichier de certificat introuvable ou non lisible : " + keyStoreFile.getAbsolutePath() + "\"}");
            }

            certificateService.certifyThedocument(
                    attestationFile,
                    keyStoreFile,
                    "password",
                    signatory.getSignatureCertificat().getAlias()
            );

            documentRequestService.signedDocumentRequest(id, signatory.getEmail());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\": \"L'attestation a été signée avec succès.\"}");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"Erreur lors du chargement du fichier d'attestation : " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\": \"Erreur lors de la signature de l'attestation : " + e.getMessage() + "\"}");
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
     *
     * @param signataireId
     * @return
     * @throws IOException
     */
    public    File  getSignatoryCertificat( Long signataireId) throws IOException {

        SignatureScanner signatureInfo=signatureScannerRepository.findById(signataireId).get();
        String certificatPath=signatureInfo.getSignatureCertificat().getCheminCertificat();

      //  final String FILE_DIRECTORY =  FileStoragePath.SCAN_SIGN_PATH.getPath();
        String filePath =certificatPath;

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

