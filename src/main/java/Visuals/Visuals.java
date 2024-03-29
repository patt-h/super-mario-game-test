package Visuals;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static Utilities.Constants.VisualsConstants.*;

public abstract class Visuals {
    public float x, y;
    public boolean active = true;
    public int visualType;

    protected int aniTick;
    protected int aniIndex;
    protected int aniSpeed = 24;

    public BufferedImage[] frames;

    public Visuals(float x, float y) {
        this.x = x;
        this.y = y;
    }

    protected void loadFrames(int framesNumber, int frameLength, String filename) {
        try {
            frames = new BufferedImage[framesNumber];
            URL imageUrl = getClass().getResource(filename);
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
        aniTick++;
        if (visualType == CLOUD) {
            aniSpeed = 48;
        }

        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= getVisualSpriteAmount(visualType)) {
                aniIndex = 0;
            }
        }
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
