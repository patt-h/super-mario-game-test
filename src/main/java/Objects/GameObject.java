package Objects;

import Levels.LevelManager;
import States.Playing;
import com.company.Game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import static Utilities.Constants.Directions.*;
import static Utilities.Constants.ObjectConstants.*;

public abstract class GameObject {
    public float x, y;
    public int width, height;
    protected int objType;
    public Rectangle2D.Float hitbox;
    protected boolean doAnimation, active = true;
    protected int aniTick, aniIndex, aniSpeed = 8;
    protected int direction = RIGHT;
    public boolean inAir;
    public boolean collected;
    private int collectedCounter;

    public LevelManager levelManager;
    private boolean collision;
    private float distanceX;
    private float distanceY;

    public float airSpeed = 0.0f;
    public static float gravity = 0.04f * Game.SCALE;

    public BufferedImage[] frames;

    public GameObject(int x, int y, int objType) {
        levelManager = new LevelManager();
        this.x = x;
        this.y = y;
        this.objType = objType;
    }

    protected void loadFrames(int framesNumber, int frameLength, String filename) {
        try {
            frames = new BufferedImage[framesNumber];
            URL imageUrl = getClass().getResource("/visuals/"+filename);
            BufferedImage spriteSheet = ImageIO.read(imageUrl);
            int frameHeight = spriteSheet.getHeight();

            for (int i = 0; i < frames.length; i++) {
                frames[i] = spriteSheet.getSubimage(i * frameLength, 0, frameLength, frameHeight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void updateAnimationTick() {
        if (objType == FIRE_BALL || objType == COIN_BRICK) {
            aniSpeed = 12;
        }
        if (objType == COIN) {
            aniSpeed = 18;
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

    public void drawScoreAdded(float x, float y, int score, Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3.5f));

        GlyphVector glyphVectorScore = Game.smallerFont.createGlyphVector(g2.getFontRenderContext(), String.valueOf(score));
        Shape scoreShape = glyphVectorScore.getOutline();

        g2.setColor(new Color(15, 30, 60));
        g2.translate(x + 1, y);
        g2.draw(scoreShape);
        g2.setColor(Color.WHITE);
        g2.fill(scoreShape);
        g2.translate(-(x + 1), -y);

        if (collected) {
            collectedCounter++;
            if (collectedCounter >= 60) {
                collected = false;
                collectedCounter = 0;
            }
        }
    }

    public void drawString(float x, float y, String text, Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3.5f));

        GlyphVector glyphVectorString = Game.smallerFont.createGlyphVector(g2.getFontRenderContext(), text);
        Shape stringShape = glyphVectorString.getOutline();

        g2.setColor(new Color(15, 30, 60));
        g2.translate(x + 1, y);
        g2.draw(stringShape);
        g2.setColor(Color.WHITE);
        g2.fill(stringShape);
        g2.translate(-(x + 1), -y);

        if (collected) {
            collectedCounter++;
            if (collectedCounter >= 60) {
                collected = false;
                collectedCounter = 0;
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

    public boolean isCollected() {
        return collected;
    }

    public void setCollected (boolean collected) {
        this.collected = collected;
    }

    public int getAniIndex() {
        return aniIndex;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
