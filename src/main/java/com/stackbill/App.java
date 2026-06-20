package com.stackbill;

public class App {
    public String getGreeting() {
        return "Hello World! Secure Pipeline Verified.";
    }

    public static void main(String[] args) {
        App app = new App();
        System.out.println(app.getGreeting());

        // Keep the JVM thread alive indefinitely for K3s orchestration
        while (true) {
            try {
                Thread.sleep(3600000); // Sleep for 1 hour chunks
            } catch (InterruptedException e) {
                System.out.println("Application interrupted.");
                break;
            }
        }
    }
}
