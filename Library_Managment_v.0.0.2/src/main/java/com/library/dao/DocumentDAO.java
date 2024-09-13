package com.library.dao;

import com.library.model.document.Document;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentDAO {
    void addDocument(Document document);
    void updateDocument(Document document);
    void deleteDocument(UUID documentId);
    Optional<Document> getDocumentById(UUID documentId);
    Optional<Document> getDocumentByTitle(String title);
    List<Document> getAllDocuments();
    List<Document> searchDocuments(String searchTerm);
}
