package com.mewo.hbmenhanced.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T read(File file, Class<T> clazz) {
        if (!file.exists()) return null;
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            System.err.println("[HBM-Enhanced] Failed to read JSON from: " + file.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T read(File file, Type typeOfT) {
        if (!file.exists()) {
            System.err.println("[JsonUtil] File does not exist: " + file.getAbsolutePath());
            return null;
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            T data = gson.fromJson(reader, typeOfT);
            if (data == null) {
                System.err.println("[JsonUtil] Parsed JSON is null for file: " + file.getAbsolutePath());
            }
            return data;
        } catch (Exception e) {
            System.err.println("[JsonUtil] Failed to read JSON from: " + file.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
    }


    public static void write(File file, Object data) {
        ensureParentDir(file);
        backupFile(file);
        writeToTempAndReplace(file, data);
    }
    private static void ensureParentDir(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    private static void backupFile(File file) {
        if (file.exists()) {
            File backup = new File(file.getAbsolutePath() + ".bak");
            try {
                Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("[HBM-Enhanced] Failed to create backup for: " + file.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    private static void writeToTempAndReplace(File targetFile, Object data) {
        File tempFile = new File(targetFile.getAbsolutePath() + ".tmp");

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("[HBM-Enhanced] Failed to write to temp file: " + tempFile.getAbsolutePath());
            e.printStackTrace();
            return;
        }

        try {
            Path tempPath = tempFile.toPath();
            Path targetPath = targetFile.toPath();

            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            System.err.println("[HBM-Enhanced] Failed to replace original file with temp file: " + targetFile.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
