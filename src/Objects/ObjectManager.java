package Objects;

import Entities.Player;
import Input.KeyInputs;
import Utilities.LoadSave;
import com.company.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Utilities.Constants.Directions.LEFT;
import static Utilities.Constants.PlayerConstants.*;

public class ObjectManager {
    private BufferedImage[][] animations;
    public ArrayList<Mushroom> mushrooms = new ArrayList<>();
    public ArrayList<FireFlower> fireFlowers = new ArrayList<>();
    public ArrayList<Fireball> fireballs = new ArrayList<>();

    public ObjectManager() {
        loadImg();
        addObjects();
    }

    private void addObjects() {
        mushrooms = Mushroom.getMushrooms();
        fireFlowers = FireFlower.getFireFlowers();
        fireballs = Fireball.getFireballs();
    }

    public void checkTouched() {
        for (Mushroom m : mushrooms) {
            if (m.isActive()) {
                m.update();
                if (Player.hitbox.intersects(m.hitbox.x, m.hitbox.y, (int) m.hitbox.width, (int) m.hitbox.height)) {
                    if (Player.playerStatus == SMALL) {
                        Player.y -= Game.TILES_SIZE;
                        Player.playerStatus = BIG;
                    }
                    m.setActive(false);
                }
            }
        }
        for (FireFlower f : fireFlowers) {
            if (f.isActive()) {
                f.update();
                if (Player.hitbox.intersects(f.hitbox.x, f.hitbox.y, (int) f.hitbox.width, (int) f.hitbox.height)) {
                    if (Player.playerStatus == SMALL) {
                        Player.y -= Game.TILES_SIZE;
                        Player.playerStatus = BIG;
                    }
                    else if (Player.playerStatus == BIG) {
                        Player.playerStatus = FIRE;
                    }
                    f.setActive(false);
                }
            }
        }
        for (Fireball fb : fireballs) {
            if (fb.isActive()) {
                fb.update();
                if (fb.collisionX) {
                    if (fb.aniIndex == 3) {
                        fb.aniIndex = 0;
                    }
                }
            }
        }
    }

    private void loadImg() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.POWERUPS_ATLAS);

        animations = new BufferedImage[11][5];
        for (int y = 0; y < animations.length; y++) {
            for (int x = 0; x < animations[y].length; x++) {
                animations[y][x] = img.getSubimage((x*16)+x+1,(y*16)+y+1,16,16);
                if (animations[y][x] == animations[9][x]) {
                    animations[y][x] = img.getSubimage((x*8)+x+1,135,8,8);
                }
                if (animations[y][x] == animations[10][x]) {
                    animations[y][x] = img.getSubimage((x*16)+x+1,144,16,16);
                }
            }
        }
    }

    public void update() {
        checkTouched();
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawMushrooms(g, xLvlOffset);
        drawFireFlowers(g, xLvlOffset);
        drawFireballs(g, xLvlOffset);
    }

    public void drawMushrooms(Graphics g, int xLvlOffset) {
        for (Mushroom m : mushrooms) {
            if (m.isActive()) {
                g.drawImage(animations[0][m.getAniIndex()], (int)m.x - xLvlOffset, (int)m.y+2, (int)m.hitbox.width, (int)m.hitbox.height, null);
            }
        }
    }

    public void drawFireFlowers(Graphics g, int xLvlOffset) {
        for (FireFlower f : fireFlowers) {
            if (f.isActive()) {
                g.drawImage(animations[1][f.getAniIndex()], (int)f.x - xLvlOffset, (int)f.y+2, (int)f.hitbox.width, (int)f.hitbox.height, null);
            }
        }
    }

    public void drawFireballs(Graphics g, int xLvlOffset) {
        for (Fireball fb : fireballs) {
            if (fb.isActive()) {
                if (!fb.collisionX) {
                    g.drawImage(animations[9][fb.getAniIndex()], (int) fb.x - xLvlOffset, (int) fb.y + 2, (int) fb.hitbox.width, (int) fb.hitbox.height, null);
                }
                if (fb.collisionX) {
                    fb.value = 0;
                    fb.airSpeed = 0;
                    if (fb.direction == LEFT) {
                        g.drawImage(animations[10][fb.getAniIndex()], (int) fb.x - xLvlOffset - Game.TILES_SIZE/2, (int) fb.y + 2, Game.TILES_SIZE, Game.TILES_SIZE, null);
                    } else {
                        g.drawImage(animations[10][fb.getAniIndex()], (int) fb.x - xLvlOffset, (int) fb.y + 2, Game.TILES_SIZE, Game.TILES_SIZE, null);
                    }

                    if (fb.getAniIndex() >= 2) {
                        fb.setActive(false);
                        KeyInputs.activeBalls--;
                    }
                }
            }
        }
    }
}
