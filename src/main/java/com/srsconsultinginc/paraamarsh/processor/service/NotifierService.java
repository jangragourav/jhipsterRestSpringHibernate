package com.srsconsultinginc.paraamarsh.processor.service;

import com.srsconsultinginc.paraamarsh.processor.common.Constants;
import com.srsconsultinginc.paraamarsh.processor.config.ApplicationProperties;
import com.srsconsultinginc.paraamarsh.processor.web.rest.util.HttpClientUtil;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@Service
public class NotifierService {

    private static final Logger log = LoggerFactory.getLogger(NotifierService.class);
    @Autowired
    HttpClientUtil clientUtil;
    @Autowired
    ApplicationProperties applicationProperties;

    boolean sendNotificationToPostman(String userName, String type, String message) throws IOException {
        String absoluteURL = applicationProperties.getPostman().getNotificationUrl();
        return sendNotification(userName, type, message, absoluteURL);
    }

    boolean sendNotification(String userName, String type, String message, String absoluteURL) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.FROM.getValue(), Constants.PROCESSOR.getValue());
        jsonObject.put(Constants.TIME_STAMP.getValue(), Instant.now());
        jsonObject.put(Constants.TYPE.getValue(), type);
        jsonObject.put(Constants.MESSAGE.getValue(), message);
        jsonObject.put(Constants.USER_HYPHEN_ID.getValue(), userName);
        StringEntity entity = new StringEntity(jsonObject.toString());
        clientUtil.execute(absoluteURL, entity, Constants.APPLICATION_SLASH_JSON.getValue(), Constants.APPLICATION_SLASH_JSON.getValue());
        return true;
    }

}
