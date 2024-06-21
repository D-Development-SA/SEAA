package com.softel.seaa.Controller.SeaaManager;

import lombok.Getter;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

public abstract class FileSystem {
    @Getter
    private static final String root = System.getProperty("user.dir") + "\\Backend\\FileSystem";
    @Getter
    private static final String rootUsers = root + "\\USERS";
    private static final String nameMI = "seaami.exe";

    public static void newFileSystem() throws IOException {
        copyFiles();
        createDirUsers();
    }

    private static void copyFiles() throws IOException {
        Path src = new ClassPathResource("static").getFile().toPath();
        Path dest = Paths.get(root);
        if (!dest.toFile().exists()) {
            try {
                System.out.println("[ " + LocalDateTime.now() + "] Copying necessary files...");
                Files.walk(src)
                        .forEach(source -> copy(source, dest.resolve(src.relativize(source))));
                System.out.println("[ " + LocalDateTime.now() + "] Copied files...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static void createDirUsers() throws IOException {
        File file = new File(rootUsers);
        if (!file.exists())
            file.mkdir();
    }

    public static void executeCMD(String answer, String question, String nameSE) throws Exception {
        String rootSE = root.concat("\\") + nameSE;
        File file = new File(rootSE + "\\MI\\execute.cmd");

        preparingCMD_File(file);

        writingCMD_File(answer, question, rootSE, file);

        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe","/c","call","execute.cmd");
        processBuilder.directory(new File(rootSE + "\\MI"));
        processBuilder.start();
    }

    private static void writingCMD_File(String answer, String question, String rootSE, File file) throws IOException {
        String command = nameMI + " "
                + answer + " "
                + question + " "
                + rootSE.concat("\\BC");

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        writer.write(command);
        writer.flush();
        writer.close();
    }

    private static void preparingCMD_File(File file) throws Exception {
        if (file.exists() && !file.delete()){
            throw new Exception("Could not delete cmd");
        }

        if (!file.createNewFile())
            throw new Exception("Could not create cmd file");
    }

}
