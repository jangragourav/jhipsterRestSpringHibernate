package com.srsconsultinginc.paraamarsh.processor.config;

import com.srsconsultinginc.paraamarsh.processor.config.bean.ContentType;
import com.srsconsultinginc.paraamarsh.processor.service.DocumentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ApplicationConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);
    @Bean
    public ContentType getContentType() throws IOException {
        log.info("=========================Creating Content Type Bean =========================");
        return new ContentType();
    }
}
