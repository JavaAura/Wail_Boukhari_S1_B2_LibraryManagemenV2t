-- Create the parent table for library users
CREATE TABLE library_users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    borrowing_limit INT NOT NULL
);


-- Create the student user table that inherits from the library_users table
CREATE TABLE student_users (
    student_id VARCHAR(50) NOT NULL,
    department VARCHAR(255),
    CHECK (borrowing_limit = 3)
) INHERITS (library_users);

-- Create the professor user table that inherits from the library_users table
CREATE TABLE professor_users (
    department VARCHAR(255) NOT NULL,
    CHECK (borrowing_limit = 5)
) INHERITS (library_users);

-- Create the parent table for library documents
CREATE TABLE library_documents (
    id UUID PRIMARY KEY,
    title VARCHAR(255) UNIQUE NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255) NOT NULL,
    publication_year INT NOT NULL,
    type VARCHAR(50) NOT NULL
);

-- Create the book table that inherits from the library_documents table
CREATE TABLE books (
    id UUID PRIMARY KEY REFERENCES library_documents(id),
    isbn VARCHAR(13) NOT NULL,
    CHECK (type = 'Book')
) INHERITS (library_documents);

-- Create the magazine table that inherits from the library_documents table
CREATE TABLE magazines (
    id UUID PRIMARY KEY REFERENCES library_documents(id),
    issue_number INT NOT NULL,
    CHECK (type = 'Magazine')
) INHERITS (library_documents);

-- Create the scientific journal table that inherits from the library_documents table
CREATE TABLE scientific_journals (
    id UUID PRIMARY KEY REFERENCES library_documents(id),
    research_field VARCHAR(255) NOT NULL,
    CHECK (type = 'ScientificJournal')
) INHERITS (library_documents);

-- Create the university thesis table that inherits from the library_documents table
CREATE TABLE university_theses (
    id UUID PRIMARY KEY REFERENCES library_documents(id),
    university VARCHAR(255) NOT NULL,
    field VARCHAR(255) NOT NULL,
    CHECK (type = 'UniversityThesis')
) INHERITS (library_documents);

-- Create the loans table
CREATE TABLE loans (
    id UUID PRIMARY KEY,
    document_title VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    loan_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (document_title) REFERENCES library_documents(title),
    FOREIGN KEY (user_name) REFERENCES library_users(name)
);

-- Create the reservations table
CREATE TABLE reservations (
    id UUID PRIMARY KEY,
    document_title VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    reservation_date DATE NOT NULL,
    FOREIGN KEY (document_title) REFERENCES library_documents(title),
    FOREIGN KEY (user_name) REFERENCES library_users(name)
);