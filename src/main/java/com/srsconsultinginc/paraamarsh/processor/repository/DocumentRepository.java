package com.srsconsultinginc.paraamarsh.processor.repository;

import com.srsconsultinginc.paraamarsh.processor.service.dto.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

    //TODO need to refactor all the query like remove unwanted query use naming convention of JPA

    Optional<Document> findByIndexId(@Param("indexid") String indexid);

    @Modifying
    @Transactional
    @Query(value="update document set document_text = :documentText, htmlcontent=:html,snippet=:snippet where id = :id ",nativeQuery=true)
    int putDocumentDetails(@Param("id") String id, @Param("documentText")  String documentText,@Param("html") String html,@Param("snippet") String snippet);

    @Modifying
    @Transactional
    @Query(value = "insert into document(id, filename,dump,created_by,extension,mimetype) VALUES (:id,:filename,:blob,:username,:extention,:mimetype)", nativeQuery = true)
    void saveDocumentData(@Param("blob") byte[] blob, @Param("extention") String extention,@Param("filename") String filename,@Param("id") String id,@Param("username") String username,@Param("mimetype") String mimetype);

}
