package com.mewo.hbmenhanced;

import com.typesafe.config.ConfigException;

import java.io.*;
import java.util.Map;

import static com.mewo.hbmenhanced.commands.RPCommand.playerRPMap;

public class saveRPData {
    public static void saveRPData() throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter("playerRPData.txt");
            for (Map.Entry<String, Integer> entry : playerRPMap.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n"); // Store player RP as playerName:RP
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void loadRPData() {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader("playerRPData.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String playerName = parts[0];
                    int rp = Integer.parseInt(parts[1]);
                    playerRPMap.put(playerName, rp); // Load player RP into the map
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
