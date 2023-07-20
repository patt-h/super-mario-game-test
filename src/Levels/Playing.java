package Levels;

import Entities.Entity;
import Entities.Goomba;
import Entities.Player;
import Entities.Troopa;
import Objects.Coin;
import Objects.FireFlower;
import Objects.Mushroom;

import static Entities.Goomba.GoombaList;
import static Entities.Troopa.TroopaList;
import static Levels.MapObjects.coinBlocksList;
import static Objects.Coin.CoinList;
import static Objects.FireFlower.FireFlowerList;
import static Objects.Mushroom.MushroomList;
import static Utilities.Constants.Directions.LEFT;
import static Utilities.Constants.Directions.RIGHT;
import static Utilities.Constants.PlayerConstants.SMALL;
import static Utilities.Constants.ObjectConstants.*;
import Utilities.LoadSave;
import com.company.Game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class Playing {
    public static int[][] lvl = LevelBuilder.getLevelData();
    public static LevelManager levelManager;
    public static boolean collision;
    private static boolean moved = false;
    private static boolean movedCoin = false;
    private static boolean broken = false;
    private static int movedX, movedY;
    private static int movedCoinX, movedCoinY;
    private static int brokenX, brokenY;
    private int bricksCounter;
    private int coinBricksCounter;
    private int bricksDownCounter = 20;
    private int coinBricksDownCounter = 20;
    public static float distanceX;
    private static float distanceY;

    public static int xLvlOffset;
    private static int leftBorder = (int)(0.5 * Game.GAME_WIDTH);
    private static int rightBorder = (int)(0.5 * Game.GAME_WIDTH);
    private static int lvlTilesWide = lvl[0].length;
    private static int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
    private static int maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE;

    private BufferedImage[][] animations;
    private int animationTick, animationIndex, animationSpeed = 16;
    private int accurateAnimationRow;
    private int blockType;
    public static int lvlLenght;

    public static int lives = 4;
    public static int score;
    public static int coins;
    public static int worldTime = 300;

    public Timer timeCounter;

    public Playing() {
        loadAnimation();
        levelManager = new LevelManager();
        CoinList.add(new Coin(200,200, COIN));
        GoombaList.add(new Goomba(200,200));
        TroopaList.add(new Troopa(500,200));
        for (int i = 0; i < lvl.length; i++) {
            for (int j = 0; j < lvl[i].length; j++) {
                lvlLenght = lvl[i].length;
                int id = lvl[i][j];
                if (levelManager.sprites.get(id) == levelManager.sprites.get(191)) {
                    coinBlocksList.add(new MapObjects(j*Game.TILES_SIZE, i*Game.TILES_SIZE));
                }
            }
        }
        coinBlocksList = MapObjects.getCoinBlocks();
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

    public static void checkCloseToBorder() {
        int playerX = (int)Player.x + 17;
        int diff = playerX - xLvlOffset;
        if (diff > rightBorder) {
            xLvlOffset += diff - rightBorder;
        } else if (diff < leftBorder) {
            xLvlOffset += diff - leftBorder;
        }

        if (xLvlOffset > maxLvlOffsetX) {
            xLvlOffset = maxLvlOffsetX;
        } else if (xLvlOffset < 0) {
            xLvlOffset = 0;
        }
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

        if (moved || movedCoin) {
            //MUSHROOM REACTION FOR BRICKS
            for (Mushroom m : MushroomList) {
                if (m.x >= movedX - (Game.TILES_SIZE / 2) && m.x <= movedX + Game.TILES_SIZE) {
                    m.y -= 4;
                    m.inAir = true;
                }
                if (m.x >= movedCoinX - (Game.TILES_SIZE / 2) && m.x <= movedCoinX + Game.TILES_SIZE) {
                    m.y -= 4;
                    m.inAir = true;
                }
            }
            if (bricksCounter < 20) {
                if (moved) {
                    g.drawImage(levelManager.sprites.get(190), movedX - LvlOffset, movedY - bricksCounter, 48, 48, null);
                }
            }
            if (coinBricksCounter < 20) {
                if (movedCoin) {
                    g.drawImage(levelManager.sprites.get(190), movedCoinX - LvlOffset, movedCoinY - coinBricksCounter, 48, 48, null);
                }
            }
            if (moved) {
                lvl[movedY / Game.TILES_SIZE][movedX / Game.TILES_SIZE] = 90;
                bricksCounter++;
            }
            if (movedCoin) {
                lvl[movedCoinY / Game.TILES_SIZE][movedCoinX / Game.TILES_SIZE] = 90;
                coinBricksCounter++;
            }
            if (bricksCounter >= 20) {
                bricksDownCounter -= 2;
                if (moved) {
                    g.drawImage(levelManager.sprites.get(190), movedX - LvlOffset, movedY - bricksDownCounter, 48, 48, null);
                    if (bricksDownCounter == 0) {
                        moved = false;
                        lvl[movedY / Game.TILES_SIZE][movedX / Game.TILES_SIZE] = 190;
                        bricksCounter = 0;
                        bricksDownCounter = 20;
                    }
                }
            }
            if (coinBricksCounter >= 20) {
                coinBricksDownCounter -= 2;
                if (movedCoin) {
                    g.drawImage(levelManager.sprites.get(190), movedCoinX - LvlOffset, movedCoinY - coinBricksDownCounter, 48, 48, null);
                    if (coinBricksDownCounter == 0) {
                        movedCoin = false;
                        lvl[movedCoinY / Game.TILES_SIZE][movedCoinX / Game.TILES_SIZE] = 191;
                        coinBricksCounter = 0;
                        coinBricksDownCounter = 20;
                    }
                }
            }
        }

        if (broken) {
            g.drawImage(levelManager.sprites.get(228), brokenX - LvlOffset, brokenY-50, 48, 48, null);
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

    public static void checkCollisions() {
        collision = false;

        if (coins == 100) {
            lives++;
            coins = 0;
        }

        int entityLeftX = (int) Entity.x;
        int entityRightX = (int) Entity.x + (int) Entity.hitbox.width;
        int entityTopY = (int) Entity.y;
        int entityBottomY = (int) Entity.y + (int) Entity.hitbox.height - 3;
        if (Player.duck) {
            entityTopY = (int) Entity.y + Game.TILES_SIZE - 3;
            entityBottomY = (int) Entity.y + Game.TILES_SIZE + (int) Entity.hitbox.height - 3;
        }
        int entityMiddleY = (int) Entity.y + (int) Entity.hitbox.height / 2;
        int entityMiddleRow = entityMiddleY / Game.TILES_SIZE;

        int entityLeftCol = entityLeftX / Game.TILES_SIZE;
        int entityRightCol = entityRightX / Game.TILES_SIZE;
        int entityTopRow = entityTopY / Game.TILES_SIZE;
        int entityBottomRow = entityBottomY / Game.TILES_SIZE;

        int tileNum1, tileNum2, tileNum3, tileNum4;

        //BLOCKING JUMPING ABOVE HIGHEST BLOCK
        if (Player.y < 0) {
            //COLLISION WHILE WALKING
            if (levelManager.sprites.get(lvl[0][entityRightCol + 1]) != levelManager.sprites.get(90) && Player.direction == RIGHT) {
                distanceX = (entityRightCol + 1) * Game.TILES_SIZE - entityRightX;
                if (distanceX <= 5) {
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
                    collision = true;
                }
            }
            if (levelManager.sprites.get(lvl[0][entityLeftCol - 1]) != levelManager.sprites.get(90) && Player.direction == LEFT) {
                distanceX = entityLeftX - (entityLeftCol) * Game.TILES_SIZE;
                if (distanceX <= 5) {
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
                    collision = true;
                }
            }
        }

        //COLLISIONS WHILE PLAYER IS TWO BLOCKS HIGH AND HALF OF PLAYER IS OUTSIDE OF MAP
        if (Player.y + 2*Game.TILES_SIZE > 0 && Player.y < 0 && Player.playerStatus != SMALL) {
            tileNum2 = lvl[entityBottomRow][entityRightCol];
            tileNum3 = lvl[entityBottomRow][entityLeftCol];

            //COLLISION WHILE WALKING
            if (levelManager.sprites.get(lvl[0][entityRightCol + 1]) != levelManager.sprites.get(90) && Player.direction == RIGHT) {
                distanceX = (entityRightCol + 1) * Game.TILES_SIZE - entityRightX;
                if (distanceX <= 5) {
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
                    collision = true;
                }
            }
            else if (levelManager.sprites.get(lvl[0][entityLeftCol - 1]) != levelManager.sprites.get(90) && Player.direction == LEFT) {
                distanceX = entityLeftX - (entityLeftCol) * Game.TILES_SIZE;
                if (distanceX <= 5) {
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
                    collision = true;
                }
            }
            //COLLISION WHEN FALLING
            if (Player.inAir
                    && (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90))) {
                distanceY = (entityBottomRow) * Game.TILES_SIZE - entityBottomY;
                if (distanceY < 0) {
                    Player.y = (entityBottomRow - 2) * Game.TILES_SIZE + (Game.TILES_SIZE*2 - Player.hitbox.height);
                    Player.jump = false;
                    Player.inAir = false;
                    Player.airSpeed = 0;
                    collision = true;
                }
            }
            //FALLING
            if (levelManager.sprites.get(lvl[entityBottomRow + 1][entityRightCol]) == levelManager.sprites.get(90)
                    && levelManager.sprites.get(lvl[entityBottomRow + 1][entityLeftCol]) == levelManager.sprites.get(90)
                    && levelManager.sprites.get(lvl[entityBottomRow][entityRightCol]) == levelManager.sprites.get(90)
                    && levelManager.sprites.get(lvl[entityBottomRow][entityLeftCol]) == levelManager.sprites.get(90)) {
                Player.inAir = true;
            }
        }

        //NORMAL COLLISIONS
        if (Player.y > 0) {
            switch (Entity.direction) {
                case RIGHT -> {
                    tileNum1 = lvl[entityTopRow][entityRightCol];
                    tileNum2 = lvl[entityBottomRow][entityRightCol];
                    tileNum3 = lvl[entityBottomRow][entityLeftCol];
                    tileNum4 = lvl[entityTopRow][entityLeftCol];

                    //HITTING INTERACTIVE BLOCKS

                    //HITTING BRICKS
                    if (Player.inAir && Player.airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(190)) || (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(190))) {
                        //WHEN PLAYER IS SMALL MARIO, BRICKS MOVE INSTEAD OF BREAKING
                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(190)) {
                            if (Player.playerStatus == SMALL) {
                                moved = true;
                                movedX = entityRightCol * 48;
                                movedY = entityTopRow * 48;
                            } else {
                                lvl[entityTopRow][entityRightCol] = 90;
                                broken = true;
                            }
                        }
                        if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(190)) {
                            if (Player.playerStatus == SMALL) {
                                moved = true;
                                movedX = entityLeftCol * 48;
                                movedY = entityTopRow * 48;
                            } else {
                                lvl[entityTopRow][entityLeftCol] = 90;
                                broken = true;
                            }
                        }
                        collision = true;
                    }
                    //HITTING COIN BRICKS
                    if (Player.inAir && Player.airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) || (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191))) {
                        for (MapObjects cb : coinBlocksList) {
                            if (cb.isActive()) {
                                if (Player.hitbox.intersects(cb.hitbox)) {
                                    cb.coinsInside--;
                                    if (cb.coinsInside > 0) {
                                        movedCoin = true;
                                        coins++;
                                        score+=200;
                                    }
                                    if (cb.coinsInside == 0) {
                                        coins++;
                                        cb.setActive(false);
                                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) {
                                            lvl[entityTopRow][entityRightCol] = 152;
                                        }
                                        if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191)) {
                                            lvl[entityTopRow][entityLeftCol] = 152;
                                        }
                                    }
                                }
                                if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) {
                                    movedCoinX = entityRightCol * 48;
                                    movedCoinY = entityTopRow * 48;
                                    CoinList.add(new Coin(entityRightCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE, COIN_BRICK));
                                }
                                if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191)) {
                                    movedCoinX = entityLeftCol * 48;
                                    movedCoinY = entityTopRow * 48;
                                    CoinList.add(new Coin(entityLeftCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE, COIN_BRICK));
                                }
                            }
                        }
                        collision = true;
                    }
                    //HITTING POWERUP BLOCK
                    if (Player.inAir && Player.airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(115) || levelManager.sprites.get(tileNum4) == levelManager.sprites.get(115))) {
                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(115)) {
                            lvl[entityTopRow][entityRightCol] = 152;
                            if (Player.playerStatus == SMALL) {
                                MushroomList.add(new Mushroom(entityRightCol * Game.TILES_SIZE + 3, (entityTopRow) * Game.TILES_SIZE - 5, MUSHROOM));
                            } else {
                                FireFlowerList.add(new FireFlower(entityRightCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE - 5, FIRE_FLOWER));
                            }
                        } else if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(115)) {
                            lvl[entityTopRow][entityLeftCol] = 152;
                            if (Player.playerStatus == SMALL) {
                                MushroomList.add(new Mushroom(entityLeftCol * Game.TILES_SIZE + 3, (entityTopRow) * Game.TILES_SIZE - 5, MUSHROOM));
                            } else {
                                FireFlowerList.add(new FireFlower(entityLeftCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE - 5, FIRE_FLOWER));
                            }
                        }
                        collision = true;
                    }

                    //WALKING COLLISIONS

                    //CHECKING IF NEXT BLOCK EXISTS
                    if (Player.x + 2 * Game.TILES_SIZE < lvlLenght * Game.TILES_SIZE) {
                        //COLLISION WHILE MOVING
                        if (levelManager.sprites.get(lvl[entityTopRow][entityRightCol + 1]) != levelManager.sprites.get(90)
                                || levelManager.sprites.get(lvl[entityBottomRow][entityRightCol + 1]) != levelManager.sprites.get(90)) {
                            distanceX = (entityRightCol + 1) * Game.TILES_SIZE - entityRightX;
                            if (distanceX <= 5) {
                                Player.rightPlayerSprint = 1;
                                Player.leftPlayerSprint = 1;
                                collision = true;
                            }
                        }
                        //COLLISION WHEN JUMPING PLAYER HITS BLOCK IN HALF
                        if (Player.inAir && levelManager.sprites.get(lvl[entityMiddleRow][entityRightCol + 1]) != levelManager.sprites.get(90)) {
                            distanceX = (entityRightCol + 1) * Game.TILES_SIZE - entityRightX;
                            if (distanceX <= 5) {
                                Player.rightPlayerSprint = 0;
                            }
                        }
                        //COLLISION WHILE SLIDING
                        if (Player.leftPlayerSprint > Player.minSprint) {
                            if (levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90)) {
                                Player.leftPlayerSprint = Player.minSprint;
                                collision = true;
                            }
                        }
                    } else if (Player.x + Game.TILES_SIZE >= lvlLenght * Game.TILES_SIZE) {
                        collision = true;
                        Player.leftPlayerSprint = 0;
                        Player.rightPlayerSprint = 0;
                    }

                    //PLAYER IN AIR COLLISIONS

                    //COLLISION WHEN FALLING
                    if (Player.inAir
                            && (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90))) {
                        distanceY = (entityBottomRow) * Game.TILES_SIZE - entityBottomY;
                        if (distanceY < 0) {
                            Player.y = entityTopRow * Game.TILES_SIZE + 8;
                            Player.jump = false;
                            Player.inAir = false;
                            Player.airSpeed = 0;
                            collision = true;
                        }
                    }
                    //COLLISION WHEN JUMPING
                    if (Player.inAir
                            && (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum4) != levelManager.sprites.get(90))) {
                        distanceY = (entityTopRow) * Game.TILES_SIZE - entityTopY;
                        if (distanceY < 0) {
                            Player.y = (entityTopRow + 1) * Game.TILES_SIZE;
                            Player.airSpeed = 0;
                            collision = true;
                        }
                    }
                    //FALLING
                    if (levelManager.sprites.get(lvl[entityBottomRow + 1][entityRightCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(lvl[entityBottomRow + 1][entityLeftCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(lvl[entityBottomRow][entityRightCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(lvl[entityBottomRow][entityLeftCol]) == levelManager.sprites.get(90)) {
                        Player.inAir = true;
                    }
                }

                case LEFT -> {
                    tileNum1 = lvl[entityTopRow][entityLeftCol];
                    tileNum2 = lvl[entityBottomRow][entityLeftCol];
                    tileNum3 = lvl[entityBottomRow][entityRightCol];
                    tileNum4 = lvl[entityTopRow][entityRightCol];

                    //HITTING INTERACTIVE BLOCKS

                    //HITTING BRICKS
                    if (Player.inAir && Player.airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(190)) || (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(190))) {
                        //WHEN PLAYER IS SMALL MARIO, BRICKS MOVE INSTEAD OF BREAKING
                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(190)) {
                            if (Player.playerStatus == SMALL) {
                                moved = true;
                                movedX = entityLeftCol * 48;
                                movedY = entityTopRow * 48;
                            } else {
                                lvl[entityTopRow][entityLeftCol] = 90;
                                broken = true;
                            }
                        }
                        if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(190)) {
                            if (Player.playerStatus == SMALL) {
                                moved = true;
                                movedX = entityRightCol * 48;
                                movedY = entityTopRow * 48;
                            } else {
                                lvl[entityTopRow][entityRightCol] = 90;
                                broken = true;
                            }
                        }
                        collision = true;
                    }
                    //HITTING COIN BRICKS
                    if (Player.inAir && Player.airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) || (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191))) {
                        for (MapObjects cb : coinBlocksList) {
                            if (cb.isActive()) {
                                if (Player.hitbox.intersects(cb.hitbox)) {
                                    cb.coinsInside--;
                                    if (cb.coinsInside > 0) {
                                        movedCoin = true;
                                        coins++;
                                        score+=200;
                                    }
                                    if (cb.coinsInside == 0) {
                                        coins++;
                                        cb.setActive(false);
                                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) {
                                            lvl[entityTopRow][entityLeftCol] = 152;
                                        }
                                        if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191)) {
                                            lvl[entityTopRow][entityRightCol] = 152;
                                        }
                                    }
                                }
                                if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(191)) {
                                    movedCoinX = entityLeftCol * 48;
                                    movedCoinY = entityTopRow * 48;
                                    CoinList.add(new Coin(entityLeftCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE, COIN_BRICK));
                                }
                                if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(191)) {
                                    movedCoinX = entityRightCol * 48;
                                    movedCoinY = entityTopRow * 48;
                                    CoinList.add(new Coin(entityRightCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE, COIN_BRICK));
                                }
                            }
                        }
                        collision = true;
                    }
                    //HITTING POWERUP BLOCK
                    if (Player.inAir && Player.airSpeed < 0 &&
                            (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(115) || levelManager.sprites.get(tileNum4) == levelManager.sprites.get(115))) {
                        if (levelManager.sprites.get(tileNum1) == levelManager.sprites.get(115)) {
                            lvl[entityTopRow][entityLeftCol] = 152;
                            if (Player.playerStatus == SMALL) {
                                MushroomList.add(new Mushroom(entityLeftCol * Game.TILES_SIZE + 3, (entityTopRow) * Game.TILES_SIZE - 5, MUSHROOM));
                            } else {
                                FireFlowerList.add(new FireFlower(entityLeftCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE - 5, FIRE_FLOWER));
                            }
                        } else if (levelManager.sprites.get(tileNum4) == levelManager.sprites.get(115)) {
                            lvl[entityTopRow][entityRightCol] = 152;
                            if (Player.playerStatus == SMALL) {
                                MushroomList.add(new Mushroom(entityRightCol * Game.TILES_SIZE + 3, (entityTopRow) * Game.TILES_SIZE - 5, MUSHROOM));
                            } else {
                                FireFlowerList.add(new FireFlower(entityRightCol * Game.TILES_SIZE, (entityTopRow) * Game.TILES_SIZE - 5, FIRE_FLOWER));
                            }
                        }
                        collision = true;
                    }

                    //WALKING COLLISIONS

                    //CHECKING IF NEXT BLOCK EXISTS
                    if (Player.x - Game.TILES_SIZE > 0) {
                        //COLLISION WHILE MOVING
                        if (levelManager.sprites.get(lvl[entityTopRow][entityLeftCol - 1]) != levelManager.sprites.get(90)
                                || levelManager.sprites.get(lvl[entityBottomRow][entityLeftCol - 1]) != levelManager.sprites.get(90)) {
                            distanceX = entityLeftX - (entityLeftCol) * Game.TILES_SIZE;
                            if (distanceX <= 5) {
                                Player.rightPlayerSprint = 1;
                                Player.leftPlayerSprint = 1;
                                collision = true;
                            }
                        }
                        //COLLISION WHEN JUMPING PLAYER HITS BLOCK IN HALF
                        if (Player.inAir && levelManager.sprites.get(lvl[entityMiddleRow][entityLeftCol - 1]) != levelManager.sprites.get(90)) {
                            distanceX = entityLeftX - (entityLeftCol) * Game.TILES_SIZE;
                            if (distanceX <= 5) {
                                Player.leftPlayerSprint = 0;
                            }
                        }
                        //COLLISION WHILE SLIDING
                        if (Player.rightPlayerSprint > Player.minSprint) {
                            if (levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90)) {
                                Player.rightPlayerSprint = Player.minSprint;
                                collision = true;
                            }
                        }
                    } else if (Player.x - Game.TILES_SIZE / 6.0f < 0) {
                        Player.rightPlayerSprint = 0;
                        Player.leftPlayerSprint = 0;
                        collision = true;
                    }

                    //PLAYER IN AIR COLLISIONS

                    //COLLISION WHEN FALLING
                    if (Player.inAir
                            && (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90))) {
                        distanceY = (entityBottomRow) * Game.TILES_SIZE - entityBottomY;
                        if (distanceY < 0) {
                            Player.y = entityTopRow * Game.TILES_SIZE + 8;
                            Player.jump = false;
                            Player.inAir = false;
                            Player.airSpeed = 0;
                        }
                    }
                    //COLLISION WHEN JUMPING
                    if (Player.inAir
                            && (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum4) != levelManager.sprites.get(90))) {
                        distanceY = (entityTopRow) * Game.TILES_SIZE - entityTopY;
                        if (distanceY < 0) {
                            Player.y = (entityTopRow + 1) * Game.TILES_SIZE;
                            Player.airSpeed = 0;
                        }
                    }
                    //FALLING
                    if (levelManager.sprites.get(lvl[entityBottomRow + 1][entityRightCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(lvl[entityBottomRow + 1][entityLeftCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(lvl[entityBottomRow][entityRightCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(lvl[entityBottomRow][entityLeftCol]) == levelManager.sprites.get(90)) {
                        Player.inAir = true;
                    }
                }
            }
        }
    }
}