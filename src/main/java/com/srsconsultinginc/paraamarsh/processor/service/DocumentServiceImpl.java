package com.srsconsultinginc.paraamarsh.processor.service;

import com.srsconsultinginc.paraamarsh.processor.common.Constants;
import com.srsconsultinginc.paraamarsh.processor.config.ApplicationProperties;
import com.srsconsultinginc.paraamarsh.processor.repository.DocumentRepository;
import com.srsconsultinginc.paraamarsh.processor.service.dto.Document;
import com.srsconsultinginc.paraamarsh.processor.web.rest.errors.BusinessErrorResponseHandler;
import com.srsconsultinginc.paraamarsh.processor.web.rest.util.DownloadHealper;
import com.srsconsultinginc.paraamarsh.processor.web.rest.util.HttpClientUtil;
import com.srsconsultinginc.paraamarsh.processor.web.rest.util.ServiceResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;


@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    DownloadHealper downloadHealper;

    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    HttpClientUtil clientUtil;

    @Autowired
    NotifierService notifierService;

    @Autowired
    UploadDocuments uploadDocuments;

    @Autowired
    SaveDocumentHelper saveDocumentHelper;

    /**
     * @return will be map containing resource type of ByteArrayResource,content type and status
     * @summary : This method id used to download the document based on elastic search Id
     * @version 1
     * @author SRS Business Solutions
     */
    @Override
    public ServiceResponse downloadDocument(String esid) throws BusinessErrorResponseHandler {
        log.info("Getting document details for download document by id {} ", esid);
        Optional<Document> document = documentRepository.findByIndexId(esid);
        if (document.isPresent())
            return downloadHealper.downloadDocument(document.get());
        else
            return noContentResponse();
    }

    private ServiceResponse noContentResponse() {
        ServiceResponse result = new ServiceResponse();
        log.info("constructing  response for no data ");
        result.setBusinessCode(HttpStatus.NOT_FOUND.value());
        result.setPayload("No document find");
        return result;
    }

    /**
     * @return send the html content based on indexId
     * @summary : This method id used to download the document based on elastic search Id
     * @version 1
     * @author SRS Business Solutions
     */
    @Override
    public ServiceResponse getDocumentData(String indexId) throws JSONException {
        ServiceResponse<String> response = new ServiceResponse();
        Optional<Document> document = documentRepository.findByIndexId(indexId);
        if (document.isPresent()) {
            String data = document.get().getHtmlContent();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.DATA.getValue(), new JSONObject().put(Constants.TEXT.getValue(), data));
            response.setBusinessCode(HttpStatus.OK.value());
            response.setPayload(jsonObject.toString());
        } else {
            response.setBusinessCode(HttpStatus.NO_CONTENT.value());
            response.setPayload("No content");
        }
        return response;
    }

    /**
     * @summary : pushing document details to ES and updating partial detail(esid, htmlContent ...) in mysql
     * {@inheritDoc}
     */
    @Override
    public ServiceResponse saveDocument(String inputdata) {
        ServiceResponse response = new ServiceResponse();
        saveDocumentHelper.saveDocument(inputdata, response);
        return response;
    }

    @Override
    public ServiceResponse uploadDocument(MultipartFile file, String username) throws IOException {
        ServiceResponse<String> result = uploadDocuments.uploadDocument(file, username);
        notifierService.sendNotificationToPostman(username, result.getExtraDetails(), result.getPayload());
        return result;
    }

}
