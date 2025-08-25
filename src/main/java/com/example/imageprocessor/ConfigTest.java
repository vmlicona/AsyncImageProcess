package com.example.imageprocessor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ConfigTest {
    public static void main(String[] args) {
        // Method 1: ClassLoader
        try (InputStream is1 = ConfigTest.class.getClassLoader().getResourceAsStream("application.yml")) {
            System.out.println("ClassLoader method: " + (is1 != null ? "SUCCESS" : "FAILED"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Method 2: Class with leading slash
        try (InputStream is2 = ConfigTest.class.getResourceAsStream("/application.yml")) {
            System.out.println("Class with slash method: " + (is2 != null ? "SUCCESS" : "FAILED"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Method 3: Class without leading slash (this will fail)
        try (InputStream is3 = ConfigTest.class.getResourceAsStream("application.yml")) {
            System.out.println("Class without slash method: " + (is3 != null ? "SUCCESS" : "FAILED"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}