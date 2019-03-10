package com.srsconsultinginc.paraamarsh.processor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Processor.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    public final Elasticsearch es = new Elasticsearch();
    public Elasticsearch getElasticsearch() {
        return es;
    }

    public static class Elasticsearch {
        @Value("#{search-document-url}")
        private String searchDocumentURL = "http://localhost:9200/paraamarsh/_search";

        @Value("#{search-document-query}")
        private String searchDocumentQuery;

        @Value("#{search-facet-multimatch-query}")
        private String searchFacetMultiMatchQuery;

        @Value("#{search-facet-aggregation}")
        private String searchFacetAggregation;

        public String getSearchFacetMultiMatchQuery() {
            return searchFacetMultiMatchQuery;
        }

        public void setSearchFacetMultiMatchQuery(String searchFacetMultiMatchQuery) {
            this.searchFacetMultiMatchQuery = searchFacetMultiMatchQuery;
        }

        public String getSearchFacetAggregation() {
            return searchFacetAggregation;
        }

        public void setSearchFacetAggregation(String searchFacetAggregation) {
            this.searchFacetAggregation = searchFacetAggregation;
        }

        @Value("#{add-document-url}")
        private String addDocumentURL;

        public String getSearchDocumentURL() {
            return searchDocumentURL;
        }

        public void setSearchDocumentURL(String searchDocumentURL) {
            this.searchDocumentURL = searchDocumentURL;
        }

        public String getSearchDocumentQuery() {
            return searchDocumentQuery;
        }
        public void setSearchDocumentQuery(String searchDocumentQuery) {
            this.searchDocumentQuery = searchDocumentQuery;
        }

        public String getAddDocumentURL() {
            return addDocumentURL;
        }

        public void setAddDocumentURL(String addDocumentURL) {
            this.addDocumentURL = addDocumentURL;
        }

    }
    public final Upload up = new Upload();
    public Upload getUpload() {
        return up;
    }
    public static class Upload {
        @Value("#{upload-path}")
        private String uploadpath;
        public String getUploadpath() {
            return uploadpath;
        }
        public void setUploadpath(String uploadpath) {
            this.uploadpath = uploadpath;
        }
    }
    public final Postman postman = new Postman();
    public Postman getPostman() {
        return postman;
    }
    public static class Postman {
        @Value("#{notification-url}")
        private String notificationUrl;

        public String getNotificationUrl() {
            return notificationUrl;
        }

        public void setNotificationUrl(String notificationUrl) {
            this.notificationUrl = notificationUrl;
        }
    }
}
