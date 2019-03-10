package com.srsconsultinginc.paraamarsh.processor.service.dto;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.net.UnknownHostException;
import java.sql.Blob;
@Entity(name = "Document")
@Table(name = "document")
@AuditTable("document")
public class Document {

    @Id
    @NotNull
    private String id;

    @Column(name = "indexid")
    private String indexId;

    @NotNull
    @Column(name = "filename")
    private String fileName;

    @NotNull
    @Column(name = "extension")
    private String extension;

    @NotNull
    @Column(name = "dump")
    private Blob dump;

    @NotNull
    @Column(name = "mimetype")
    private String mimetype;

    // Attribute will be there
    @Column(name = "indexedcontent")
    @Lob
    private String indexedContent;


    @Column(name = "htmlcontent")
    private String htmlContent;

    @Column(name = "summary")
    private String summary;

    @NotNull
    @Column(name = "created_by")
    private String created_by;

    @Column(name = "modified_by")
    private String modified_by;


    @Column(name = "created_on")
    private String created_on;

    @Column(name = "modified_on")
    private String modified_on;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Blob getDump() {
        return dump;
    }

    public void setDump(Blob dump) {
        this.dump = dump;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getIndexedContent() {
        return indexedContent;
    }

    public void setIndexedContent(String indexedContent) {
        this.indexedContent = indexedContent;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getModified_on() {
        return modified_on;
    }

    public void setModified_on(String modified_on) {
        this.modified_on = modified_on;
    }
}
