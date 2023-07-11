package Entities;

import Levels.LevelManager;
import Levels.Playing;
import com.company.Game;

import java.awt.geom.Rectangle2D;

import static Utilities.Constants.Directions.LEFT;
import static Utilities.Constants.Directions.RIGHT;
import static Utilities.Constants.EnemyConstants.*;

public abstract class Enemy {
    public float x, y;
    public int enemyType;
    public Rectangle2D.Float hitbox;
    protected Rectangle2D.Float damageHitbox;
    protected int direction = LEFT;
    protected boolean active = true;
    protected int aniTick;
    public boolean inAir;

    public LevelManager levelManager;
    private boolean collision;
    private float distanceX;
    private float distanceY;

    public float airSpeed = 0.0f;
    public static float gravity = 0.04f * Game.SCALE;

    protected int aniIndex;
    protected int aniSpeed = 24;
    public int accurateAnimationRow;

    public Enemy(float x, float y) {
        levelManager = new LevelManager();
        this.x = x;
        this.y = y;
    }

    protected void updateAnimationTick() {
        if (enemyType == GOOMBA) {
            accurateAnimationRow = 6;
        }

        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= getEnemySprite(enemyType)) {
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

                if (x + 2*Game.TILES_SIZE < Playing.lvlLenght * Game.TILES_SIZE) {
                    //COLLISION WHILE MOVING
                    if (levelManager.sprites.get(Playing.lvl[objectBottomRow][objectRightCol + 1]) != levelManager.sprites.get(90)) {
                        distanceX = (objectRightCol + 1) * Game.TILES_SIZE - objectRightX;
                        if (distanceX <= 5) {
                            direction = LEFT;
                            collision = true;
                        }
                    }

                    //COLLISION WHILE FALLING
                    if (inAir
                            && (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90))) {
                        distanceY = (objectBottomRow) * Game.TILES_SIZE - objectBottomY;
                        if (distanceY < 0) {
                            y = objectTopRow * Game.TILES_SIZE - 1;
                            inAir = false;
                            airSpeed = 0;
                            collision = true;
                        }
                    }
                    //FALLING
                    if (levelManager.sprites.get(Playing.lvl[objectBottomRow + 1][objectRightCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(Playing.lvl[objectBottomRow + 1][objectLeftCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectRightCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectLeftCol]) == levelManager.sprites.get(90)) {
                        inAir = true;
                    }
                }
                else {
                    if (x + 1.25*Game.TILES_SIZE >= Playing.lvlLenght * Game.TILES_SIZE) {
                        collision = true;
                        direction = LEFT;
                    }
                }
            }
            case LEFT -> {
                tileNum2 = Playing.lvl[objectBottomRow][objectLeftCol];
                tileNum3 = Playing.lvl[objectBottomRow][objectRightCol];

                if (x >= Game.TILES_SIZE) {
                    //COLLISION WHILE MOVING
                    if (levelManager.sprites.get(Playing.lvl[objectBottomRow][objectLeftCol - 1]) != levelManager.sprites.get(90)) {
                        distanceX = objectLeftX - (objectLeftCol - 1) * Game.TILES_SIZE;
                        if (distanceX <= Game.TILES_SIZE + 5) {
                            direction = RIGHT;
                            collision = true;
                        }
                    }
                    //COLLISION WHILE FALLING
                    if (inAir
                            && (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90) || levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90))) {
                        distanceY = (objectBottomRow) * Game.TILES_SIZE - objectBottomY;
                        if (distanceY < 0) {
                            y = objectTopRow * Game.TILES_SIZE - 1;
                            inAir = false;
                            airSpeed = 0;
                            collision = true;
                        }
                    }
                    //FALLING
                    if (levelManager.sprites.get(Playing.lvl[objectBottomRow + 1][objectRightCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(Playing.lvl[objectBottomRow + 1][objectLeftCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectRightCol]) == levelManager.sprites.get(90)
                            && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectLeftCol]) == levelManager.sprites.get(90)) {
                        inAir = true;
                    }
                }
                else {
                    if (x <= 0) {
                        collision = true;
                        direction = RIGHT;
                    }
                }
            }
        }
    }

    public int getAniIndex() {
        return aniIndex;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
