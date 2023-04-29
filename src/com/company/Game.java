package com.company;

import Entities.Player;
import Levels.LevelBuilder;
import Levels.LevelManager;

import javax.swing.*;
import java.awt.*;
import Levels.Playing;

public class Game implements Runnable{
    private GameWindow gameWindow;
    private GamePanel gamePanel = new GamePanel(this);
    public JLabel FPSlabel = new JLabel();
    private Thread gameThread;
    private final int FPS_SET = 144;
    private final int UPS_SET = 200;
    public int frames = 0;

    private Player player;
    private LevelManager levelManager;

    private Menu menu;
    private Playing playing;

    public final static int TILES_DEFAULT_SIZE = 16;
    public final static float SCALE = 3.0f;
    public final static int TILES_IN_WIDTH = 20;
    public final static int TILES_IN_HEIGHT = 15;
    public final static int TILES_SIZE = (int)(TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    public Game() {
        initClasses();
        FPSlabel.setForeground(Color.GREEN);
        FPSlabel.setBounds(2,2,80,10);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
        gamePanel.requestFocusInWindow();
        gamePanel.setLayout(null);
        gamePanel.add(FPSlabel);
        gameWindow = new GameWindow(gamePanel);
        startGameLoop();
    }

    private void initClasses() {
        levelManager = new LevelManager();
        player = new Player(50,533);
        playing = new Playing();

    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void update() {
        player.update();


    }

    public void render(Graphics g) {
        playing.render(g);
        player.render(g);

    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }

            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                //FPSlabel.setText(String.valueOf(frames) + " | " + String.valueOf(updates));
                FPSlabel.setText(String.valueOf(frames));
                frames = 0;
                updates = 0;

            }
        }
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public Player getPlayer() {
        return player;
    }

    public Menu getMenu() {
        return menu;
    }

    public Playing getPlaying() {
        return playing;
    }

}
