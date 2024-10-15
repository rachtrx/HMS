# Hospital Management System [HMS] application

Y2S1 - SC2002: Object-Oriented Design & Programming

# Java Project

Follow the steps below to compile and run the Java code in the `src` directory.

## Steps

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