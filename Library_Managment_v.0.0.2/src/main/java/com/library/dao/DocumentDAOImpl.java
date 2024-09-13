package com.library.dao;

import com.library.model.document.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DocumentDAOImpl implements DocumentDAO {
    private static DocumentDAOImpl instance;
    private final Connection connection;

    private DocumentDAOImpl() {
        try {
            this.connection = PostgresConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    public static DocumentDAOImpl getInstance() {
        if (instance == null) {
            instance = new DocumentDAOImpl();
        }
        return instance;
    }

    @Override
    public void addDocument(Document document) {
        String sql = "INSERT INTO library_documents (id, title, author, publisher, publication_year, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, document.getId());
            stmt.setString(2, document.getTitle());
            stmt.setString(3, document.getAuthor());
            stmt.setString(4, document.getPublisher());
            stmt.setInt(5, document.getPublicationYear());
            stmt.setString(6, document.getType());
            stmt.executeUpdate();
    
            addSpecificDocumentDetails(document);
        } catch (SQLException e) {
            if (e.getSQLState().equals("P0001")) { // PostgreSQL custom error code
                throw new IllegalArgumentException("A document with this title and author already exists.");
            }
            throw new RuntimeException("Error adding document", e);
        }
    }
    private void addSpecificDocumentDetails(Document document) throws SQLException {
        if (document instanceof Book) {
            addBookDetails((Book) document);
        } else if (document instanceof Magazine) {
            addMagazineDetails((Magazine) document);
        } else if (document instanceof ScientificJournal) {
            addScientificJournalDetails((ScientificJournal) document);
        } else if (document instanceof UniversityThesis) {
            addUniversityThesisDetails((UniversityThesis) document);
        } else {
            throw new IllegalArgumentException("Unknown document type");
        }
    }

    private void addBookDetails(Book book) throws SQLException {
        String sql = "INSERT INTO books (id, isbn) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, book.getId());
            stmt.setString(2, book.getIsbn());
            stmt.executeUpdate();
        }
    }

    private void addMagazineDetails(Magazine magazine) throws SQLException {
        String sql = "INSERT INTO magazines (id, issue_number) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, magazine.getId());
            stmt.setInt(2, magazine.getIssueNumber());
            stmt.executeUpdate();
        }
    }

    private void addScientificJournalDetails(ScientificJournal journal) throws SQLException {
        String sql = "INSERT INTO scientific_journals (id, research_field) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, journal.getId());
            stmt.setString(2, journal.getResearchField());
            stmt.executeUpdate();
        }
    }

    private void addUniversityThesisDetails(UniversityThesis thesis) throws SQLException {
        String sql = "INSERT INTO university_theses (id, university, field) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, thesis.getId());
            stmt.setString(2, thesis.getUniversity());
            stmt.setString(3, thesis.getField());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateDocument(Document document) {
        String sql = "UPDATE library_documents SET title = ?, author = ?, publisher = ?, publication_year = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, document.getTitle());
            stmt.setString(2, document.getAuthor());
            stmt.setString(3, document.getPublisher());
            stmt.setInt(4, document.getPublicationYear());
            stmt.setObject(5, document.getId());
            stmt.executeUpdate();

            updateSpecificDocumentDetails(document);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating document", e);
        }
    }

    private void updateSpecificDocumentDetails(Document document) throws SQLException {
        if (document instanceof Book) {
            updateBookDetails((Book) document);
        } else if (document instanceof Magazine) {
            updateMagazineDetails((Magazine) document);
        } else if (document instanceof ScientificJournal) {
            updateScientificJournalDetails((ScientificJournal) document);
        } else if (document instanceof UniversityThesis) {
            updateUniversityThesisDetails((UniversityThesis) document);
        }
    }

    private void updateBookDetails(Book book) throws SQLException {
        String sql = "UPDATE books SET isbn = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getIsbn());
            stmt.setObject(2, book.getId());
            stmt.executeUpdate();
        }
    }

    private void updateMagazineDetails(Magazine magazine) throws SQLException {
        String sql = "UPDATE magazines SET issue_number = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, magazine.getIssueNumber());
            stmt.setObject(2, magazine.getId());
            stmt.executeUpdate();
        }
    }

    private void updateScientificJournalDetails(ScientificJournal journal) throws SQLException {
        String sql = "UPDATE scientific_journals SET research_field = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, journal.getResearchField());
            stmt.setObject(2, journal.getId());
            stmt.executeUpdate();
        }
    }

    private void updateUniversityThesisDetails(UniversityThesis thesis) throws SQLException {
        String sql = "UPDATE university_theses SET university = ?, field = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, thesis.getUniversity());
            stmt.setString(2, thesis.getField());
            stmt.setObject(3, thesis.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteDocument(UUID documentId) {
        String sql = "DELETE FROM library_documents WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, documentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting document", e);
        }
    }

    @Override
    public Optional<Document> getDocumentById(UUID documentId) {
        String sql = "SELECT * FROM library_documents WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, documentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching document by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Document> getDocumentByTitle(String title) {
        String sql = "SELECT * FROM library_documents WHERE title = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching document by title", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Document> getAllDocuments() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM all_documents";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all documents", e);
        }
        return documents;
    }

    @Override
    public List<Document> searchDocuments(String searchTerm) {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM library_documents WHERE title ILIKE ? OR author ILIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String likeSearchTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeSearchTerm);
            stmt.setString(2, likeSearchTerm);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                documents.add(mapResultSetToDocument(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching documents", e);
        }
        return documents;
    }

    private Document mapResultSetToDocument(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String publisher = rs.getString("publisher");
        int publicationYear = rs.getInt("publication_year");
        String type = rs.getString("type");

        switch (type) {
            case "Book":
                return mapToBook(id, title, author, publisher, publicationYear);
            case "Magazine":
                return mapToMagazine(id, title, author, publisher, publicationYear);
            case "ScientificJournal":
                return mapToScientificJournal(id, title, author, publisher, publicationYear);
            case "UniversityThesis":
                return mapToUniversityThesis(id, title, author, publisher, publicationYear);
            default:
                throw new IllegalArgumentException("Unknown document type: " + type);
        }
    }

    private Book mapToBook(UUID id, String title, String author, String publisher, int publicationYear) throws SQLException {
        String sql = "SELECT isbn FROM books WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String isbn = rs.getString("isbn");
                return new Book(id, title, author, publisher, publicationYear, isbn);
            }
        }
        throw new SQLException("Book details not found for document ID: " + id);
    }

    private Magazine mapToMagazine(UUID id, String title, String author, String publisher, int publicationYear) throws SQLException {
        String sql = "SELECT issue_number FROM magazines WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int issueNumber = rs.getInt("issue_number");
                return new Magazine(id, title, author, publisher, publicationYear, issueNumber);
            }
        }
        throw new SQLException("Magazine details not found for document ID: " + id);
    }

    private ScientificJournal mapToScientificJournal(UUID id, String title, String author, String publisher, int publicationYear) throws SQLException {
        String sql = "SELECT research_field FROM scientific_journals WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String researchField = rs.getString("research_field");
                return new ScientificJournal(id, title, author, publisher, publicationYear, researchField);
            }
        }
        throw new SQLException("Scientific Journal details not found for document ID: " + id);
    }

    private UniversityThesis mapToUniversityThesis(UUID id, String title, String author, String publisher, int publicationYear) throws SQLException {
        String sql = "SELECT university, field FROM university_theses WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String university = rs.getString("university");
                String field = rs.getString("field");
                return new UniversityThesis(id, title, author, publisher, publicationYear, university, field);
            }
        }
        throw new SQLException("University Thesis details not found for document ID: " + id);
    }

}