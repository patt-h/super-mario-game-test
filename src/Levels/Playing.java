package Levels;

import Entities.Entity;
import Entities.Player;
import Utilities.Constants.Directions;
import static Utilities.Constants.PlayerConstants.SMALL;
import Utilities.LoadSave;
import com.company.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Playing {
    static private int[][] lvl;
    static private LevelManager levelManager;
    public static boolean collision;
    private BufferedImage backgroundImg;
    static private  boolean moved = false;
    static private int movedX, movedY;
    private float counter;

    public Playing() {
        lvl = LevelBuilder.getLevelData();
        levelManager = new LevelManager();
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.BACKGROUND_IMG);
    }

    public void render(Graphics g) {
        g.drawImage(backgroundImg,0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT, null);
        for (int i = 0; i < lvl.length; i++) {
            for (int j = 0; j < lvl[i].length; j++) {
                int id = lvl[i][j];
                g.drawImage(levelManager.sprites.get(id),j*48,i*48,48,48,null);
            }
        }
        if (moved) {
            g.drawImage(levelManager.sprites.get(190),movedX,movedY-5,48,48,null);
            lvl[movedY/Game.TILES_SIZE][movedX/Game.TILES_SIZE] = 90;
            counter += 1;
            if (counter >= 20) {
                g.drawImage(levelManager.sprites.get(190),movedX,movedY+5,48,48,null);
                lvl[movedY/Game.TILES_SIZE][movedX/Game.TILES_SIZE] = 190;
                counter = 0;
                moved = false;
            }
        }
    }

    public static void checkCollisions() {
        collision = false;

        int entityLeftX = (int)Entity.x;
        int entityRightX = (int)Entity.x + (int)Entity.hitbox.width;
        int entityTopY = (int)Entity.y;
        int entityBottomY = (int)Entity.y + (int)Entity.hitbox.height -3;

        int entityLeftCol = entityLeftX / Game.TILES_SIZE;
        int entityRightCol = entityRightX / Game.TILES_SIZE;
        int entityTopRow = entityTopY / Game.TILES_SIZE;
        int entityBottomRow = entityBottomY / Game.TILES_SIZE;

        int tileNum1, tileNum2, tileNum3, tileNum4;

        switch (Entity.direction) {
            case Directions.RIGHT -> {
                tileNum1 = lvl[entityTopRow][entityRightCol];
                tileNum2 = lvl[entityBottomRow][entityRightCol];
                tileNum3 = lvl[entityBottomRow][entityLeftCol];
                tileNum4 = lvl[entityTopRow][entityLeftCol];

                //HITTING BRICKS
                if (Player.inAir && Player.airSpeed < 0 && levelManager.sprites.get(tileNum1) == levelManager.sprites.get(190)) {
                    //WHEN PLAYER IS SMALL MARIO, BRICKS MOVE INSTEAD OF BREAKING
                    if (Player.playerStatus == SMALL) {
                        moved = true;
                        movedX = entityRightCol * 48;
                        movedY = entityTopRow * 48;
                        System.out.println(Player.playerStatus);
                    } else {
                        lvl[entityTopRow][entityRightCol] = 90;
                    }
                    collision = true;
                }
                if (Player.inAir && Player.airSpeed < 0 && levelManager.sprites.get(tileNum4) == levelManager.sprites.get(190)) {
                    //WHEN PLAYER IS SMALL MARIO, BRICKS MOVE INSTEAD OF BREAKING
                    if (Player.playerStatus == SMALL) {
                        moved = true;
                        movedX = entityLeftCol * 48;
                        movedY = entityTopRow * 48;
                    } else {
                        lvl[entityTopRow][entityLeftCol] = 90;
                    }
                    collision = true;
                }
                //HITTING PRIZE BLOCK
                if (Player.inAir && Player.airSpeed < 0 && levelManager.sprites.get(tileNum1) == levelManager.sprites.get(115)) {
                    lvl[entityTopRow][entityRightCol] = 152;
                    collision = true;
                }
                if (Player.inAir && Player.airSpeed < 0 && levelManager.sprites.get(tileNum4) == levelManager.sprites.get(115)) {
                    lvl[entityTopRow][entityLeftCol] = 152;
                    collision = true;
                }

                //RIGHT SIDE OF MAP
                if (Entity.x + Entity.hitbox.width >= 958) {
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
                    collision = true;
                }
                //COLLISION WHILE SLIDING
                if (Player.leftPlayerSprint > Player.minSprint) {
                    if (levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90) || Player.x < 0) {
                        Player.leftPlayerSprint = Player.minSprint;
                        collision = true;
                    }
                }
                //COLLISION WHILE HITTING CEILING
                if (Player.inAir && (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum4) != levelManager.sprites.get(90))) {
                    //MOVING BACK WHEN STAYS NEXT TO STRUCTURE THAT IS HIGHER THAN PLAYER
                    if (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90)) {
                        Player.inAir = false;
                        Player.x -= 0.5f;
                    }
                    //FALLING DOWN WHEN HIT CEILING
                    Player.airSpeed = 0;
                    Player.y += 1;
                    collision = true;
                }
                //CHECKING IF OPPOSITE HITBOX SIDE HAS COLLISION
                if (Player.inAir && Player.airSpeed > 0 && levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90)) {
                    Player.airSpeed = 0;
                    Player.y = entityBottomRow*48 - 88;
                    Player.inAir = false;
                    collision = true;
                }
                //COLLISION WHILE WALKING AND JUMPING
                else if (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90)) {
                    //MOVING BACK WHEN JUMPING NEXT TO ONE BLOCK STRUCTURE, SO PLAYER WON'T LAND ON NEXT BLOCK IF DIDN'T MOVE
                    if (Player.inAir && levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90)) {
                        Player.x -= 0.5f;
                    }
                    //LANDING ON THE FLOOR
                    if (Player.inAir && Player.airSpeed > 0) {
                        Player.airSpeed = 0;
                        Player.y = entityBottomRow*48 - 88;
                        Player.inAir = false;
                    }
                    //RESETTING SPRINT VALUES WHEN SPRINTING NEXT TO WALL
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
                    collision = true;
                }
                //FALLING
                else if (levelManager.sprites.get(lvl[entityBottomRow+1][entityLeftCol]) == levelManager.sprites.get(90)
                        && levelManager.sprites.get(lvl[entityBottomRow+1][entityRightCol]) == levelManager.sprites.get(90)) {
                    Player.inAir = true;
                }
            }
            case Directions.LEFT -> {
                tileNum1 = lvl[entityTopRow][entityLeftCol];
                tileNum2 = lvl[entityBottomRow][entityLeftCol];
                tileNum3 = lvl[entityBottomRow][entityRightCol];
                tileNum4 = lvl[entityTopRow][entityRightCol];

                //HITTING BRICKS
                if (Player.inAir && Player.airSpeed < 0 && levelManager.sprites.get(tileNum1) == levelManager.sprites.get(190)) {
                    //WHEN PLAYER IS SMALL MARIO, BRICKS MOVE INSTEAD OF BREAKING
                    if (Player.playerStatus == SMALL) {
                        moved = true;
                        movedX = entityLeftCol * 48;
                        movedY = entityTopRow * 48;
                    } else {
                        lvl[entityTopRow][entityLeftCol] = 90;
                    }
                    collision = true;
                }
                if (Player.inAir && Player.airSpeed < 0 && levelManager.sprites.get(tileNum4) == levelManager.sprites.get(190)) {
                    //WHEN PLAYER IS SMALL MARIO, BRICKS MOVE INSTEAD OF BREAKING
                    if (Player.playerStatus == SMALL) {
                        moved = true;
                        movedX = entityRightCol * 48;
                        movedY = entityTopRow * 48;
                    } else {
                        lvl[entityTopRow][entityRightCol] = 90;
                    }
                    collision = true;
                }
                //HITTING PRIZE BLOCK
                if (Player.inAir && Player.airSpeed < 0 && levelManager.sprites.get(tileNum1) == levelManager.sprites.get(115)) {
                    lvl[entityTopRow][entityLeftCol] = 152;
                    collision = true;
                }
                if (Player.inAir && Player.airSpeed < 0 && levelManager.sprites.get(tileNum4) == levelManager.sprites.get(115)) {
                    lvl[entityTopRow][entityRightCol] = 152;
                    collision = true;
                }

                //LEFT SIDE OF MAP
                if (Entity.x < 0) {
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
                    collision = true;
                }
                //COLLISION WHILE SLIDING
                if (Player.rightPlayerSprint > Player.minSprint) {
                    if (levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90) ) {
                        Player.rightPlayerSprint = Player.minSprint;
                        collision = true;
                    }
                }
                //COLLISI0N WHILE HITTING CEILING
                if (Player.inAir && (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum4) != levelManager.sprites.get(90))) {
                    //MOVING BACK WHEN STAYS NEXT TO STRUCTURE THAT IS HIGHER THAN PLAYER
                    if (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90)) {
                        Player.inAir = false;
                        Player.x += 0.5f;
                    }
                    //FALLING DOWN WHEN HIT CEILING
                    Player.airSpeed = 0;
                    Player.y += 1;
                    collision = true;
                }
                //CHECKING IF OPPOSITE HITBOX SIDE HAS COLLISION
                if (Player.inAir && Player.airSpeed > 0 && levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90)) {
                    Player.airSpeed = 0;
                    Player.y = entityBottomRow*48 - 88;
                    Player.inAir = false;
                }
                //COLLISION WHILE WALKING AND JUMPING
                else if (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90)) {
                    //MOVING BACK WHEN JUMPING NEXT TO ONE BLOCK STRUCTURE, SO PLAYER WON'T LAND ON NEXT BLOCK IF DIDN'T MOVE
                    if (Player.inAir && levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90)) {
                        Player.x += 0.5f;
                    }
                    //LANDING ON THE FLOOR
                    if (Player.inAir && Player.airSpeed > 0) {
                        Player.airSpeed = 0;
                        Player.y = entityBottomRow*48 - 88;
                        Player.inAir = false;
                    }
                    //RESETTING SPRINT VALUES WHEN SPRINTING NEXT TO WALL
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
                    collision = true;
                }
                //FALLING
                else if (levelManager.sprites.get(lvl[entityBottomRow+1][entityLeftCol]) == levelManager.sprites.get(90)
                        && levelManager.sprites.get(lvl[entityBottomRow+1][entityRightCol]) == levelManager.sprites.get(90)) {
                    Player.inAir = true;
                }
            }
        }
    }
}
