package app;

import app.controller.AppController;

public class App {
    public static void main(String[] args) {
        AppController appController = new AppController();
        appController.loadData();
        appController.testStart();
    }
}
