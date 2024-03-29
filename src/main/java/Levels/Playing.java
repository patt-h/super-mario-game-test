package Levels;

import Entities.Player;
import Objects.CoinBlock;
import Objects.Mushroom;


import static Objects.CoinBlock.coinBlocksList;
import static Objects.Mushroom.MushroomList;
import static Utilities.Constants.ObjectConstants.*;
import static Utilities.Constants.PlayerConstants.*;

import Utilities.LoadSave;
import com.company.Game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class Playing {
    private Player player;
    public boolean lobby = true;
    public static int[][] lvl;
    public LevelManager levelManager;
    private int bricksCounter;
    private int bricksDownCounter = 20;

    private BufferedImage[][] animations;
    private int animationTick, animationIndex, animationSpeed = 16;
    private int accurateAnimationRow;
    private int blockType;
    public static int lvlLength;

    public static int worldTime = 300;

    public Timer timeCounter;

    public Playing(Player player) {
        this.player = player;
        levelManager = new LevelManager();
        loadAnimation();
        initLevel();
    }

    public void initLevel() {
        lvl = player.lvl;
        for (int i = 0; i < lvl.length; i++) {
            for (int j = 0; j < lvl[i].length; j++) {
                lvlLength = lvl[i].length;
                int id = lvl[i][j];
            }
        }
    }

    public void getOutOfStartingPipe() {
        if (lobby) {
            player.x +=0.5;
            player.playerAction = SMALL_MARIO_WALK;
            if (player.x >= 3 * Game.TILES_SIZE) {
                lobby = false;
                player.setRight(false);
                player.inAir = true;
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

            for (CoinBlock cb : coinBlocksList) {
                if (cb.isActive() && cb.movedBlock) {
                    //lvl[player.movedCoinY / Game.TILES_SIZE][player.movedCoinX / Game.TILES_SIZE] = 90;
                    lvl[player.movedCoinY / Game.TILES_SIZE][cb.getMovedCoinBlockX() / Game.TILES_SIZE] = 90;
                    cb.coinBricksCounter++;
                    if (cb.coinBricksCounter < 20) {
                        //g.drawImage(levelManager.sprites.get(190), player.movedCoinX - LvlOffset, player.movedCoinY - cb.coinBricksCounter, 48, 48, null);
                        g.drawImage(levelManager.sprites.get(190), cb.getMovedCoinBlockX() - LvlOffset, player.movedCoinY - cb.coinBricksCounter, 48, 48, null);
                    }
                    if (cb.coinBricksCounter >= 20) {
                        cb.coinBricksDownCounter -= 2;
                        if (cb.movedBlock) {
                            //g.drawImage(levelManager.sprites.get(190), player.movedCoinX - LvlOffset, player.movedCoinY - cb.coinBricksDownCounter, 48, 48, null);
                            g.drawImage(levelManager.sprites.get(190), cb.getMovedCoinBlockX() - LvlOffset, player.movedCoinY - cb.coinBricksDownCounter, 48, 48, null);
                            if (cb.coinBricksDownCounter == 0) {
                                cb.movedBlock = false;
                                player.movedCoin = false;
                                //lvl[player.movedCoinY / Game.TILES_SIZE][player.movedCoinX / Game.TILES_SIZE] = 191;
                                lvl[player.movedCoinY / Game.TILES_SIZE][cb.getMovedCoinBlockX() / Game.TILES_SIZE] = 191;
                                cb.coinBricksCounter = 0;
                                cb.coinBricksDownCounter = 20;
                            }
                            else if (cb.coinBricksDownCounter > 0) {
                                player.movedCoin = true;
                            }
                        }
                    }
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