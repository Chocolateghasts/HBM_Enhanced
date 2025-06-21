package com.mewo.hbmenhanced;

import org.lwjgl.input.Keyboard;

public class PongGame {
    private int paddleLeftY = 60;
    private int paddleRightY = 60;
    private float ballX = 88;
    private float ballY = 60;
    private float ballSpeedX = 3;
    private float ballSpeedY = 3;
    private int playerScore = 0;
    private int aiScore = 0;

    public static final int PADDLE_HEIGHT = 30;
    public static final int PADDLE_WIDTH = 5;
    public static final int BALL_SIZE = 4;

    public void handleInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            paddleLeftY -= 4;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            paddleLeftY += 4;
        }
        paddleLeftY = Math.max(0, Math.min(90, paddleLeftY));
    }

    public void update() {
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        if (ballY > paddleRightY + PADDLE_HEIGHT/2) {
            paddleRightY += 2;
        } else if (ballY < paddleRightY + PADDLE_HEIGHT/2) {
            paddleRightY -= 2;
        }

        if (ballX <= 10 && ballY >= paddleLeftY && ballY <= paddleLeftY + PADDLE_HEIGHT) {
            ballSpeedX = -ballSpeedX;
        }
        if (ballX >= 166 && ballY >= paddleRightY && ballY <= paddleRightY + PADDLE_HEIGHT) {
            ballSpeedX = -ballSpeedX;
        }

        if (ballY <= 0 || ballY >= 120) {
            ballSpeedY = -ballSpeedY;
        }

        if (ballX <= 0) {
            aiScore++;
            resetBall();
        }
        if (ballX >= 176) {
            playerScore++;
            resetBall();
        }
    }

    private void resetBall() {
        ballX = 88;
        ballY = 60;
        ballSpeedX = (Math.random() > 0.5 ? 3 : -3);
        ballSpeedY = (Math.random() > 0.5 ? 3 : -3);
    }

    public int getPaddleLeftY() { return paddleLeftY; }
    public int getPaddleRightY() { return paddleRightY; }
    public int getBallX() { return (int)ballX; }
    public int getBallY() { return (int)ballY; }
    public int getPlayerScore() { return playerScore; }
    public int getAiScore() { return aiScore; }
}