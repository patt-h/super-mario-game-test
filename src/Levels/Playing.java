package Levels;

import Entities.Entity;
import Entities.Player;
import Utilities.Constants.Directions;
import com.company.Game;

import java.awt.*;

public class Playing  {
    static private int[][] lvl;
    static private LevelManager levelManager;
    public static boolean collision;

    public Playing() {
        lvl = LevelBuilder.getLevelData();
        levelManager = new LevelManager();
    }

    public void render(Graphics g) {

        for (int i = 0; i < lvl.length; i++) {
            for (int j = 0; j < lvl[i].length; j++) {
                int id = lvl[i][j];
                g.drawImage(levelManager.sprites.get(id),j*48,i*48,48,48,null);
            }
        }
    }

    public static void checkCollisions() {
        collision = false;

        int entityLeftX = (int) Entity.x;
        int entityRightX = (int)Entity.x + (int)Entity.hitbox.width;
        int entityTopY = (int)Entity.y;
        int entityBottomY = (int)Entity.y + (int)Entity.hitbox.height;

        int entityLeftCol = entityLeftX / Game.TILES_SIZE;
        int entityRightCol = entityRightX / Game.TILES_SIZE;
        int entityTopRow = entityTopY / Game.TILES_SIZE;
        int entityBottomRow = entityBottomY / Game.TILES_SIZE;

        int tileNum1, tileNum2, tileNum3, tileNum4;


        switch (Entity.direction) {
            case Directions.RIGHT -> {
                tileNum1 = lvl[entityTopRow][entityRightCol];
                tileNum2 = lvl[entityBottomRow][entityRightCol];

                if (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90)) {
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
                    collision = true;
                }

            }
            case Directions.LEFT -> {
                tileNum1 = lvl[entityTopRow][entityLeftCol];
                tileNum2 = lvl[entityBottomRow][entityLeftCol];

                if (Entity.x < 0) {
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
                    collision = true;
                }

                if (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90)) {
                    Player.rightPlayerSprint = 1;
                    Player.leftPlayerSprint = 1;
                    collision = true;
                }
            }
            case Directions.UP -> {

                tileNum1 = lvl[entityBottomRow][entityRightCol];
                tileNum2 = lvl[entityBottomRow][entityLeftCol];
                tileNum3 = lvl[entityTopRow][entityRightCol];
                tileNum4 = lvl[entityTopRow][entityLeftCol];



                if (levelManager.sprites.get(tileNum1) != levelManager.sprites.get(90)
                        || levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90)) {
                    collision = true;

                }

                if (levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90)
                        || levelManager.sprites.get(tileNum4) != levelManager.sprites.get(90)) {
                }
            }
        }

    }
}
