package com.example.imageprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.Map;

public class YamlTest {
    public static void main(String[] args) {
        try {
            String testYaml = "aws:\n  region: us-east-2";
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> result = mapper.readValue(testYaml, Map.class);
            System.out.println("Test successful: " + result);
        } catch (Exception e) {
            System.out.println("YAML test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}