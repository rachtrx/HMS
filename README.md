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

## Build and Run
Follow the steps below to compile and run the Java code in the `src` directory.

1. **Navigate to project directory:**
    ```bash
    cd HMS
    ```

2. **Compile the Java source files:**
    From the `HMS` directory, run the following to compile and output the `.class` files into an `out` directory.
    ```bash
    javac -d out -sourcepath src src/app/App.java
    ```

3. **Run the App:**
    After compiling, run the `App` Class.
    ```bash
    java -cp out app.App
    ```
