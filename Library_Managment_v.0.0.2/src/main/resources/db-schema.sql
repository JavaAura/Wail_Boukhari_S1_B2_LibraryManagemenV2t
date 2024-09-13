-- Create the base table for documents
CREATE TABLE library_documents (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255) NOT NULL,
    publication_year INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL
);

-- Create child tables inheriting from library_documents
CREATE TABLE books (
    isbn VARCHAR(20) NOT NULL
) INHERITS (library_documents);

CREATE TABLE magazines (
    issue_number INTEGER NOT NULL
) INHERITS (library_documents);

CREATE TABLE scientific_journals (
    research_field VARCHAR(100) NOT NULL
) INHERITS (library_documents);

CREATE TABLE university_theses (
    university VARCHAR(100) NOT NULL,
    field VARCHAR(100) NOT NULL
) INHERITS (library_documents);

-- Create views to maintain compatibility with existing code
CREATE OR REPLACE VIEW books_view AS
SELECT id, isbn FROM books;

CREATE OR REPLACE VIEW magazines_view AS
SELECT id, issue_number FROM magazines;

CREATE OR REPLACE VIEW scientific_journals_view AS
SELECT id, research_field FROM scientific_journals;

CREATE OR REPLACE VIEW university_theses_view AS
SELECT id, university, field FROM university_theses;

-- Update the all_documents view
CREATE OR REPLACE VIEW all_documents AS
SELECT 
    d.id, d.title, d.author, d.publisher, d.publication_year, d.type,
    b.isbn, 
    m.issue_number, 
    sj.research_field, 
    ut.university, ut.field
FROM library_documents d
LEFT JOIN books b ON d.id = b.id
LEFT JOIN magazines m ON d.id = m.id
LEFT JOIN scientific_journals sj ON d.id = sj.id
LEFT JOIN university_theses ut ON d.id = ut.id;

-- Create unique index on the base table
CREATE UNIQUE INDEX idx_document_title_author ON library_documents (title, author);

-- Create the check_document_exists function
CREATE OR REPLACE FUNCTION check_document_exists()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM library_documents WHERE title = NEW.title AND author = NEW.author) THEN
        RAISE EXCEPTION 'A document with this title and author already exists.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger on the base table
CREATE TRIGGER document_exists_check
BEFORE INSERT ON library_documents
FOR EACH ROW
EXECUTE FUNCTION check_document_exists();

-- Create the base table for users
CREATE TABLE library_users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    borrowing_limit INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL
);

-- Create child tables inheriting from library_users
CREATE TABLE student_users (
    student_id VARCHAR(20) NOT NULL,
    department VARCHAR(100) NOT NULL
) INHERITS (library_users);

CREATE TABLE professor_users (
    department VARCHAR(100) NOT NULL
) INHERITS (library_users);

-- Create views to maintain compatibility with existing code
CREATE OR REPLACE VIEW student_users_view AS
SELECT id, student_id, department FROM student_users;

CREATE OR REPLACE VIEW professor_users_view AS
SELECT id, department FROM professor_users;

-- Create unique indexes on the base table
CREATE UNIQUE INDEX idx_user_name ON library_users (name);
CREATE UNIQUE INDEX idx_user_email ON library_users (email);

-- Create the loans table (no changes needed)
CREATE TABLE loans (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL,
    user_id UUID NOT NULL,
    loan_date DATE NOT NULL,
    return_date DATE
);

CREATE INDEX idx_loan_document_id ON loans (document_id);
CREATE INDEX idx_loan_user_id ON loans (user_id);

-- Create the reservations table (no changes needed)
CREATE TABLE reservations (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL,
    user_id UUID NOT NULL,
    reservation_date DATE NOT NULL
);

CREATE INDEX idx_reservation_document_id ON reservations (document_id);
CREATE INDEX idx_reservation_user_id ON reservations (user_id);

-- Create a function to check if a document exists in any of the document tables
CREATE OR REPLACE FUNCTION document_exists(doc_id UUID) RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 FROM all_documents WHERE id = doc_id
    );
END;
$$ LANGUAGE plpgsql;

-- Create a function to check if a user exists
CREATE OR REPLACE FUNCTION user_exists(user_id UUID) RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 FROM library_users WHERE id = user_id
    );
END;
$$ LANGUAGE plpgsql;

-- Create triggers to enforce foreign key-like constraints
CREATE OR REPLACE FUNCTION check_loan_references()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT document_exists(NEW.document_id) THEN
        RAISE EXCEPTION 'Referenced document does not exist';
    END IF;
    IF NOT user_exists(NEW.user_id) THEN
        RAISE EXCEPTION 'Referenced user does not exist';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER loan_reference_check
BEFORE INSERT OR UPDATE ON loans
FOR EACH ROW EXECUTE FUNCTION check_loan_references();

-- Create triggers to enforce foreign key-like constraints for reservations
CREATE OR REPLACE FUNCTION check_reservation_references()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT document_exists(NEW.document_id) THEN
        RAISE EXCEPTION 'Referenced document does not exist';
    END IF;
    IF NOT user_exists(NEW.user_id) THEN
        RAISE EXCEPTION 'Referenced user does not exist';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER reservation_reference_check
BEFORE INSERT OR UPDATE ON reservations
FOR EACH ROW EXECUTE FUNCTION check_reservation_references();

ALTER TABLE loans ADD CONSTRAINT unique_active_loan UNIQUE (document_id, return_date);