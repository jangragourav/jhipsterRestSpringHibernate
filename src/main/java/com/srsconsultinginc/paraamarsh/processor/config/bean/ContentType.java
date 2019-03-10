package com.srsconsultinginc.paraamarsh.processor.config.bean;


import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentType {
    private static final Logger log = LoggerFactory.getLogger(ContentType.class);
    Properties prop;
    public ContentType() throws IOException {
        prop = new Properties();
        log.info("properties file loaded...!");
        prop.load(getClass().getClassLoader().getResourceAsStream("contenttype.properties"));
    }
    public String getcontentType(String extention) {
        log.info("Geting Content Type Value based on extention of resume");
        return prop.getProperty(extention);
    }
}
