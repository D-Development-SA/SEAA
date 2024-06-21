package com.softel.seaa;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public abstract class DatabaseInitializer{

    public static void createDatabase() throws Exception {

        if (initOneTime()) {
            String password = getPassword();
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                    "postgres",
                    password.replaceAll("[$o.@+*/#]*", ""))) {
                if (conn != null) {
                    System.out.println("**Established connection");

                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT datname FROM pg_database WHERE datname = 'db_seaa_backend'");

                    if (!rs.next()){
                        st.executeUpdate("CREATE DATABASE db_seaa_backend");
                        System.out.println("--Database created");
                    }
                }else System.out.println("Failed to make connection");
            }
        }
    }

    private static boolean initOneTime() throws IOException {
        File file = new File("$");

        if (file.exists()) {
            return false;
        }
        else file.createNewFile();

        return true;
    }

    private static String getPassword() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = DatabaseInitializer.class.getResourceAsStream("/application.properties")) {
            properties.load(inputStream);
        }
        return properties.getProperty("spring.datasource.password");
    }
}