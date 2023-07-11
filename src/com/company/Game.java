package com.company;

import Entities.EnemyManager;
import Entities.Player;
import Levels.LevelManager;

import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import Levels.Playing;
import Objects.ObjectManager;
import Utilities.LoadSave;

public class Game implements Runnable{
    private GameWindow gameWindow;
    private GamePanel gamePanel = new GamePanel(this);
    public JLabel FPSlabel = new JLabel();
    private JLabel playerLabel = new JLabel();
    private JLabel scoreLabel = new JLabel();
    private JLabel coinsLabel = new JLabel();
    private JLabel worldLabel = new JLabel();
    private JLabel timeLabel = new JLabel();
    private JLabel timerLabel = new JLabel();
    private Thread gameThread;
    private final int FPS_SET = 144;
    private final int UPS_SET = 200;
    public int frames = 0;

    Font font;

    private Player player;
    private LevelManager levelManager;
    private ObjectManager objectManager;
    private EnemyManager enemyManager;
    private BufferedImage backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.BACKGROUND_IMG);

    private Menu menu;
    private Playing playing;

    private int aniTick, aniIndex, aniSpeed;

    public final static int TILES_DEFAULT_SIZE = 16;
    public final static int SCALE = 3;
    public final static int TILES_IN_WIDTH = 20;
    public final static int TILES_IN_HEIGHT = 15;
    public final static int TILES_SIZE = TILES_DEFAULT_SIZE * SCALE;
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    public Game() {
        initClasses();
        FPSlabel.setForeground(Color.GREEN);
        FPSlabel.setBounds(2, 2, 80, 10);
        playerLabel.setBounds(15,15,160,30);
        scoreLabel.setBounds(15,35,160,30);
        coinsLabel.setBounds(350,35,120,30);
        worldLabel.setBounds(595,15,120,30);
        timeLabel.setBounds(GAME_WIDTH-95, 15,120,30);
        timerLabel.setBounds(GAME_WIDTH-76, 35,120,30);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
        gamePanel.requestFocusInWindow();
        gamePanel.setLayout(null);
        gamePanel.add(FPSlabel);
        gameWindow = new GameWindow(gamePanel);
        startGameLoop();

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("Resources/font.ttf")).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        playerLabel.setFont(font);
        playerLabel.setForeground(Color.WHITE);

        scoreLabel.setFont(font);
        scoreLabel.setForeground(Color.WHITE);

        coinsLabel.setFont(font);
        coinsLabel.setForeground(Color.WHITE);

        worldLabel.setFont(font);
        worldLabel.setForeground(Color.WHITE);

        timeLabel.setFont(font);
        timeLabel.setForeground(Color.WHITE);

        timerLabel.setFont(font);
        timerLabel.setForeground(Color.WHITE);

        gamePanel.add(playerLabel);
        gamePanel.add(scoreLabel);
        gamePanel.add(coinsLabel);
        gamePanel.add(worldLabel);
        gamePanel.add(timeLabel);
        gamePanel.add(timerLabel);
    }

    private void initClasses() {
        levelManager = new LevelManager();
        player = new Player(50,200);
        playing = new Playing();

        //THIS WILL BE MOVED TO CLASS WHERE MAP WILL BE INITIALIZED
        playing.timeCounter();
        playing.timeCounter.start();

        objectManager = new ObjectManager();
        enemyManager = new EnemyManager();
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        objectManager.update();
        enemyManager.update();
        player.update();
    }

    public void render(Graphics g) {
        g.drawImage(backgroundImg,0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT, null);
        objectManager.draw(g, Playing.xLvlOffset);
        enemyManager.draw(g, Playing.xLvlOffset);
        playing.render(g, Playing.xLvlOffset);
        player.render(g, Playing.xLvlOffset);

        //COINS SECTION ON HUD
        miniCoinAniIndex();
        g.drawImage(objectManager.animations[8][aniIndex], 330,43,14,16,null);
        //OUTLINED TEXT ON HUD
        outlineShape(g);
    }

    private void outlineShape(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(15, 30, 60));

        //MARIO
        String mario = "MARIO *" + Playing.lives;
        GlyphVector glyphVectorMario = font.createGlyphVector(g2.getFontRenderContext(), mario);
        Shape marioShape = glyphVectorMario.getOutline();

        g2.translate(16,35);
        g2.setStroke(new BasicStroke(3.5f));
        g2.draw(marioShape);
        g2.translate(-16,-35);

        //SCORE
        String score = String.format("%07d", Playing.score);
        GlyphVector glyphVectorScore = font.createGlyphVector(g2.getFontRenderContext(), score);
        Shape scoreShape = glyphVectorScore.getOutline();

        g2.translate(16,55);
        g2.setStroke(new BasicStroke(3.5f));
        g2.draw(scoreShape);
        g2.translate(-16,-55);

        //COINS
        String coins = "^ " + String.format("%02d", Playing.coins);
        GlyphVector glyphVectorCoins = font.createGlyphVector(g2.getFontRenderContext(), coins);
        Shape coinsShape = glyphVectorCoins.getOutline();

        g2.translate(350,55);
        g2.setStroke(new BasicStroke(3.5f));
        g2.draw(coinsShape);
        g2.translate(-350,-55);

        //WORLD
        String world = "WORLD";
        GlyphVector glyphVectorWorld = font.createGlyphVector(g2.getFontRenderContext(), world);
        Shape worldShape = glyphVectorWorld.getOutline();

        g2.translate(596,35);
        g2.setStroke(new BasicStroke(3.5f));
        g2.draw(worldShape);
        g2.translate(-596,-35);

        //TIME
        String time = "TIME";
        GlyphVector glyphVectorTime = font.createGlyphVector(g2.getFontRenderContext(), time);
        Shape timeShape = glyphVectorTime.getOutline();

        g2.translate(GAME_WIDTH-94,35);
        g2.setStroke(new BasicStroke(3.5f));
        g2.draw(timeShape);
        g2.translate(-(GAME_WIDTH-94),-35);

        //TIME
        String timer = String.format("%03d", playing.worldTime);
        GlyphVector glyphVectorTimer = font.createGlyphVector(g2.getFontRenderContext(), timer);
        Shape timerShape = glyphVectorTimer.getOutline();

        g2.translate(GAME_WIDTH-75,55);
        g2.setStroke(new BasicStroke(3.5f));
        g2.draw(timerShape);
        g2.translate(-(GAME_WIDTH-75),-55);


        playerLabel.setText(mario);
        scoreLabel.setText(score);
        coinsLabel.setText(coins);
        worldLabel.setText(world);
        timeLabel.setText(time);
        timerLabel.setText(timer);
    }

    private void miniCoinAniIndex() {
        aniSpeed = 12;
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= 3) {
                aniIndex = 0;
            }
        }
    }

    private void timer() {

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

    public ObjectManager getObjectManager() {
        return objectManager;
    }
}
