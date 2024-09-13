package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.library.model.document.Book;
import com.library.model.document.Document;
import com.library.model.document.Magazine;
import com.library.model.document.ScientificJournal;
import com.library.model.document.UniversityThesis;

public class DocumentDAO {
    private static DocumentDAO instance;

    private DocumentDAO() {}

    public static DocumentDAO getInstance() {
        if (instance == null) {
            instance = new DocumentDAO();
        }
        return instance;
    }

    public void save(Document document) {
        String sql = "INSERT INTO library_documents (id, title, author, publisher, publication_year, type) VALUES (?, ?, ?, ?, ?, ?)";
        UUID id = UUID.randomUUID();
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setDocumentParams(stmt, id, document);
            stmt.setString(6, document.getClass().getSimpleName());
            stmt.executeUpdate();
            saveSpecificDocument(conn, id, document);
        } catch (SQLException e) {
            handleException("Error saving document", e);
        }
    }

    public boolean documentExists(String title) {
        String sql = "SELECT COUNT(*) FROM library_documents WHERE title = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            handleException("Error checking document existence", e);
            return false;
        }
    }

    private void saveSpecificDocument(Connection conn, UUID id, Document document) throws SQLException {
        String tableName = getTableName(document);
        String sql;
        if (document instanceof Book) {
            sql = String.format("INSERT INTO %s (id, title, author, publisher, publication_year, type, isbn) VALUES (?, ?, ?, ?, ?, ?, ?)", tableName);
        } else if (document instanceof Magazine) {
            sql = String.format("INSERT INTO %s (id, title, author, publisher, publication_year, type, issue_number) VALUES (?, ?, ?, ?, ?, ?, ?)", tableName);
        } else if (document instanceof ScientificJournal) {
            sql = String.format("INSERT INTO %s (id, title, author, publisher, publication_year, type, research_field) VALUES (?, ?, ?, ?, ?, ?, ?)", tableName);
        } else if (document instanceof UniversityThesis) {
            sql = String.format("INSERT INTO %s (id, title, author, publisher, publication_year, type, university, field) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", tableName);
        } else {
            sql = String.format("INSERT INTO %s (id, title, author, publisher, publication_year, type) VALUES (?, ?, ?, ?, ?, ?)", tableName);
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setString(2, document.getTitle());
            stmt.setString(3, document.getAuthor());
            stmt.setString(4, document.getPublisher());
            stmt.setInt(5, document.getPublicationYear());
            stmt.setString(6, document.getClass().getSimpleName());
            if (document instanceof Book) {
                stmt.setString(7, ((Book) document).getIsbn());
            } else if (document instanceof Magazine) {
                stmt.setInt(7, ((Magazine) document).getIssueNumber());
            } else if (document instanceof ScientificJournal) {
                stmt.setString(7, ((ScientificJournal) document).getResearchField());
            } else if (document instanceof UniversityThesis) {
                UniversityThesis thesis = (UniversityThesis) document;
                stmt.setString(7, thesis.getUniversity());
                stmt.setString(8, thesis.getField());
            }
            stmt.executeUpdate();
        }
    }
    public void update(Document document) {
        String sql = "UPDATE library_documents SET author = ?, publisher = ?, publication_year = ? WHERE title = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setUpdateParams(stmt, document);
            stmt.executeUpdate();
            updateSpecificDocument(conn, document);
        } catch (SQLException e) {
            handleException("Error updating document", e);
        }
    }

    private void updateSpecificDocument(Connection conn, Document document) throws SQLException {
        String tableName = getTableName(document);
        String sql;
        if (document instanceof Book) {
            sql = String.format("UPDATE %s SET author = ?, publisher = ?, publication_year = ?, isbn = ? WHERE title = ?", tableName);
        } else if (document instanceof Magazine) {
            sql = String.format("UPDATE %s SET author = ?, publisher = ?, publication_year = ?, issue_number = ? WHERE title = ?", tableName);
        } else if (document instanceof ScientificJournal) {
            sql = String.format("UPDATE %s SET author = ?, publisher = ?, publication_year = ?, research_field = ? WHERE title = ?", tableName);
        } else if (document instanceof UniversityThesis) {
            sql = String.format("UPDATE %s SET author = ?, publisher = ?, publication_year = ?, university = ?, field = ? WHERE title = ?", tableName);
        } else {
            sql = String.format("UPDATE %s SET author = ?, publisher = ?, publication_year = ? WHERE title = ?", tableName);
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setUpdateParams(stmt, document);
            if (document instanceof Book) {
                stmt.setString(4, ((Book) document).getIsbn());
                stmt.setString(5, document.getTitle());
            } else if (document instanceof Magazine) {
                stmt.setInt(4, ((Magazine) document).getIssueNumber());
                stmt.setString(5, document.getTitle());
            } else if (document instanceof ScientificJournal) {
                stmt.setString(4, ((ScientificJournal) document).getResearchField());
                stmt.setString(5, document.getTitle());
            } else if (document instanceof UniversityThesis) {
                UniversityThesis thesis = (UniversityThesis) document;
                stmt.setString(4, thesis.getUniversity());
                stmt.setString(5, thesis.getField());
                stmt.setString(6, document.getTitle());
            } else {
                stmt.setString(4, document.getTitle());
            }
            stmt.executeUpdate();
        }
    }

    public void delete(String title) {
        String sql = "DELETE FROM library_documents WHERE title = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleException("Error deleting document", e);
        }
    }

    public List<Document> findAll() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT ld.*, b.isbn, m.issue_number, sj.research_field, ut.university, ut.field " +
                     "FROM library_documents ld " +
                     "LEFT JOIN books b ON ld.id = b.id " +
                     "LEFT JOIN magazines m ON ld.id = m.id " +
                     "LEFT JOIN scientific_journals sj ON ld.id = sj.id " +
                     "LEFT JOIN university_theses ut ON ld.id = ut.id";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                documents.add(createDocumentFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error finding all documents", e);
        }
        return documents;
    }

    public Optional<Document> findByTitle(String title) {
        String sql = "SELECT ld.*, b.isbn, m.issue_number, sj.research_field, ut.university, ut.field " +
                     "FROM library_documents ld " +
                     "LEFT JOIN books b ON ld.id = b.id " +
                     "LEFT JOIN magazines m ON ld.id = m.id " +
                     "LEFT JOIN scientific_journals sj ON ld.id = sj.id " +
                     "LEFT JOIN university_theses ut ON ld.id = ut.id " +
                     "WHERE ld.title = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(createDocumentFromResultSet(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            handleException("Error finding document by title", e);
            return Optional.empty();
        }
    }

    private Document createDocumentFromResultSet(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String publisher = rs.getString("publisher");
        int publicationYear = rs.getInt("publication_year");
        String type = getDocumentType(title);

        if (type == null) {
            throw new IllegalArgumentException("Unknown document type for title: " + title);
        }

        switch (type) {
            case "Book":
                String isbn = rs.getString("isbn");
                return new Book(id, title, author, publisher, publicationYear, isbn);
            case "Magazine":
                int issueNumber = rs.getInt("issue_number");
                return new Magazine(id, title, author, publisher, publicationYear, issueNumber);
            case "ScientificJournal":
                String researchField = getResearchField(title);
                return new ScientificJournal(id, title, author, publisher, publicationYear, researchField);
            case "UniversityThesis":
                String university = getUniversity(title);
                String field = getField(title);
                return new UniversityThesis(id, title, author, publisher, publicationYear, university, field);
            default:
                throw new IllegalArgumentException("Unknown document type: " + type);
        }
    }

    private String getTableName(Document document) {
        if (document instanceof Book) return "books";
        if (document instanceof Magazine) return "magazines";
        if (document instanceof ScientificJournal) return "scientific_journals";
        if (document instanceof UniversityThesis) return "university_theses";
        throw new IllegalArgumentException("Unknown document type");
    }

    private String getDocumentType(String title) throws SQLException {
        String[] tables = {"books", "magazines", "scientific_journals", "university_theses"};
        for (String table : tables) {
            String sql = String.format("SELECT type FROM %s WHERE title = ?", table);
            try (Connection conn = PostgresConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, title);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("type");
                    }
                }
            }
        }
        throw new SQLException("Document not found in any table");
    }

    private String getFieldFromTable(String title, String table, String field) throws SQLException {
        String sql = String.format("SELECT %s FROM %s WHERE title = ?", field, table);
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(field);
                }
            }
        }
        throw new SQLException(field + " not found for title: " + title);
    }

    private String getResearchField(String title) throws SQLException {
        return getFieldFromTable(title, "scientific_journals", "research_field");
    }

    private String getUniversity(String title) throws SQLException {
        return getFieldFromTable(title, "university_theses", "university");
    }

    private String getField(String title) throws SQLException {
        return getFieldFromTable(title, "university_theses", "field");
    }

    private void setDocumentParams(PreparedStatement stmt, UUID id, Document document) throws SQLException {
        stmt.setObject(1, id);
        stmt.setString(2, document.getTitle());
        stmt.setString(3, document.getAuthor());
        stmt.setString(4, document.getPublisher());
        stmt.setInt(5, document.getPublicationYear());
    }

    private void setUpdateParams(PreparedStatement stmt, Document document) throws SQLException {
        stmt.setString(1, document.getAuthor());
        stmt.setString(2, document.getPublisher());
        stmt.setInt(3, document.getPublicationYear());
        stmt.setString(4, document.getTitle());
    }

    private void handleException(String message, SQLException e) {
        // Log the exception or handle it according to your application's needs
        System.err.println(message + ": " + e.getMessage());
    }
}