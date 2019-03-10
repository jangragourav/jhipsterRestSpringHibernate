package com.srsconsultinginc.paraamarsh.processor.web.rest.util;

import com.srsconsultinginc.paraamarsh.processor.common.Constants;
import com.srsconsultinginc.paraamarsh.processor.config.bean.ContentType;
import com.srsconsultinginc.paraamarsh.processor.service.DocumentServiceImpl;
import com.srsconsultinginc.paraamarsh.processor.service.dto.Document;
import com.srsconsultinginc.paraamarsh.processor.web.rest.errors.BusinessErrorResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.zalando.problem.Status;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class DownloadHealper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    ContentType contentType;

    byte[] byteData = new byte[0];

    public ServiceResponse downloadDocument(Document document) throws BusinessErrorResponseHandler{
        ServiceResponse<Map<String,Object>> result = new ServiceResponse();
        HashMap payload =  new HashMap();
        payload.put(Constants.FILENAME.getValue(), document.getFileName());
        LOGGER.info("Collecting details to download document for file {} ", document.getFileName());
        Blob blob = document.getDump();
        payload.put(Constants.EXTENSION.getValue(), contentType.getcontentType(document.getExtension()));
        ByteArrayResource resource = convertBlob_ByteResource(blob,result);
        LOGGER.info("Got the byte data for requested document for extension {} ", document.getExtension());
        payload.put(Constants.RESOURCE.getValue(), resource);
        payload.put(Constants.CONTENT_HYPHEN_LENGTH.getValue(), byteData.length);
        payload.put(Constants.STATUS.getValue(), HttpStatus.OK);
        return result;
    }

    private ByteArrayResource convertBlob_ByteResource(Blob blob,ServiceResponse result) throws BusinessErrorResponseHandler{
        ByteArrayResource resource = null;
        try {
            byteData = Base64.getDecoder().decode(blob.getBytes(1, (int) blob.length()));
            resource = new ByteArrayResource(byteData);
        } catch (SQLException e) {
            LOGGER.error("DownloadHealper:convertBlob_ByteResource error while decoding blob" ,e);
            result.setPayload("can not process the request");
            result.setBusinessCode(Status.EXPECTATION_FAILED.getStatusCode());
            throw new BusinessErrorResponseHandler("Error while decoding the blob", Status.EXPECTATION_FAILED);
        }
        return resource;
    }
}
