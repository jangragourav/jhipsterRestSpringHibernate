package com.srsconsultinginc.paraamarsh.processor.service;

import com.srsconsultinginc.paraamarsh.processor.common.Constants;
import com.srsconsultinginc.paraamarsh.processor.config.ApplicationProperties;
import com.srsconsultinginc.paraamarsh.processor.repository.DocumentRepository;
import com.srsconsultinginc.paraamarsh.processor.service.dto.Document;
import com.srsconsultinginc.paraamarsh.processor.web.rest.util.ServiceResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Function;

@Service
public class UploadDocuments {

    private static final Logger log = LoggerFactory.getLogger(UploadDocuments.class);

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    NotifierService notifierService;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ApplicationProperties applicationProperties;


    public ServiceResponse uploadDocument(MultipartFile file, String username) throws IOException {
        ServiceResponse<String> result = new ServiceResponse();
        if (!file.isEmpty()) {
            String filename = file.getOriginalFilename();
            String id = getSHA.apply(file.getBytes());
            Optional<Document> documentdata = documentRepository.findById(id);
            if (documentdata.isPresent()) {
                result.setPayload("The Document " + filename + " is already upload ");
                result.setBusinessCode(HttpStatus.CONFLICT.value());
                result.setExtraDetails("WARNING");
            } else {
                uploadDocumentOnSuccess(file, username, result, filename, id);
            }
        } else {
            result.setPayload("You failed to upload " + file.getOriginalFilename() + " because the file was empty");
            result.setExtraDetails("ERROR");
            result.setBusinessCode(HttpStatus.NO_CONTENT.value());
        }
        return result;
    }


    private void uploadDocumentOnSuccess(MultipartFile file, String username, ServiceResponse result, String filename, String id) {
        Transaction transaction = null;
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        try (
            Session session = sessionFactory.openSession();
        ) {
            transaction = session.beginTransaction();
            saveDocumentDB_NFS(file, username, filename, id);
            transaction.commit();
            result.setPayload("Upload of document \" + filename + \" has been done successfully");
            result.setExtraDetails("INFO");

            result.setBusinessCode(HttpStatus.ACCEPTED.value());
        } catch (IOException e) {
            if (null != transaction) {
                transaction.rollback();
                result.setPayload("Oops something went wrong while uploading the file");
                result.setBusinessCode(HttpStatus.FAILED_DEPENDENCY.value());
                result.setExtraDetails("ERROR");

            }
        }

    }

    private void saveDocumentDB_NFS(MultipartFile file, String username, String filename, String id) throws IOException {
        String mimetype = file.getContentType();
        byte[] binaryblock = Base64.getEncoder().encode(file.getBytes());
        String extention = FilenameUtils.getExtension(filename);
        documentRepository.saveDocumentData(binaryblock, extention, filename, id, username, mimetype);
        log.info("File {} saved into db successfully will be saving into NFS ", filename);
        ApplicationProperties.Upload upload_path = applicationProperties.getUpload();
        String ret = String.valueOf(File.separatorChar);
        FileUtils.writeByteArrayToFile(new File(upload_path.getUploadpath() + ret + "[" + username + "]" + filename), file.getBytes());
        log.info("File uploaded successfully");
    }

    Function<byte[], String> getSHA = binaryblock -> {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(binaryblock);
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            log.info("Hash generated for the given Data");
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            log.error("Exception thrown for incorrect algorithm: {} ", e);
            return null;
        }
    };


}
