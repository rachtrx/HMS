![Cover Page](./img/HMSCover.jpg)

**Hospital Management System(HMS)** is a Java console application built using Object Oriented Principles. It allows for efficient access to hospital data by various stakeholders, such as doctors, pharmacists and patients. The system consists of various functions, such as management of hospital operations - patient management, staff management, appointment scheduling, and billing. The system is designed to be scalable as it closely follows the SOLID Principles.


## Links
- **[Main Page/Github Repository](https://github.com/rachtrx/HMS)**
- **[Documentation](https://example.com/documentation)**
- **[Report](https://example.com/report)**
- **[UML Class Diagram](https://example.com/report)**
- **[All Test Cases](https://example.com/report)**

## Team Members

| Name                          | GitHub Account                                       |
|-------------------------------|------------------------------------------------------|
| Rachmiel Andre Teo Ren Xiang  | [rachtrx](https://github.com/rachtrx)                |            
| Luke Eng Peng Kee             | [__LEPK__](https://github.com/LEPK02)               |
| Yeo Boon Ling, Faith          | [Faith](https://github.com/Faith-Yeo)                | 
| Lee Ding Lin                  | [dinglinlee](https://github.com/dinglinlee)          | 
| Khoo Qian Yee                 | [erinarin034](https://github.com/erinarin034)        |

## New Features

## Key Existing Features

### Patient Menu
- View Medical Record
- Update Personal Information
- View Available Appointment Slots
- Schedule an Appointment
- Reschedule an Appointment
- Cancel an Appointment
- View Scheduled Appointments
- View Past Appointment Outcome Records
- Logout

### Doctor Menu
- View Patient Medical Records
- Update Patient Medical Records
- View Personal Schedule
- Set Availability for Appointments
- Accept or Decline Appointment Requests
- View Upcoming Appointments
- Record Appointment Outcome
- Logout

### Pharmacist Menu
- View Appointment Outcome Record
- Update Prescription Status
- View Medication Inventory
- Submit Replenishment Request
- Logout

### Administrator Menu
- View and Manage Hospital Staff
- View Appointment Details
- View and Manage Medication Inventory
- Approve Replenishment Requests
- Logout

## MVC Design

This project follows the MVC (Model-View-Controller) architecture to organize code effectively, separating data management, user interface, and control logic.

- **Model**: Defines the core data structures and business logic for the application.
  - [Models](./src/app/model/): Represents essential data entities, such as patients, appointments, and inventory, along with methods for managing and validating data. This layer includes data handling logic and relationships between entities.
  
- **View**: Manages the user interface, displaying data to users and capturing their input.
  - [Views](./src/app/view/): Contains components that present the UI for different user roles (patients, doctors, pharmacists, and administrators). This layer facilitates interactions, allowing users to view and manage users, appointments and inventory.
  
- **Controller**: Acts as an intermediary between the Model and View layers, handling business logic and user requests.
  - [Controllers](./src/app/controller/): Coordinates the interactions between models and views. For example, controllers process user actions in the View, update Model data accordingly, and direct the View to display the appropriate updates. This layer ensures a smooth flow of data and maintains separation between data processing and user interface.

## SOLID Design Principles

This project is also designed with SOLID principles in mind to ensure modularity, maintainability, and flexibility. Here’s how each principle is applied:

1. **Single Responsibility Principle (SRP)**: Each class in this project has a single responsibility or purpose. For example, `User`, `Appointment`, and `Medication` classes focus solely on managing their respective data and behaviors, while controllers like `UserService` and `AppointmentService` manage the flow between the model and view. This separation ensures that each class is easier to understand, test, and maintain.

2. **Open/Closed Principle (OCP)**: Classes in this project are open for extension but closed for modification. For instance, by using inheritance in builders, controllers, and menu structures, new functionalities can be added by extending existing classes rather than modifying them. This is evident in the `Menu` and `User` classes, where additional menus or users can be added without altering the core code.

3. **Liskov Substitution Principle (LSP)**: Derived classes in this project can replace their base classes without affecting the functionality. For example, `Patient`, `Doctor`, `Pharmacist`, and `Admin` extend the `Staff` class, ensuring that they can be used interchangeably in contexts where `User` is expected, such as for authentication functionality, without breaking the code.

4. **Interface Segregation Principle (ISP)**: This project uses focused interfaces, like `ISerializable` and functional interfaces for callbacks (such as `NextAction` and `DisplayGenerator`), which provide only the specific methods needed by a particular class. This design avoids forcing classes to implement methods they don’t use, keeping the code clean and efficient.

5. **Dependency Inversion Principle (DIP)**: The project adheres to DIP by depending on abstractions rather than concrete implementations. For instance, functional interfaces allow high-level modules, like `Menu` and `Builder`, to depend on abstractions, making the system more flexible and adaptable to change. This principle is also demonstrated in the interaction between `DatabaseManager` and various base models through the use of the `ISerializable` interface.

These principles guide the architecture of the application, making it easier to extend, test, and refactor over time. By adhering to SOLID principles, the project achieves a clean, organized, and robust structure that supports future growth and adaptability.

## Directory Structure
```plaintext
src/
└── app/
    ├── model/                # Core entities and business logic
    │   ├── appointments/
    │   ├── inventory/
    │   ├── users/
    │   ├── Builder.java
    │   └── ISerializable.java
    ├── view/                 # UI and user interaction components (formerly user_input)
    │   ├── menu_collections/
    │   ├── option_collections/
    │   ├── Input.java
    │   ├── InputMenu.java
    │   ├── Menu.java
    │   ├── MenuState.java
    │   ├── Option.java
    │   ├── OptionMenu.java
    │   └── OptionTable.java
    ├── controller/          # Manages interactions between model and view
    │   ├── AppointmentService.java
    │   ├── CsvReaderService.java
    │   ├── MedicationService.java
    │   ├── MenuService.java
    │   ├── FunctionalInterfaces.java
    │   └── UserService.java
    ├── db/                   # Mock database for storing serialized model instances
    │   ├── DatabaseManager.java
    │   ├── DeleteBehavior.java
    │   ├── Relationship.java
    │   ├── Row.java
    │   ├── Table.java
    │   └── TableConfig.java
    ├── utils/                # Helper classes and functional interfaces
    │   ├── DateTimeUtils.java
    │   ├── EnumUtils.java
    │   ├── LoggerUtils.java
    │   └── StringUtils.java
└── resources/                # Data files and resources
```

## Installation
To set up and run this application locally, follow these steps:
1. Clone this repository:
   ```bash
   git clone <repository-url>

2. **Navigate to project directory:**
    ```bash
    cd HMS
    ```

3. **Compile the Java source files:**
    From the `HMS` directory, run the following to compile and output the `.class` files into an `out` directory.
    ```bash
    javac -d out -sourcepath src src/app/App.java
    ```

4. **Run the App:**
    After compiling, run the `App` Class.
    ```bash
    java -cp out app.App
    ```
