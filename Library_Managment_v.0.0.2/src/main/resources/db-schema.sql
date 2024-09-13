CREATE TABLE library_documents (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255) NOT NULL,
    publication_year INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL
);

CREATE TABLE books (
    id UUID PRIMARY KEY REFERENCES library_documents(id),
    isbn VARCHAR(20) NOT NULL
);

CREATE TABLE magazines (
    id UUID PRIMARY KEY REFERENCES library_documents(id),
    issue_number INTEGER NOT NULL
);

CREATE TABLE scientific_journals (
    id UUID PRIMARY KEY REFERENCES library_documents(id),
    research_field VARCHAR(100) NOT NULL
);

CREATE TABLE university_theses (
    id UUID PRIMARY KEY REFERENCES library_documents(id),
    university VARCHAR(100) NOT NULL,
    field VARCHAR(100) NOT NULL
);

    CREATE VIEW all_documents AS
    SELECT d.*, b.isbn, m.issue_number, sj.research_field, ut.university, ut.field
    FROM library_documents d
    LEFT JOIN books b ON d.id = b.id
    LEFT JOIN magazines m ON d.id = m.id
    LEFT JOIN scientific_journals sj ON d.id = sj.id
    LEFT JOIN university_theses ut ON d.id = ut.id;

    CREATE UNIQUE INDEX idx_document_title_author ON library_documents (title, author);

    CREATE OR REPLACE FUNCTION check_document_exists()
    RETURNS TRIGGER AS $$
    BEGIN
        IF EXISTS (SELECT 1 FROM library_documents WHERE title = NEW.title AND author = NEW.author) THEN
            RAISE EXCEPTION 'A document with this title and author already exists.';
        END IF;
        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;

    CREATE TRIGGER document_exists_check
    BEFORE INSERT ON library_documents
    FOR EACH ROW
    EXECUTE FUNCTION check_document_exists();
    CREATE TABLE library_users (
        id UUID PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL,
        phone_number VARCHAR(20) NOT NULL,
        borrowing_limit INTEGER NOT NULL,
        type VARCHAR(50) NOT NULL
    );

    CREATE TABLE student_users (
        id UUID PRIMARY KEY REFERENCES library_users(id),
        student_id VARCHAR(20) NOT NULL,
        department VARCHAR(100) NOT NULL
    );

    CREATE TABLE professor_users (
        id UUID PRIMARY KEY REFERENCES library_users(id),
        department VARCHAR(100) NOT NULL
    );

    CREATE UNIQUE INDEX idx_user_name ON library_users (name);
    CREATE UNIQUE INDEX idx_user_email ON library_users (email);

    CREATE TABLE loans (
        id UUID PRIMARY KEY,
        document_id UUID NOT NULL REFERENCES library_documents(id),
        user_id UUID NOT NULL REFERENCES library_users(id),
        loan_date DATE NOT NULL,
        return_date DATE
    );

    CREATE INDEX idx_loan_document_id ON loans (document_id);
    CREATE INDEX idx_loan_user_id ON loans (user_id);
    CREATE TABLE reservations (
        id UUID PRIMARY KEY,
        document_id UUID NOT NULL REFERENCES library_documents(id),
        user_id UUID NOT NULL REFERENCES library_users(id),
        reservation_date DATE NOT NULL
    );

    CREATE INDEX idx_reservation_document_id ON reservations (document_id);
    CREATE INDEX idx_reservation_user_id ON reservations (user_id);