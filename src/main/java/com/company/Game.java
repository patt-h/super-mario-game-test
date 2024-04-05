package com.company;

import Actions.PlayerDeath;
import Entities.EnemyManager;
import Entities.Player;
import Levels.LevelBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.prefs.Preferences;

import Levels.Playing;
import Objects.ObjectManager;
import Objects.WarpPipe;
import Utilities.LoadSave;
import Visuals.VisualsManager;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import static Objects.WarpPipe.WorldWarpPipesMap;
import static Utilities.Constants.PlayerConstants.DEAD;

public class Game implements Runnable{
    private GameWindow gameWindow;
    private GamePanel gamePanel = new GamePanel(this);
    public JLabel FPSlabel = new JLabel();
    private Thread gameThread;
    private final int FPS_SET = 144;
    private final int UPS_SET = 200;

    public static Font font;
    public static Font smallerFont;

    private Player player;
    private ObjectManager objectManager;
    private EnemyManager enemyManager;
    private VisualsManager visualsManager;
    private BufferedImage backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.BACKGROUND_IMG);

    private Playing playing;
    private PlayerDeath playerDeath;

    private int aniTick, aniIndex, aniSpeed;

    public final static int TILES_DEFAULT_SIZE = 16;
    public final static int SCALE = 3;
    public final static int TILES_IN_WIDTH = 20;
    public final static int TILES_IN_HEIGHT = 15;
    public final static int TILES_SIZE = TILES_DEFAULT_SIZE * SCALE;
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    public static boolean debugMode;
    public static boolean changeWorld = false;
    public static String world = "lobby";

    public static ArrayList<String> lobbyWorldValues = new ArrayList<>();

    public Game() {
        loadSave();
        initClasses();
        FPSlabel.setForeground(Color.GREEN);
        FPSlabel.setBounds(2, 2, 80, 10);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
        gamePanel.requestFocusInWindow();
        gamePanel.setLayout(null);
        gamePanel.add(FPSlabel);
        gameWindow = new GameWindow(gamePanel);
        startGameLoop();

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("Resources/font.ttf")).deriveFont(20f);
            smallerFont = Game.font.deriveFont(16f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    private void loadSave() {
        try {
            Ini ini = new Ini(new File("save.ini"));
            Preferences preferences = new IniPreferences(ini);
            for (String section : ini.keySet()) {
                if (preferences.node(section).get("world", null) != null) {
                    lobbyWorldValues.add(preferences.node(section).get("world", null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initClasses() {
        player = new Player(0,370);
        objectManager = new ObjectManager(player);
        enemyManager = new EnemyManager(player);
        visualsManager = new VisualsManager();
        playing = new Playing(player);

        playerDeath = new PlayerDeath();
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        objectManager.update();
        enemyManager.update();
        player.update();

        //RESETTING WHOLE MAP AFTER DEATH
        if (player.playerStatus == DEAD && player.y > 6 * GAME_HEIGHT) {
            playerDeath.reloadWorld(player, playing, enemyManager, objectManager);
        }

        if (changeWorld) {
            load();
            GameStates.gameStates = GameStates.PLAYING;
        }

        if (playing.lobby) {
            playing.getOutOfStartingPipe();
        }
    }

    public void load() {
        player.resetDirBooleans();
        enemyManager.resetEnemy();
        objectManager.resetObjects();
        visualsManager.resetObjects();
        player.lvl = LevelBuilder.getLevelData();
        player.x = LevelBuilder.startingX;
        player.y = LevelBuilder.startingY;
        player.initBorderDistance();
        player.inAir = true;
        playing.initLevel();
        playing.timeCounter();
        playing.timeCounter.start();
        changeWorld = false;
    }

    public void render(Graphics g) {
        if (player.playerStatus != DEAD) {
            g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
            visualsManager.draw(g, player.xLvlOffset);
            objectManager.draw(g, player.xLvlOffset);
            enemyManager.draw(g, player.xLvlOffset);
            player.render(g, player.xLvlOffset);
            playing.render(g, player.xLvlOffset);
        }
        else {
            g.drawImage(backgroundImg,0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT, null);
            visualsManager.draw(g, player.xLvlOffset);
            objectManager.draw(g, player.xLvlOffset);
            enemyManager.draw(g, player.xLvlOffset);
            playing.render(g, player.xLvlOffset);
            player.render(g, player.xLvlOffset);
        }

        if (GameStates.gameStates == GameStates.PLAYING) {
            //COINS SECTION ON HUD
            miniCoinAniIndex();
            g.drawImage(objectManager.animations[8][aniIndex], 330, 42, 14, 16, null);
            //OUTLINED TEXT ON HUD
            outlineShape(g);
        }

        if (GameStates.gameStates == GameStates.LOBBY) {
            outlineShapeLobby(g);
        }
    }

    private void outlineShapeLobby(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        GlyphVector glyphVectorWorldString = smallerFont.createGlyphVector(g2.getFontRenderContext(), "WORLD");
        Shape lobbyWorldStringShape = glyphVectorWorldString.getOutline();

        for (Map.Entry<WarpPipe, String> worldPipes : WorldWarpPipesMap.entrySet()) {
            String lobbyWorld = worldPipes.getValue();
            String[] parts = lobbyWorld.split("-");
            lobbyWorld = parts[0].trim();
            g2.setColor(new Color(15, 30, 60));

            GlyphVector glyphVectorLobbyWorld = smallerFont.createGlyphVector(g2.getFontRenderContext(), lobbyWorld);
            Shape lobbyWorldShape = glyphVectorLobbyWorld.getOutline();

            g2.translate(worldPipes.getKey().x + 15,worldPipes.getKey().y - 3 * TILES_SIZE);
            g2.setStroke(new BasicStroke(3.5f));
            g2.draw(lobbyWorldShape);
            g2.setColor(Color.WHITE);
            g2.fill(lobbyWorldShape);
            g2.translate(-(worldPipes.getKey().x + 15),-(worldPipes.getKey().y - 3 * TILES_SIZE));

            g2.setColor(new Color(15, 30, 60));
            g2.translate(worldPipes.getKey().x - 18,worldPipes.getKey().y - 3.5 * TILES_SIZE);
            g2.setStroke(new BasicStroke(3.5f));
            g2.draw(lobbyWorldStringShape);
            g2.setColor(Color.WHITE);
            g2.fill(lobbyWorldStringShape);
            g2.translate(-(worldPipes.getKey().x - 18),-(worldPipes.getKey().y - 3.5 * TILES_SIZE));
        }
    }

    private void outlineShape(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3.5f));

        //MARIO
        String mario = "MARIO *" + player.lives;
        GlyphVector glyphVectorMario = font.createGlyphVector(g2.getFontRenderContext(), mario);
        Shape marioShape = glyphVectorMario.getOutline();

        g2.setColor(new Color(15, 30, 60));
        g2.translate(16,35);
        g2.draw(marioShape);
        g2.setColor(Color.WHITE);
        g2.fill(marioShape);
        g2.translate(-16,-35);

        //SCORE
        String score = String.format("%07d", player.score);
        GlyphVector glyphVectorScore = font.createGlyphVector(g2.getFontRenderContext(), score);
        Shape scoreShape = glyphVectorScore.getOutline();

        g2.setColor(new Color(15, 30, 60));
        g2.translate(16,55);
        g2.draw(scoreShape);
        g2.setColor(Color.WHITE);
        g2.fill(scoreShape);
        g2.translate(-16,-55);

        //COINS
        String coins = "^ " + String.format("%02d", player.coins);
        GlyphVector glyphVectorCoins = font.createGlyphVector(g2.getFontRenderContext(), coins);
        Shape coinsShape = glyphVectorCoins.getOutline();

        g2.setColor(new Color(15, 30, 60));
        g2.translate(350,55);
        g2.draw(coinsShape);
        g2.setColor(Color.WHITE);
        g2.fill(coinsShape);
        g2.translate(-350,-55);

        //WORLD
        String world = "WORLD";
        GlyphVector glyphVectorWorld = font.createGlyphVector(g2.getFontRenderContext(), world);
        Shape worldShape = glyphVectorWorld.getOutline();

        g2.setColor(new Color(15, 30, 60));
        g2.translate(596,35);
        g2.draw(worldShape);
        g2.setColor(Color.WHITE);
        g2.fill(worldShape);
        g2.translate(-596,-35);

        //WORLD NAME
        GlyphVector glyphVectorWorldName = font.createGlyphVector(g2.getFontRenderContext(), this.world);
        Shape worldNameShape = glyphVectorWorldName.getOutline();

        g2.setColor(new Color(15, 30, 60));
        g2.translate(618,55);
        g2.draw(worldNameShape);
        g2.setColor(Color.WHITE);
        g2.fill(worldNameShape);
        g2.translate(-618,-55);

        //TIME
        String time = "TIME";
        GlyphVector glyphVectorTime = font.createGlyphVector(g2.getFontRenderContext(), time);
        Shape timeShape = glyphVectorTime.getOutline();

        g2.setColor(new Color(15, 30, 60));
        g2.translate(GAME_WIDTH-94,35);
        g2.draw(timeShape);
        g2.setColor(Color.WHITE);
        g2.fill(timeShape);
        g2.translate(-(GAME_WIDTH-94),-35);

        //TIME
        String timer = String.format("%03d", playing.worldTime);
        GlyphVector glyphVectorTimer = font.createGlyphVector(g2.getFontRenderContext(), timer);
        Shape timerShape = glyphVectorTimer.getOutline();

        g2.setColor(new Color(15, 30, 60));
        g2.translate(GAME_WIDTH-75,55);
        g2.draw(timerShape);
        g2.setColor(Color.WHITE);
        g2.fill(timerShape);
        g2.translate(-(GAME_WIDTH-75),-55);
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

    @Override
    public void run() {
        float timePerFrame = 1000000000f / FPS_SET;
        float timePerUpdate = 1000000000f / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.nanoTime();

        float deltaU = 0;
        float deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();
            long elapsedTime = currentTime - previousTime;
            previousTime = currentTime;

            deltaU += elapsedTime / timePerUpdate;
            deltaF += elapsedTime / timePerFrame;

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

            if (System.nanoTime() - lastCheck >= 1000000000L) {
                lastCheck = System.nanoTime();
                // FPSlabel.setText(String.valueOf(frames) + " | " + String.valueOf(updates));
                FPSlabel.setText(String.valueOf(frames));
                frames = 0;
                updates = 0;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public Player getPlayer() {
        return player;
    }


    public Playing getPlaying() {
        return playing;
    }

    public ObjectManager getObjectManager() {
        return objectManager;
    }
}
