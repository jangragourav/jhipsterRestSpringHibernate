package com.srsconsultinginc.paraamarsh.processor.web.rest.util;

import com.srsconsultinginc.paraamarsh.processor.web.rest.errors.BusinessErrorResponseHandler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zalando.problem.Status;

import java.io.IOException;

@Component
public class HttpClientUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    public String execute(String Uri, org.apache.http.HttpEntity entity, String acceptType, String contentType) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(Uri);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", acceptType);
        httpPost.setHeader("Content-type", contentType);
        String response = null;

        try {
            HttpResponse httpresponse = client.execute(httpPost);
            HttpEntity resEntity = httpresponse.getEntity();
            if (resEntity != null) {
                response = EntityUtils.toString(resEntity);
                LOGGER.debug(" got status code {} for {} ", httpresponse.getStatusLine().getStatusCode(), Uri);
            }
        } catch (ClientProtocolException e) {
            LOGGER.error("HttpClientUtil:execute:ClientProtocolException Converting response entity to string ",e);
            throw new BusinessErrorResponseHandler("Converting response entity to string", Status.INTERNAL_SERVER_ERROR);
        }catch (IOException e) {
            LOGGER.error("HttpClientUtil:execute:IOException Error while indexing " ,e);
            throw new BusinessErrorResponseHandler("Converting response entity to string", Status.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
