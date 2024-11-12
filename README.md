# Hospital Management System [HMS] application

Y2S1 - SC2002: Object-Oriented Design & Programming

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

## Overview
This Hospital Management System is designed to facilitate interactions between patients, doctors, pharmacists, and administrators within a hospital. Each role has specific functionalities accessible through their customized menu interfaces. The application includes robust features for managing appointments, medical records, prescriptions, inventory, and staff, with each component efficiently integrated to enhance the user experience.

## Features
### Patient Menu
Patients can manage their personal information, view medical records, and interact with the hospital’s scheduling system. Options include:
- **View Medical Record**: Access detailed records of personal medical history.
- **Update Personal Information**: Update contact information and other personal details.
- **View Available Appointment Slots**: Browse available time slots for scheduling appointments with doctors.
- **Schedule an Appointment**: Book a new appointment.
- **Reschedule an Appointment**: Change an existing appointment to a different time.
- **Cancel an Appointment**: Cancel an existing appointment.
- **View Scheduled Appointments**: List all upcoming appointments.
- **View Past Appointment Outcome Records**: Access summaries of past appointments, including outcomes and treatments provided.
- **Logout**: Log out of the application.

### Doctor Menu
Doctors can manage patient records, handle appointment scheduling, and record outcomes. Options include:
- **View Patient Medical Records**: Access the medical histories of patients.
- **Update Patient Medical Records**: Add or modify patient medical records.
- **View Personal Schedule**: Review a calendar of upcoming appointments.
- **Set Availability for Appointments**: Define time slots available for patient appointments.
- **Accept or Decline Appointment Requests**: Manage pending appointment requests.
- **View Upcoming Appointments**: See a list of confirmed future appointments.
- **Record Appointment Outcome**: Document notes and treatments from completed appointments.
- **Logout**: Log out of the application.

### Pharmacist Menu
Pharmacists can monitor appointment outcomes and manage medication inventory. Options include:
- **View Appointment Outcome Record**: Review patient records for medication dispensing.
- **Update Prescription Status**: Mark prescriptions as filled or pending.
- **View Medication Inventory**: Check current stock levels of medications.
- **Submit Replenishment Request**: Request additional stock for medications with low inventory.
- **Logout**: Log out of the application.

### Administrator Menu
Administrators oversee hospital operations, manage staff, appointments, and medication inventory. Options include:
- **View and Manage Hospital Staff**: Access and manage information on hospital employees.
- **View Appointments Details**: Review details of all scheduled appointments.
- **View and Manage Medication Inventory**: Monitor and adjust medication stocks.
- **Approve Replenishment Requests**: Approve or deny requests for medication restocking.
- **Logout**: Log out of the application.

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

## Contributors

| Name                        |
|-----------------------------|
| Lee Ding Lin                |
| Luke Eng Peng Kee           |
| Khoo Qian Yee               |
| Rachmiel Andre Teo Ren Xiang |
| Yeo Boon Ling, Faith        |