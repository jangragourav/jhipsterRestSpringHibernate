package com.srsconsultinginc.paraamarsh.processor.web.rest;

import com.srsconsultinginc.paraamarsh.processor.common.Constants;
import com.srsconsultinginc.paraamarsh.processor.service.DocumentService;
import com.srsconsultinginc.paraamarsh.processor.web.rest.errors.BusinessErrorResponseHandler;
import com.srsconsultinginc.paraamarsh.processor.web.rest.util.ServiceResponse;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * The class helps crud for document contain upload into NFS, update , gethtml content , download document
 * <p>
 * output : -  always will be in form of json with status code(will vary based on API to API) and body (will vary based on API to API)
 *
 * @author SRS Business Soultions
 * @version 1
 */


//@Component
@RestController
@RequestMapping("processor/api/v1")
public class DocumentResource {

    private static final Logger log = LoggerFactory.getLogger(DocumentResource.class);

    @Autowired
    private DocumentService documentService;

    //TODO: user-id in header
    @Timed
    @ApiOperation(value = "Downloading document form system")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully Downloaded document"),
        @ApiResponse(code = 400, message = "Bad request. Please check you are sending uses-id request header and it must be string"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "There is some error while downloading it my be because of decoding blob data or ")
    })
    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public ResponseEntity downloadDocument(@PathVariable("id") String id, @RequestHeader(value = "user-id") String userId) {
        try {
            ServiceResponse response = documentService.downloadDocument(id);
            HashMap<String, Object> payload = (HashMap<String, Object>) response.getPayload();
            if (payload.containsKey(Constants.RESOURCE.getValue())) {
                return ResponseEntity.status(response.getBusinessCode()).header(Constants.CONTENT_HYPHEN_DISPOSITION.getValue(), "attachment; filename=" + payload.get(Constants.FILENAME.getValue()))
                    .header(Constants.CONTENT_HYPHEN_LENGTH.getValue(), String.valueOf(payload.get(Constants.CONTENT_HYPHEN_LENGTH.getValue())))
                    .header(HttpHeaders.CONTENT_TYPE, (String) payload.get(Constants.EXTENSION.getValue())).body(payload.get(Constants.RESOURCE.getValue()));
            } else {
                return ResponseEntity.status(response.getBusinessCode()).body(response.getPayload());
            }
        } catch (BusinessErrorResponseHandler businessErrorResponseHandler) {
            log.error("DocumentResource:downloadDocument:businessErrorResponseHandler  Failed during downloading document");
        } catch (Exception e) {
            log.error("DocumentResource:downloadDocument:Exception  Failed during downloading document ", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    @Timed
    @ApiOperation(value = "Get htmlContent for required document")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully Got the html content of document to display in UI"),
        @ApiResponse(code = 400, message = "Bad request. Please check you are sending uses-id request header and it must be string"),
        @ApiResponse(code = 500, message = "Resource is present but not able to get it")
    })
    @RequestMapping(value = "/documents/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getHTMLContent(@PathVariable(required = true, value = "id") String id, @RequestHeader(value = "user-id") String userId) {
        try {
            ServiceResponse response = documentService.getDocumentData(id);
            return ResponseEntity.status(response.getBusinessCode()).body(response.getPayload());
        } catch (BusinessErrorResponseHandler businessErrorResponseHandler) {
            log.error("DocumentResource:getHTMLContent:businessErrorResponseHandler  Failed during downloading document");
        } catch (Exception e) {
            log.error("DocumentResource:getHTMLContent:Exception  Failed during retrieving html data ", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @ApiOperation(value = "pushing document details to ES and updating in mysql")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Successfully saved the document in ES and mysql"),
        @ApiResponse(code = 400, message = "Bad request. Please check you are sending uses-id request header and it must be string"),
        @ApiResponse(code = 409, message = "CONFLICT. Please check the data it has been already indexed"),
        @ApiResponse(code = 500, message = "Thers is some error occured while updating the record")
    })
    @RequestMapping(value = "/document", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity updateDocument(@RequestBody String data, @RequestHeader(value = "user-id") String userId) {
        try {
            ServiceResponse response = documentService.saveDocument(data);
            return ResponseEntity.status(response.getBusinessCode()).body(response.getPayload());
        } catch (BusinessErrorResponseHandler businessErrorResponseHandler) {
            log.error("DocumentResource:updateDocument:businessErrorResponseHandler Failed during saving the document into the system  ");
        } catch (Exception e) {
            log.error("DocumentResource:updateDocument:Exception Failed during saving the document into the system ", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @Timed
    @ApiOperation(value = "Uploading document into NFS and partial details in mysql")
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Successfully saved the document in NFS"),
        @ApiResponse(code = 400, message = "Bad request. Please check you are sending uses-id request header and it must be string"),
        @ApiResponse(code = 409, message = "CONFLICT. Please check the uploading file It has been uploaded already"),
        @ApiResponse(code = 500, message = "There is some error while upload whether NFS is not available or file is corrupt")
    })
    @RequestMapping(value = "/documents", method = RequestMethod.POST)
    public ResponseEntity<?> uploadDocument(@RequestParam() MultipartFile file,
                                            @RequestHeader(value = "user-id") String username) {
        try {
            ServiceResponse response = documentService.uploadDocument(file, username);
            return ResponseEntity.status(response.getBusinessCode()).body(response.getPayload());
        }
        catch (BusinessErrorResponseHandler businessErrorResponseHandler) {
            log.error("DocumentResource:uploadDocument:IOException error while getting the byte from input file or while sending notification ");
        } catch (Exception e) {
            log.error("DocumentResource:updateDocumentDetails:Exception  Failed during retrieving html data {} ", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }
}
