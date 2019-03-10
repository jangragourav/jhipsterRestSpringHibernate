package com.srsconsultinginc.paraamarsh.processor.common;

public enum Constants {
    CONTENT_HYPHEN_LENGTH("Content-Length"),
    FILENAME("filename"),
    RESOURCE("resource"),
    BODY("body"),
    CONTENT_HYPHEN_DISPOSITION("Content-Disposition"),
    EXTENSION("extention"),
    TEXT("text"),
    HITS ("hits"),
    UNDESCORE_ID ("_id"),
    ID ("id"),
    METADATA ("metadata"),
    DATA ("data"),
    FILE_NAME ("file_name"),
    PATH ("path"),
    HTML ("html"),
    HTML_CONTENT ("htmlContent"),
    SNIPPET ("snippet"),
    SUMMARY ("summary"),
    FROM ("from"),
    TYPE ("type"),
    MESSAGE ("message"),
    USER_HYPHEN_ID ("user-id"),
    PROCESSOR ("processor"),
    TIME_STAMP ("timeStamp"),
    FILE ("file"),
    ANNOTATED_DATA ("annotatedData"),
    DOCUMENT_TEXT ("documentText"),
    APPLICATION_SLASH_JSON ("application/json"),
    STATUS("status");


    private String stringValue;

    Constants(String value){
        stringValue = value;
    }

    public String getValue(){
        return stringValue;
    }

}
