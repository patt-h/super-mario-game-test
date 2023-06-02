package Objects;

import Entities.Player;
import Utilities.LoadSave;
import com.company.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Utilities.Constants.PlayerConstants.BIG;
import static Utilities.Constants.PlayerConstants.SMALL;

public class ObjectManager {
    private BufferedImage[][] animations;
    public ArrayList<Mushroom> mushrooms = new ArrayList<>();

    public ObjectManager() {
        loadImg();
        addMushrooms();
    }

    private void addMushrooms() {
        mushrooms = Mushroom.getMushrooms();
    }

    public void checkTouched() {
        for (Mushroom m : mushrooms) {
            if (m.isActive()) {
                m.update();
                if (Player.hitbox.intersects(m.hitbox.x, m.hitbox.y, (int) m.hitbox.width, (int) m.hitbox.height)) {
                    if (Player.playerStatus == SMALL) {
                        Player.y -= Game.TILES_SIZE;
                    }
                    Player.playerStatus = BIG;
                    m.setActive(false);
                }
            }
        }
    }

    private void loadImg() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.POWERUPS_ATLAS);

        animations = new BufferedImage[5][11];
        for (int y = 0; y < animations.length; y++) {
            for (int x = 0; x < animations[y].length; x++) {
                animations[y][x] = img.getSubimage((x*16)+x+1,(y*16)+y+1,16,16);
            }
        }
    }

    public void update() {
        checkTouched();
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawMushrooms(g, xLvlOffset);
    }

    public void drawMushrooms(Graphics g, int xLvlOffset) {
        for (Mushroom m : mushrooms) {
            if (m.isActive()) {
                g.drawImage(animations[0][m.getAniIndex()], (int)m.x - xLvlOffset, (int)m.y+2, (int)m.hitbox.width, (int)m.hitbox.height, null);
                //g.drawRect((int)m.hitbox.x - xLvlOffset, (int)m.hitbox.y,(int)m.hitbox.width, (int)m.hitbox.height);
            }
        }
    }
}
