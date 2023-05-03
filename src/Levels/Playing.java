package Levels;

import Entities.Entity;
import Entities.Player;
import Utilities.Constants.Directions;
import Utilities.LoadSave;
import com.company.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Playing  {
    static private int[][] lvl;
    static private LevelManager levelManager;
    public static boolean collision;
    private BufferedImage backgroundImg;

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

        int tileNum1, tileNum2, tileNum3;

        switch (Entity.direction) {
            case Directions.RIGHT -> {
                tileNum1 = lvl[entityTopRow][entityRightCol];
                tileNum2 = lvl[entityBottomRow][entityRightCol];
                tileNum3 = lvl[entityBottomRow][entityLeftCol];

                //RIGHT SIDE OF MAP
                if (Entity.x + Entity.hitbox.width >= 958) {
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
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
                    if (Player.inAir && Player.airSpeed > 0) {
                        Player.airSpeed = 0;
                        Player.y = entityBottomRow*48 - 88;
                        Player.inAir = false;
                    }
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

                //LEFT SIDE OF MAP
                if (Entity.x < 0) {
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
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
                    if (Player.inAir && Player.airSpeed > 0) {
                        Player.airSpeed = 0;
                        Player.y = entityBottomRow*48 - 88;
                        Player.inAir = false;
                    }
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
