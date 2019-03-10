package com.srsconsultinginc.paraamarsh.processor.service;

import com.srsconsultinginc.paraamarsh.processor.web.rest.errors.BusinessErrorResponseHandler;
import com.srsconsultinginc.paraamarsh.processor.web.rest.util.ServiceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;

/**

 * The class is just a abstract layer of service class

 * output : -  always hashmap

 * @version 1

 * @author SRS Business Soultions

 */
public interface DocumentService {
//    HashMap<String,Object> downloadDocument(String esid) throws SQLException;
    ServiceResponse downloadDocument(String esid) throws BusinessErrorResponseHandler;

    ServiceResponse getDocumentData(String indexId) throws BusinessErrorResponseHandler;

    ServiceResponse saveDocument(String inputdata) throws BusinessErrorResponseHandler;

    ServiceResponse uploadDocument(MultipartFile file, String username) throws IOException, SQLException;
}
