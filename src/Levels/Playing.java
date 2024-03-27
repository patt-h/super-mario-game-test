package Levels;

import Entities.Goomba;
import Entities.Piranha;
import Entities.Player;
import Entities.Troopa;
import Objects.Coin;
import Objects.Mushroom;

import static Entities.Goomba.GoombaList;
import static Entities.Piranha.PiranhaList;
import static Entities.Troopa.TroopaList;
import static Levels.MapObjects.coinBlocksList;
import static Objects.Coin.CoinList;
import static Objects.FireFlower.FireFlowerList;
import static Objects.Mushroom.MushroomList;
import static Objects.WarpPipe.WarpPipesMap;
import static Utilities.Constants.ObjectConstants.*;

import Objects.WarpPipe;
import Utilities.LoadSave;
import com.company.Game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class Playing {
    private Player player;
    public static int[][] lvl;
    public LevelManager levelManager;
    private int bricksCounter;
    private int bricksDownCounter = 20;

    private BufferedImage[][] animations;
    private int animationTick, animationIndex, animationSpeed = 16;
    private int accurateAnimationRow;
    private int blockType;
    public static int lvlLength;

    public int startX = 50;
    public int startY = 200;
    public static int worldTime = 300;
    public static String worldName = "TEST";

    public Timer timeCounter;

    public Playing(Player player) {
        this.player = player;
        levelManager = new LevelManager();
        loadAnimation();
        initEntities();
    }

    public void initEntities() {
        lvl = player.lvl;
        CoinList.clear();
        GoombaList.clear();
        TroopaList.clear();
        PiranhaList.clear();
        MushroomList.clear();
        FireFlowerList.clear();
        coinBlocksList.clear();

//        CoinList.add(new Coin(200,200, COIN));
//        GoombaList.add(new Goomba(600,200));
//        GoombaList.add(new Goomba(800,200));
//        GoombaList.add(new Goomba(850,200));
//        GoombaList.add(new Goomba(870,200));
//        GoombaList.add(new Goomba(890,200));
//        GoombaList.add(new Goomba(900,200));
//        GoombaList.add(new Goomba(920,200));
//        GoombaList.add(new Goomba(930,200));
//        GoombaList.add(new Goomba(1030,200));
//        TroopaList.add(new Troopa(1400,500));
//        PiranhaList.add(new Piranha(456,490));
//        WarpPipesMap.put(new WarpPipe(456, 470, WARPPIPE), new WarpPipe(1665, 470, WARPPIPE));
        for (int i = 0; i < lvl.length; i++) {
            for (int j = 0; j < lvl[i].length; j++) {
                lvlLength = lvl[i].length;
                int id = lvl[i][j];
                if (levelManager.sprites.get(id) == levelManager.sprites.get(191)) {
                    coinBlocksList.add(new MapObjects(j*Game.TILES_SIZE, i*Game.TILES_SIZE));
                }
            }
        }
    }

    public void timeCounter() {
        timeCounter = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worldTime--;
                if (worldTime == 0) {
                    timeCounter.stop();
                }
            }
        });
    }

    public void render(Graphics g, int LvlOffset) {
        updateAnimationTick();
        for (int i = 0; i < lvl.length; i++) {
            for (int j = 0; j < lvl[i].length; j++) {
                int id = lvl[i][j];
                if (levelManager.sprites.get(id) == levelManager.sprites.get(115)
                        || levelManager.sprites.get(id) == levelManager.sprites.get(114)) {
                    g.drawImage(animations[accurateAnimationRow][animationIndex], j * 48 - LvlOffset, i * 48, 48, 48, null);
                } else if (levelManager.sprites.get(id) == levelManager.sprites.get(191)) {
                    g.drawImage(levelManager.sprites.get(190), j * 48 - LvlOffset, i * 48, 48, 48, null);
                } else {
                    g.drawImage(levelManager.sprites.get(id), j * 48 - LvlOffset, i * 48, 48, 48, null);
                }
            }
        }

        for (MapObjects cb : coinBlocksList) {
            if (cb.coinsInside == 0 && lvl[(int)cb.hitbox.y / Game.TILES_SIZE][(int)cb.hitbox.x / Game.TILES_SIZE] == 191) {
                lvl[(int)cb.hitbox.y / Game.TILES_SIZE][(int)cb.hitbox.x / Game.TILES_SIZE] = 152;
            }
            if (cb.isActive() && cb.movedBlock) {
                lvl[player.movedCoinY / Game.TILES_SIZE][player.movedCoinX / Game.TILES_SIZE] = 90;
                lvl[player.movedCoinY / Game.TILES_SIZE][cb.getMovedCoinBlockX() / Game.TILES_SIZE] = 90;
                cb.coinBricksCounter++;
                if (cb.coinBricksCounter < 20) {
                    g.drawImage(levelManager.sprites.get(190), player.movedCoinX - LvlOffset, player.movedCoinY - cb.coinBricksCounter, 48, 48, null);
                    g.drawImage(levelManager.sprites.get(190), cb.getMovedCoinBlockX() - LvlOffset, player.movedCoinY - cb.coinBricksCounter, 48, 48, null);
                }
                if (cb.coinBricksCounter >= 20) {
                    cb.coinBricksDownCounter -= 2;
                    if (player.movedCoin) {
                        g.drawImage(levelManager.sprites.get(190), player.movedCoinX - LvlOffset, player.movedCoinY - cb.coinBricksDownCounter, 48, 48, null);
                        g.drawImage(levelManager.sprites.get(190), cb.getMovedCoinBlockX() - LvlOffset, player.movedCoinY - cb.coinBricksDownCounter, 48, 48, null);
                        if (cb.coinBricksDownCounter == 0) {
                            cb.movedBlock = false;
                            lvl[player.movedCoinY / Game.TILES_SIZE][player.movedCoinX / Game.TILES_SIZE] = 191;
                            lvl[player.movedCoinY / Game.TILES_SIZE][cb.getMovedCoinBlockX() / Game.TILES_SIZE] = 191;
                            cb.coinBricksCounter = 0;
                            cb.coinBricksDownCounter = 20;
                        }
                    }
                }
            }
        }

        if (player.moved || player.movedCoin) {
            //MUSHROOM REACTION FOR BRICKS
            for (Mushroom m : MushroomList) {
                if (m.x >= player.movedX - (Game.TILES_SIZE / 2) && m.x <= player.movedX + Game.TILES_SIZE) {
                    m.y -= 4;
                    m.inAir = true;
                }
                else if (m.x >= player.movedCoinX - (Game.TILES_SIZE / 2) && m.x <= player.movedCoinX + Game.TILES_SIZE) {
                    m.y -= 4;
                    m.inAir = true;
                }
            }

            if (bricksCounter < 20) {
                if (player.moved) {
                    g.drawImage(levelManager.sprites.get(190), player.leftMovedX - LvlOffset, player.movedY - bricksCounter, 48, 48, null);
                    g.drawImage(levelManager.sprites.get(190), player.rightMovedX - LvlOffset, player.movedY - bricksCounter, 48, 48, null);
                }
            }
            if (player.moved) {
                lvl[player.movedY / Game.TILES_SIZE][player.leftMovedX / Game.TILES_SIZE] = 90;
                lvl[player.movedY / Game.TILES_SIZE][player.rightMovedX / Game.TILES_SIZE] = 90;
                bricksCounter++;
            }
            if (bricksCounter >= 20) {
                bricksDownCounter -= 2;
                if (player.moved) {
                    g.drawImage(levelManager.sprites.get(190), player.leftMovedX - LvlOffset, player.movedY - bricksDownCounter, 48, 48, null);
                    g.drawImage(levelManager.sprites.get(190), player.rightMovedX - LvlOffset, player.movedY - bricksDownCounter, 48, 48, null);
                    if (bricksDownCounter == 0) {
                        player.moved = false;
                        lvl[player.movedY / Game.TILES_SIZE][player.leftMovedX / Game.TILES_SIZE] = 190;
                        lvl[player.movedY / Game.TILES_SIZE][player.rightMovedX / Game.TILES_SIZE] = 190;
                        bricksCounter = 0;
                        bricksDownCounter = 20;
                    }
                }
            }
        }
    }

    private void loadAnimation() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.POWERUPS_ATLAS);

        animations = new BufferedImage[5][11];
        for (int y = 0; y < animations.length; y++) {
            for (int x = 0; x < animations[y].length; x++) {
                animations[y][x] = img.getSubimage((x*16)+x+1,(y*16)+y+1,16,16);
            }
        }
    }

    private void updateAnimationTick() {
        animationTick++;
        if (blockType == PRIZE_BLOCK) {
            accurateAnimationRow = 3;
        }
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= getSpriteAmount(blockType)) {
                animationIndex = 0;
            }
        }
    }
}