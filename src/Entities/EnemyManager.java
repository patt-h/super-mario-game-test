package Entities;

import Input.KeyInputs;
import Levels.Playing;
import Objects.Fireball;
import Utilities.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Utilities.Constants.PlayerConstants.*;

public class EnemyManager {
    public BufferedImage[][] animations;
    private int counter;

    public ArrayList<Goomba> goombas = new ArrayList<>();
    public ArrayList<Fireball> fireballs = new ArrayList<>();

    public EnemyManager() {
        loadAnimation();
        addEnemies();
    }

    private void addEnemies() {
        fireballs = Fireball.getFireballs();
        goombas = Goomba.getGoomba();
    }

    public void checkTouched() {
        for (Goomba go : goombas) {
            if (go.isActive()) {
                go.update();
                if (Player.hitbox.intersects(go.damageHitbox) && !Player.hitbox.intersects(go.hitbox) && Player.airSpeed > 0) {
                    go.stepped = true;
                    Playing.score += 200;
                }
                else if (Player.hitbox.intersects(go.hitbox) && !go.stepped && !go.fireballed) {
                    if (Player.playerStatus == FIRE) {
                        Player.playerStatus = BIG;
                    }
                    if (Player.playerStatus == BIG) {
                        Player.playerStatus = SMALL;
                        if (!Player.inAir) {
                            Player.y = Player.y + 48;
                        }
                    }
                }
                for (Fireball fb : fireballs) {
                    if (fb.isActive()) {
                        if (fb.hitbox.intersects(go.hitbox)) {
                            go.fireballed = true;
                            fb.setActive(false);
                            KeyInputs.activeBalls--;
                        }
                    }
                }
            }
        }
    }

    public void update() {
        checkTouched();
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

    public void draw(Graphics g, int xLvlOffset) {
        drawGoombas(g, xLvlOffset);
    }

    public void drawGoombas(Graphics g, int xLvlOffset) {
        for (Goomba go : goombas) {
            if (go.isActive()) {
                if (!go.stepped && !go.fireballed) {
                    g.drawImage(animations[go.accurateAnimationRow][go.getAniIndex()], (int) go.x - xLvlOffset, (int) go.y - 20, 120, 120, null);
                }
                else if (go.stepped) {
                    g.drawImage(animations[go.accurateAnimationRow][2], (int) go.x - xLvlOffset, (int) go.y-8, 120, 120, null);
                    counter++;
                    if (counter >= 144) {
                        go.setActive(false);
                        counter = 0;
                    }
                }
                else if (go.fireballed) {

                }
                if (Player.debugMode) {
                    g.drawRect((int) go.hitbox.x - xLvlOffset, (int) go.hitbox.y, (int) go.hitbox.width, (int) go.hitbox.height);
                    g.setColor(Color.RED);
                    g.drawRect((int) go.damageHitbox.x - xLvlOffset, (int) go.damageHitbox.y, (int) go.damageHitbox.width, (int) go.damageHitbox.height);
                }
            }
        }
    }
}