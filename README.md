# Library Management System

## Project Description
This project aims to enhance a console-based library management application by adding data persistence and advanced functionalities. The application utilizes a relational database to store information about documents and users and leverages advanced Java concepts to optimize and extend its features.

## General Objective of the Application
The main objective is to extend the functionality of a library management application by integrating data persistence using PostgreSQL while applying advanced Object-Oriented Programming (OOP) concepts in Java. This includes managing borrowing, reservations, and users, as well as advanced search and input validation.

## Technologies Used
- **Java 8**: For Object-Oriented Programming, functional interfaces, lambda expressions, and advanced data handling with Java Time API.
- **PostgreSQL**: For relational database management.
- **JDBC**: For database connectivity.
- **HashMaps**: To optimize document searches.
- **UML**: For system modeling.

## Project Structure
1. **Presentation Layer**:
   - `ConsoleUI`: Enhanced console interface for user interaction.

2. **Business Layer**:
   - Management of documents, borrowings, and reservations.
   - Extended class hierarchy for documents and users.

3. **Persistence Layer**:
   - DAO (Data Access Objects) for data access.

4. **Utility Layer**:
   - `DateUtils`: Advanced date manipulation.
   - `InputValidator`: User input validation.

## Brief Description of the Adopted Architecture
The application is structured into three main layers:
- **Presentation Layer**: Manages user interface via the console.
- **Business Layer**: Contains logic for managing documents and users.
- **Persistence Layer**: Uses the DAO pattern to interact with the PostgreSQL database.
- **Utility Layer**: Provides auxiliary classes for input validation and date manipulation.

## Installation and Usage Instructions

### Prerequisites
- **Java 8**: Ensure Java 8 is installed on your machine.
- **PostgreSQL**: You need to have PostgreSQL installed and configured.

### Steps to Configure the Database
1. **Create the Database**:
   - Connect to PostgreSQL and create a new database for the application.

2. **Define the Schema**:
   - Run the SQL scripts provided in the `sql/` directory to create the necessary tables and relationships.

3. **Configure JDBC Connection**:
   - Modify the `db.properties` configuration file to include your PostgreSQL database connection details.

### How to Run the Application
1. **Compile the Project**:
   - Use Maven to compile the project and resolve dependencies.

2. **Run the Application**:
   - Start the application via the console using the appropriate command (e.g., `java -jar library-management-system.jar`).

## Possible Future Improvements
- **Graphical User Interface**: Develop a graphical user interface for a better user experience.
- **Advanced Features**: Add features such as fine management and reporting.
- **Performance Optimization**: Improve query efficiency and resource management.

## Ideas for Extending or Improving the Project
- **Integration with External Systems**: Add features to integrate the application with existing library management systems.
- **Enhanced Search Capabilities**: Implement advanced search algorithms for better document search.
- **Authentication and Authorization**: Add authentication and authorization mechanisms to secure access to application features.

## Author and Contact
- **Author**: [WAIL BOUKHARI]
- **Contact**: [boukhari.wail001@gmail.com] | [My GitHub : https://github.com/WailBoukhari1/ -- My Linkedin : https://www.linkedin.com/in/wail-boukhari-976257240/]

