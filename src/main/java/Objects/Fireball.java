package Objects;

import Input.KeyInputs;
import Levels.Playing;
import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static Levels.Playing.lvlLength;
import static Utilities.Constants.Directions.LEFT;
import static Utilities.Constants.Directions.RIGHT;

public class Fireball extends GameObject {
    public static ArrayList<Fireball> FireballList = new ArrayList<>();
    public static float jumpSpeed = -0.85f * Game.SCALE;
    public int value = 4;

    public boolean collision;
    public boolean collisionX;
    private float distanceX;
    private float distanceY;

    public Fireball(int x, int y, int objType, int direction) {
        super(x, y, objType);
        width = 8 * Game.SCALE;
        height = 8 * Game.SCALE;
        this.direction = direction;
        doAnimation = true;
        inAir = true;
        hitbox = new Rectangle2D.Float();
        hitbox.width = 8 * Game.SCALE;
        hitbox.height = 8 * Game.SCALE;
    }

    private void updatePosition() {
        hitbox.x = x;
        hitbox.y = y;

        if (direction == RIGHT) {
            x += value;
        }
        else if (direction == LEFT) {
            x -= value;
        }

        if (inAir) {
            y += airSpeed;
            airSpeed += gravity;
        }
        if (!inAir) {
            inAir = true;
            airSpeed = jumpSpeed;
        }
    }

    public static ArrayList<Fireball> getFireballs() {
        return FireballList;
    }

    public void update() {
        updatePosition();
        if (y > 0 && y < Game.GAME_HEIGHT - Game.TILES_SIZE) {
            checkCollisions();
        }
        if (x > lvlLength * Game.TILES_SIZE - Game.TILES_SIZE || x < 0) {
            active = false;
            KeyInputs.activeBalls--;
        }
        if (y > Game.GAME_HEIGHT) {
            setActive(false);
            KeyInputs.activeBalls--;
        }
        updateAnimationTick();
    }

    @Override
    public void checkCollisions() {
        for (Fireball fb: FireballList) {
            if (fb.isActive()) {
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

                        if (objectRightX + Game.TILES_SIZE < lvlLength * Game.TILES_SIZE) {
                            //COLLISION WHILE MOVING
                            if (levelManager.sprites.get(Playing.lvl[objectTopRow][objectRightCol + 1]) != levelManager.sprites.get(90)
                                || levelManager.sprites.get(Playing.lvl[objectBottomRow][objectRightCol + 1]) != levelManager.sprites.get(90)
                                && distanceY != 0) {
                                distanceX = (objectRightCol + 1) * Game.TILES_SIZE - objectRightX;

                                if (distanceX <= 16 && distanceX > 8) {
                                    fb.aniIndex = 0;
                                }
                                if (distanceX <= 8) {
                                    collisionX = true;
                                }
                            }
                        }
                        //COLLISION WHILE FALLING
                        if (inAir
                                && (levelManager.sprites.get(tileNum2) != levelManager.sprites.get(90) && levelManager.sprites.get(tileNum3) != levelManager.sprites.get(90))) {
                            distanceY = (objectBottomRow)*Game.TILES_SIZE - objectBottomY;
                            if (distanceY < 0) {
                                y = objectTopRow * Game.TILES_SIZE - 1;
                                inAir = false;
                                airSpeed = 0;
                                collision = true;
                            }
                        }
                        //FALLING
                        if (y < Game.GAME_HEIGHT - 3 * Game.TILES_SIZE) {
                            if (levelManager.sprites.get(Playing.lvl[objectBottomRow + 1][objectRightCol]) == levelManager.sprites.get(90)
                                    && levelManager.sprites.get(Playing.lvl[objectBottomRow + 1][objectLeftCol]) == levelManager.sprites.get(90)
                                    && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectRightCol]) == levelManager.sprites.get(90)
                                    && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectLeftCol]) == levelManager.sprites.get(90)) {
                                inAir = true;
                            }
                        }
                        else {
                            if (levelManager.sprites.get(Playing.lvl[objectBottomRow][objectRightCol]) == levelManager.sprites.get(90)
                                    && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectLeftCol]) == levelManager.sprites.get(90)) {
                                inAir = true;
                            }
                        }
                    }
                    case LEFT -> {
                        tileNum2 = Playing.lvl[objectBottomRow][objectLeftCol];
                        tileNum3 = Playing.lvl[objectBottomRow][objectRightCol];

                        if (objectLeftX - 2*fb.hitbox.width > 0) {
                            //COLLISION WHILE MOVING
                            if (levelManager.sprites.get(Playing.lvl[objectTopRow][objectLeftCol - 1]) != levelManager.sprites.get(90)
                                || levelManager.sprites.get(Playing.lvl[objectBottomRow][objectLeftCol - 1]) != levelManager.sprites.get(90)
                                && distanceY != 0) {
                                distanceX = objectLeftX - (objectLeftCol - 1) * Game.TILES_SIZE;
                                if (distanceX <= Game.TILES_SIZE + 16 && distanceX > Game.TILES_SIZE + 8) {
                                    fb.aniIndex = 0;
                                }
                                if (distanceX <= Game.TILES_SIZE + 8) {
                                    collisionX = true;
                                }
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
                        if (y < Game.GAME_HEIGHT - 3 * Game.TILES_SIZE) {
                            if (levelManager.sprites.get(Playing.lvl[objectBottomRow + 1][objectRightCol]) == levelManager.sprites.get(90)
                                    && levelManager.sprites.get(Playing.lvl[objectBottomRow + 1][objectLeftCol]) == levelManager.sprites.get(90)
                                    && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectRightCol]) == levelManager.sprites.get(90)
                                    && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectLeftCol]) == levelManager.sprites.get(90)) {
                                inAir = true;
                            }
                        }
                        else {
                            if (levelManager.sprites.get(Playing.lvl[objectBottomRow][objectRightCol]) == levelManager.sprites.get(90)
                                    && levelManager.sprites.get(Playing.lvl[objectBottomRow][objectLeftCol]) == levelManager.sprites.get(90)) {
                                inAir = true;
                            }
                        }
                    }
                }
            }
        }
    }
}
