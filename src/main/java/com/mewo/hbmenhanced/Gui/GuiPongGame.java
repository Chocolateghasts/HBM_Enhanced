package com.mewo.hbmenhanced.Gui;

import com.mewo.hbmenhanced.PongGame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuiPongGame extends GuiScreen {
    private PongGame pongGame;
    private long lastUpdateTime;
    private static final int GAME_WIDTH = 176;
    private static final int GAME_HEIGHT = 120;
    private static final String AD_DIRECTORY = "resources/assets/hbmenhanced/textures/ponggameads/";
    private List<File> ads;
    private int currentAdIndex = 0;
    private long adStartTime;
    private boolean isVideoPlaying = false;

    public GuiPongGame() {
        this.pongGame = new PongGame();
        this.lastUpdateTime = System.currentTimeMillis();
        this.ads = loadAds();
        this.adStartTime = System.currentTimeMillis();
    }

    private List<File> loadAds() {
        List<File> adFiles = new ArrayList<>();
        File directory = new File(AD_DIRECTORY);
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".png") || file.getName().endsWith(".mp4")) {
                    adFiles.add(file);
                }
            }
        }
        return adFiles;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 30, 5, 60, 20, "Close"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        int gameX = (this.width - GAME_WIDTH) / 2;
        int gameY = (this.height - GAME_HEIGHT) / 2;

        // Draw gray border around the Pong game
        drawRect(gameX - 2, gameY - 2, gameX + GAME_WIDTH + 2, gameY + GAME_HEIGHT + 2, 0xFF808080);
        drawRect(gameX, gameY, gameX + GAME_WIDTH, gameY + GAME_HEIGHT, 0xFF000000);

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > 16) {
            pongGame.handleInput();
            pongGame.update();
            lastUpdateTime = currentTime;
        }

        // Draw Pong game elements
        drawRect(gameX + 10, gameY + pongGame.getPaddleLeftY(),
                gameX + 10 + PongGame.PADDLE_WIDTH,
                gameY + pongGame.getPaddleLeftY() + PongGame.PADDLE_HEIGHT,
                0xFFFFFFFF);

        drawRect(gameX + 166, gameY + pongGame.getPaddleRightY(),
                gameX + 166 + PongGame.PADDLE_WIDTH,
                gameY + pongGame.getPaddleRightY() + PongGame.PADDLE_HEIGHT,
                0xFFFFFFFF);

        drawRect(gameX + pongGame.getBallX(), gameY + pongGame.getBallY(),
                gameX + pongGame.getBallX() + PongGame.BALL_SIZE,
                gameY + pongGame.getBallY() + PongGame.BALL_SIZE,
                0xFFFFFFFF);

        this.fontRendererObj.drawString("Player: " + pongGame.getPlayerScore(), gameX + 20, gameY + 10, 0xFFFFFF);
        this.fontRendererObj.drawString("AI: " + pongGame.getAiScore(), gameX + 130, gameY + 10, 0xFFFFFF);

        // Draw ad frame below the Pong game
        int adFrameHeight = 30;
        int adFrameY = gameY + GAME_HEIGHT + 10;
        drawRect(gameX - 2, adFrameY - 2, gameX + GAME_WIDTH + 2, adFrameY + adFrameHeight + 2, 0xFF808080);

        // Display current ad
        if (!ads.isEmpty()) {
            File currentAd = ads.get(currentAdIndex);
            if (currentAd.getName().endsWith(".png")) {
                // Display image
                this.mc.getTextureManager().bindTexture(new ResourceLocation("hbmenhanced", "textures/ponggameads/" + currentAd.getName()));
                drawTexturedModalRect(gameX, adFrameY, 0, 0, GAME_WIDTH, adFrameHeight);

                // Check if 30 seconds have passed
                if (currentTime - adStartTime > 30000) {
                    nextAd();
                }
            } else if (currentAd.getName().endsWith(".mp4")) {
                // Play video
                if (!isVideoPlaying) {
                    playVideo(currentAd);
                    isVideoPlaying = true;
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void nextAd() {
        adStartTime = System.currentTimeMillis();
        isVideoPlaying = false;
        currentAdIndex = (currentAdIndex + 1) % ads.size();
    }

    private void playVideo(File videoFile) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Use a video player library like VLCJ or JavaFX to play the video
                // Example: VLCJ or JavaFX MediaPlayer
                System.out.println("Playing video: " + videoFile.getAbsolutePath());
                // After video ends, move to the next ad
                nextAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}