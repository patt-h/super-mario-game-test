package Entities;

import Utilities.LoadSave;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class Entity {
    public float x, y;
    public int direction;
    public static Rectangle2D.Float hitbox;
    public BufferedImage[][] animations;

    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
        loadAnimation();
    }

    private void loadAnimation() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.ENTITY_ATLAS);

        animations = new BufferedImage[11][10];
        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                animations[j][i] = img.getSubimage(i * 40, j * 40, 40, 40);
                //BIG DUCKING
                if (animations[j][i] == animations[3][3]) {
                    animations[j][i] = img.getSubimage((i * 40) - 1, j * 40, 40, 40);
                }
                //BIG TURNING
                if (animations[j][i] == animations[2][4]) {
                    animations[j][i] = img.getSubimage(i * 40, (j * 40) - 1, 40, 40);
                }
                //FIRE DUCKING
                if (animations[j][i] == animations[5][3]) {
                    animations[j][i] = img.getSubimage((i * 40) - 1, j * 40, 40, 40);
                }
                //FIRE TURNING
                if (animations[j][i] == animations[4][4]) {
                    animations[j][i] = img.getSubimage(i * 40, (j * 40) - 1, 40, 40);
                }
            }
        }
    }
}
