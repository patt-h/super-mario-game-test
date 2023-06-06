package Objects;

import Levels.LevelManager;
import Levels.Playing;
import com.company.Game;


import java.awt.geom.Rectangle2D;

import static Utilities.Constants.Directions.*;
import static Utilities.Constants.ObjectConstants.*;

public class GameObject {

    public float x, y;
    protected int objType;
    protected Rectangle2D.Float hitbox;
    protected boolean doAnimation, active = true;
    protected int aniTick, aniIndex, aniSpeed = 8;
    protected int direction = RIGHT;
    public boolean inAir;

    private LevelManager levelManager;
    private boolean collision;
    private float distanceX;
    private float distanceY;

    public float airSpeed = 0.0f;
    public static float gravity = 0.04f * Game.SCALE;

    public GameObject(int x, int y, int objType) {
        levelManager = new LevelManager();
        this.x = x;
        this.y = y;
        this.objType = objType;
    }

    protected void updateAnimationTick() {
        if (objType == FIRE_BALL) {
            aniSpeed = 12;
        }

        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= getSpriteAmount(objType)) {
                aniIndex = 0;
            }
        }
    }


    public void checkCollisions() {
        collision = false;

        int objectLeftX = (int)x;
        int objectRightX = (int)x + (int)hitbox.width;
        int objectTopY = (int)y;
        int objectBottomY = (int)y + (int)hitbox.height;

        int objectLeftCol = objectLeftX / Game.TILES_SIZE;
        int objectRightCol = objectRightX / Game.TILES_SIZE;
        int objectTopRow = objectTopY / Game.TILES_SIZE;
        int objectBottomRow = objectBottomY / Game.TILES_SIZE;

        int tileNum2, tileNum3;

        switch (direction) {
            case RIGHT -> {
                tileNum2 = Playing.lvl[objectBottomRow][objectRightCol];
                tileNum3 = Playing.lvl[objectBottomRow][objectLeftCol];

                //COLLISION WHILE MOVING
                if (levelManager.sprites.get(Playing.lvl[objectTopRow][objectRightCol+1]) != levelManager.sprites.get(90)
                        || levelManager.sprites.get(Playing.lvl[objectBottomRow][objectRightCol+1]) != levelManager.sprites.get(90)) {
                    distanceX = (objectRightCol+1)*Game.TILES_SIZE - objectRightX;
                    if (distanceX <= 1) {
                        direction = LEFT;
                        collision = true;
                    }
                }

                //COLLISION WHILE FALLING
                if (inAir
                        && (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90))) {
                    distanceY = (objectBottomRow)*Game.TILES_SIZE - objectBottomY;
                    if (distanceY < 0) {
                        y = objectTopRow * Game.TILES_SIZE - 1;
                        inAir = false;
                        airSpeed = 0;
                        collision = true;
                    }
                }
                //FALLING
                if (levelManager.sprites.get(Playing.lvl[objectBottomRow+1][objectRightCol]) == levelManager.sprites.get(90)
                        && levelManager.sprites.get(Playing.lvl[objectBottomRow+1][objectLeftCol]) == levelManager.sprites.get(90)
                        && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectRightCol]) == levelManager.sprites.get(90)
                        && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectLeftCol]) == levelManager.sprites.get(90)) {
                    inAir = true;
                }
            }
            case LEFT -> {
                tileNum2 = Playing.lvl[objectBottomRow][objectLeftCol];
                tileNum3 = Playing.lvl[objectBottomRow][objectRightCol];

                //COLLISION WHILE MOVING
                if (levelManager.sprites.get(Playing.lvl[objectTopRow][objectLeftCol-1]) != levelManager.sprites.get(90)
                        || levelManager.sprites.get(Playing.lvl[objectBottomRow][objectLeftCol-1]) != levelManager.sprites.get(90)) {
                    distanceX = objectLeftX - (objectLeftCol-1)*Game.TILES_SIZE;
                    if (distanceX <= Game.TILES_SIZE) {
                        direction = RIGHT;
                        collision = true;
                    }
                }
                //COLLISION WHILE FALLING
                if (inAir
                        && (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90))) {
                    distanceY = (objectBottomRow)*Game.TILES_SIZE - objectBottomY;
                    if (distanceY < 0) {
                        y = objectTopRow * Game.TILES_SIZE - 1;
                        inAir = false;
                        airSpeed = 0;
                        collision = true;
                    }
                }
                //FALLING
                if (levelManager.sprites.get(Playing.lvl[objectBottomRow+1][objectRightCol]) == levelManager.sprites.get(90)
                        && levelManager.sprites.get(Playing.lvl[objectBottomRow+1][objectLeftCol]) == levelManager.sprites.get(90)
                        && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectRightCol]) == levelManager.sprites.get(90)
                        && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectLeftCol]) == levelManager.sprites.get(90)) {
                    inAir = true;
                }
            }
        }
    }

    public int getObjType() {
        return objType;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getAniIndex() {
        return aniIndex;
    }
}
