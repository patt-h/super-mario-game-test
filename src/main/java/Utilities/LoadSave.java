package Utilities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class LoadSave {
    public static final String ENTITY_ATLAS = "sheet.png";
    public static final String ENEMY_ATLAS = "enemy.png";
    public static final String LEVEL_ATLAS = "tiles.png";
    public static final String POWERUPS_ATLAS = "powerups.png";
    public static final String BLUE_SKY_BACKGROUND = "background1.png";
    public static final String UNDERGROUD_BACKGROUND = "underground.png";
    public static final String EFFECTS_IMG = "effects.png";

    public static BufferedImage GetSpriteAtlas(String fileName) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" +fileName);
        try {
            img = ImageIO.read(is);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }
}
