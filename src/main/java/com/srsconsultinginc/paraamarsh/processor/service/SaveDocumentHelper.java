package com.srsconsultinginc.paraamarsh.processor.service;

import com.srsconsultinginc.paraamarsh.processor.common.Constants;
import com.srsconsultinginc.paraamarsh.processor.config.ApplicationProperties;
import com.srsconsultinginc.paraamarsh.processor.repository.DocumentRepository;
import com.srsconsultinginc.paraamarsh.processor.service.dto.Document;
import com.srsconsultinginc.paraamarsh.processor.web.rest.errors.BusinessErrorResponseHandler;
import com.srsconsultinginc.paraamarsh.processor.web.rest.util.HttpClientUtil;
import com.srsconsultinginc.paraamarsh.processor.web.rest.util.ServiceResponse;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.zalando.problem.Status;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Service
public class SaveDocumentHelper {

    private static final Logger log = LoggerFactory.getLogger(SaveDocumentHelper.class);

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    HttpClientUtil clientUtil;

    @Autowired
    ApplicationProperties applicationProperties;


    public void saveDocument(String inputdata, ServiceResponse response) {
        JSONObject inputJSON = new JSONObject(inputdata);
        JSONObject data = inputJSON.getJSONObject("data");
        String id = (String) data.get("id");
        Optional<Document> documentObj = documentRepository.findById(id);
        if(documentObj.isPresent()) {
            Document document = documentObj.get();
            if(document.getIndexId().isEmpty()) {
                log.info("Adding missing information to the user input to store in system");
                String responseJson = saveDocumentES(inputJSON, document, response);
                saveAndConstructResponse(response, inputJSON, document, responseJson);
            }else {
                response.setBusinessCode(HttpStatus.CONFLICT.value());
                response.setPayload("{document is already indexed}");
            }
        }
    }

    /**
     * @return send status based on the sucess or failure
     * @throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException
     * @summary : saving details in elastic search
     * parent:{@link #saveDocument(String, ServiceResponse) saveDocument}
     * @version 1
     * @author SRS Business Solutions
     */
    private String saveDocumentES(JSONObject inputJSON, Document documentObj, ServiceResponse response) {
        JSONObject getdata = addMissingDetails(inputJSON.getJSONObject("data"), documentObj, response);
        getdata.put(Constants.TEXT.getValue(), inputJSON.get(Constants.DOCUMENT_TEXT.getValue()));
        StringEntity entity = null;
        try {
            entity = new StringEntity(getdata.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("SaveDocumentHelper:saveDocumentES:UnsupportedEncodingException Error while converting data to StringEntity ", e);
            throw new BusinessErrorResponseHandler("Error while converting data to StringEntity", Status.INTERNAL_SERVER_ERROR);
        }
        log.info("Got the entity {}", entity);
        ApplicationProperties.Elasticsearch esproperty = applicationProperties.getElasticsearch();
        return clientUtil.execute(esproperty.getAddDocumentURL(), entity, Constants.APPLICATION_SLASH_JSON.getValue(), Constants.APPLICATION_SLASH_JSON.getValue());
    }

    /**
     * @return send same jsonObject(data) by adding few other details Using Enum If we want to add other details than
     * just add values for in enum({DocumentRemField})
     * @summary : saving details in elastic search
     * parent: {@link #saveDocumentES(JSONObject, Document, ServiceResponse) saveDocumentES}
     * @version 1
     * @author SRS Business Solutions
     */
    private JSONObject addMissingDetails(JSONObject data, Document documentObj, ServiceResponse response) {
        for (DocumentRemField column : DocumentRemField.values()) {
            if (!data.has(column.toString())) {
                String value = null;
                try {
                    value = String.valueOf(documentObj.getClass().getMethod(column.getValue()).invoke(documentObj));
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    log.error("SaveDocumentHelper:addMissingDetails Error occured while mapping missing details to the document object", e);
                    throw new BusinessErrorResponseHandler("Error occured while mapping missing details to the document object", Status.INTERNAL_SERVER_ERROR);
                }
                data.put(column.name(), value);
            }
        }
        return data;
    }

    /**
     * @return constructing response object based on elasticsearch response
     * @summary : updating partial details data of document in mysql bases in on ID and constructing response
     * parent: {@link #saveDocument(String, ServiceResponse) saveDocument}
     * @version 1
     * @author SRS Business Solutions
     */
    private void saveAndConstructResponse(ServiceResponse response, JSONObject inputJSON, Document documentInfo, String responseJson) {
        if (responseJson != null && (new JSONObject(responseJson).has(Constants.UNDESCORE_ID.getValue())
            && ((new JSONObject(responseJson).get(Constants.UNDESCORE_ID.getValue())).toString()) != null)) {

            String esid = new JSONObject(responseJson).get(Constants.UNDESCORE_ID.getValue()).toString();
            documentInfo.setIndexId(esid);
            updateDocumentDetails(documentInfo, inputJSON);
            response.setBusinessCode(HttpStatus.CREATED.value());
        } else if (responseJson == null) {
            response.setBusinessCode(HttpStatus.CONFLICT.value());
        }
    }

    /**
     * @return NA
     * @summary : updating partial details data of document in mysql bases in on ID
     * parent: {@link #saveAndConstructResponse(ServiceResponse, JSONObject, Document, String) saveAndConstructResponse}
     * @version 1
     * @author SRS Business Solutions
     */
    private void updateDocumentDetails(Document documentInfo, JSONObject inputJSON) {
        JSONObject data = inputJSON.getJSONObject("data");
        documentInfo.setIndexedContent((String) data.get(Constants.ANNOTATED_DATA.getValue()));
        documentInfo.setFileName((String) data.get(Constants.FILENAME.getValue()));
        JSONObject metadata = inputJSON.getJSONObject(Constants.METADATA.getValue());
        documentInfo.setHtmlContent((String) metadata.get(Constants.HTML_CONTENT.getValue()));
        documentInfo.setHtmlContent((String) metadata.get(Constants.SUMMARY.getValue()));
        documentRepository.save(documentInfo);
    }
}

enum DocumentRemField {
    summary("getSummary"),
    filename("getFileName"),
    extention("getExtension"),
    username("getCreated_by"),
    id("getId");
    private String stringValue;

    DocumentRemField(String value) {
        stringValue = value;
    }

    public String getValue() {
        return stringValue;
    }
}
