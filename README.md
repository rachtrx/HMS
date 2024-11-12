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

## Contributors

| Name                        |
|-----------------------------|
| Lee Ding Lin                |
| Luke Eng Peng Kee           |
| Khoo Qian Yee               |
| Rachmiel Andre Teo Ren Xiang |
| Yeo Boon Ling, Faith        |