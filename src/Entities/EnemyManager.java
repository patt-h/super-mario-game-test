package Entities;

import Input.KeyInputs;
import Objects.Fireball;
import Utilities.LoadSave;
import com.company.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Utilities.Constants.Directions.LEFT;
import static Utilities.Constants.Directions.RIGHT;
import static Utilities.Constants.EnemyConstants.*;
import static Utilities.Constants.PlayerConstants.*;

public class EnemyManager {
    public BufferedImage[][] animations;
    private int counter;

    public ArrayList<Goomba> goombas = new ArrayList<>();
    public ArrayList<Troopa> troopas = new ArrayList<>();

    public ArrayList<Fireball> fireballs = new ArrayList<>();

    public EnemyManager() {
        loadAnimation();
        addEnemies();
    }

    private void addEnemies() {
        goombas = Goomba.getGoomba();
        troopas = Troopa.getTroopa();
        fireballs = Fireball.getFireballs();
    }

    private void killedByShell() {
        for (Troopa tr : troopas) {
            if (tr.isActive() && tr.enemyType == TROOPA_KICKED) {
                for (Goomba go : goombas) {
                    if (go.isActive()) {
                        if (tr.hitbox.intersects(go.hitbox) && !go.stepped && !go.fireballed) {
                            tr.killstreak++;
                            go.killstreak = tr.killstreak;
                            System.out.println(go.killstreak);
                            go.setKilledByShell(true);
                            go.fireballed = true;
                            if (tr.killstreak < 9) {
                                Player.score += getScoreKillstreak(tr.killstreak);
                            }
                            else {
                                Player.lives++;
                            }
                        }
                    }
                }
                for (Troopa tr2 : troopas) {
                    if (tr2.isActive()) {
                        if (tr.hitbox.intersects(tr2.hitbox) && !tr2.hitbox.intersects(tr2.hitbox) && !tr2.stepped && !tr2.fireballed) {
                            tr.killstreak++;
                            tr2.setKilledByShell(true);
                            tr2.fireballed = true;
                            if (tr.killstreak < 9) {
                                Player.score += getScoreKillstreak(tr.killstreak);
                            }
                            else {
                                Player.lives++;
                            }
                        }
                    }
                }
            }
        }
    }

    public void checkTouched() {
        //ADDING NEW ENEMIES WITH SCHEME BELOW
        for (Goomba go : goombas) {
            if (go.isActive()) {
                go.update();
                if (Player.hitbox.intersects(go.damageHitbox) && !Player.hitbox.intersects(go.hitbox) && Player.airSpeed > 0 && !go.fireballed && !go.stepped && Player.playerStatus != DEAD) {
                    go.stepped = true;
                    go.setKilled(true);
                    Player.y -= 48;
                    Player.airSpeed = -5;
                    Player.score += getEnemyScoreAmount(GOOMBA);
                }
                else if (Player.hitbox.intersects(go.hitbox) && !go.stepped && !go.fireballed) {
                    if (Player.playerStatus == FIRE && !Player.immortality) {
                        Player.playerStatus = BIG;
                        Player.immortality = true;
                        Player.gotHit = true;
                    }
                    if (Player.playerStatus == BIG && !Player.immortality) {
                        Player.playerStatus = SMALL;
                        Player.immortality = true;
                        Player.gotHit = true;
                        if (!Player.inAir) {
                            Player.y = Player.y + 48;
                        }
                    }
                    if (Player.playerStatus == SMALL && !Player.immortality) {
                        Player.playerStatus = DEAD;
                        Player.inAir = false;
                    }
                }
                for (Fireball fb : fireballs) {
                    if (fb.isActive()) {
                        if (fb.hitbox.intersects(go.hitbox) && !go.stepped && !go.fireballed) {
                            go.fireballed = true;
                            go.setKilled(true);
                            fb.setActive(false);
                            KeyInputs.activeBalls--;
                            Player.score += getEnemyScoreAmount(GOOMBA);
                        }
                    }
                }
            }
        }
        for (Troopa tr : troopas) {
            if (tr.isActive()) {
                tr.update();
                //STEPPING
                if (Player.hitbox.intersects(tr.damageHitbox) && !Player.hitbox.intersects(tr.hitbox) && Player.airSpeed > 0 && Player.playerStatus != DEAD && !tr.fireballed && !tr.kicked && !tr.stepped) {
                    Player.y -= 48;
                    Player.airSpeed = -5;
                    Player.score += getEnemyScoreAmount(TROOPA);
                    tr.stepped = true;
                    tr.killed = true;
                }
                //KICKING SHELL
                if (Player.hitbox.intersects(tr.hitbox) && tr.stepped && Player.airSpeed >= 0 && Player.playerStatus != DEAD && !tr.kicked) {
                    tr.tempImmortal = 0;
                    tr.kicked = true;
                    tr.stepped = false;
                    tr.enemyType = TROOPA_KICKED;
                    if (Player.direction == RIGHT) {
                        tr.direction = RIGHT;
                    }
                    else {
                        tr.direction = LEFT;
                    }
                }
                //STOPPING SHELL
                if (tr.kicked && Player.hitbox.intersects(tr.damageHitbox) && !Player.hitbox.intersects(tr.hitbox) && !tr.stepped && Player.airSpeed > 0 && Player.playerStatus != DEAD) {
                    tr.tempImmortal = 0;
                    Player.y -= 48;
                    Player.airSpeed = -5;
                    tr.stepped = true;
                    tr.kicked = false;
                    tr.killstreak = 0;
                    tr.enemyType = TROOPA;
                }
                //GETTING HIT
                if (Player.hitbox.intersects(tr.hitbox) && !tr.fireballed && tr.tempImmortal == 40) {
                    if (Player.playerStatus == FIRE && !Player.immortality) {
                        Player.playerStatus = BIG;
                        Player.immortality = true;
                        Player.gotHit = true;
                    }
                    if (Player.playerStatus == BIG && !Player.immortality) {
                        Player.playerStatus = SMALL;
                        Player.immortality = true;
                        Player.gotHit = true;
                        if (!Player.inAir) {
                            Player.y = Player.y + 48;
                        }
                    }
                    if (Player.playerStatus == SMALL && !Player.immortality) {
                        Player.playerStatus = DEAD;
                        Player.inAir = false;
                    }
                }
                for (Fireball fb : fireballs) {
                    if (fb.isActive()) {
                        if (fb.hitbox.intersects(tr.hitbox) && !tr.fireballed) {
                            tr.fireballed = true;
                            tr.killed = true;
                            tr.enemyType = TROOPA;
                            fb.setActive(false);
                            KeyInputs.activeBalls--;
                            Player.score += getEnemyScoreAmount(TROOPA);
                        }
                    }
                }
            }
        }
    }

    public void update() {
        checkTouched();
        killedByShell();
    }

    private void loadAnimation() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.ENEMY_ATLAS);

        animations = new BufferedImage[11][10];
        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                animations[j][i] = img.getSubimage(i * 40, j * 40, 40, 40);
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawGoombas(g, xLvlOffset);
        drawTroopas(g, xLvlOffset);
    }

    public void drawGoombas(Graphics g, int xLvlOffset) {
        for (Goomba go : goombas) {
            if (go.isActive()) {
                if (!go.stepped && !go.fireballed) {
                    g.drawImage(animations[go.accurateAnimationRow][go.getAniIndex()], (int) go.x - xLvlOffset, (int) go.y + 1, 120, 120, null);
                }
                if (go.stepped) {
                    g.drawImage(animations[go.accurateAnimationRow][2], (int) go.x - xLvlOffset, (int) go.y + 13, 120, 120, null);
                    counter++;
                    if (counter >= 144) {
                        go.setActive(false);
                        counter = 0;
                    }
                }
                if (go.fireballed) {
                    g.drawImage(animations[go.accurateAnimationRow][3], (int) go.x - xLvlOffset, (int) go.y, 120, 120, null);
                    if (go.y > Game.GAME_HEIGHT) {
                        go.setActive(false);
                    }
                }
                if (Player.debugMode) {
                    g.drawRect((int) go.hitbox.x - xLvlOffset, (int) go.hitbox.y, (int) go.hitbox.width, (int) go.hitbox.height);
                    g.setColor(Color.RED);
                    g.drawRect((int) go.damageHitbox.x - xLvlOffset, (int) go.damageHitbox.y, (int) go.damageHitbox.width, (int) go.damageHitbox.height);
                }
            }
            if (go.isKilled()) {
                go.drawScoreAdded(go.hitbox.x - xLvlOffset, go.hitbox.y, String.valueOf(getEnemyScoreAmount(GOOMBA)), g);
            }
            else if (go.isKilledByShell()) {
                for (Troopa trShell : troopas) {
                    if (trShell.isActive() && trShell.enemyType == TROOPA_KICKED) {
                        if (go.killstreak < 9) {
                            go.drawScoreAdded(go.hitbox.x - xLvlOffset, go.hitbox.y, String.valueOf(getScoreKillstreak(go.killstreak)), g);
                        }
                        else {
                            go.drawScoreAdded(go.hitbox.x - xLvlOffset, go.hitbox.y, "1UP", g);
                        }
                    }
                }
            }
        }
    }

    public void drawTroopas(Graphics g, int xLvlOffset) {
        for (Troopa tr : troopas) {
            if (tr.isActive()) {
                if (!tr.stepped && !tr.kicked && !tr.fireballed) {
                    g.drawImage(animations[2][tr.getAniIndex()], (int) tr.x - xLvlOffset, (int) tr.y - 20, 120, 120, null);
                }
                if (tr.stepped && !tr.fireballed) {
                    g.drawImage(animations[3][0], (int) tr.x - xLvlOffset, (int) tr.y - 14, 120, 120, null);
                }
                if (tr.kicked && !tr.fireballed) {
                    g.drawImage(animations[3][tr.getAniIndex()], (int) tr.x - xLvlOffset, (int) tr.y - 14, 120, 120, null);
                }
                if (tr.fireballed) {
                    g.drawImage(animations[3][4], (int) tr.x - xLvlOffset, (int) tr.y, 120, 120, null);
                    if (tr.y > Game.GAME_HEIGHT) {
                        tr.setActive(false);
                    }
                }
                if (Player.debugMode) {
                    g.drawRect((int) tr.hitbox.x - xLvlOffset, (int) tr.hitbox.y, (int) tr.hitbox.width, (int) tr.hitbox.height);
                    g.setColor(Color.RED);
                    g.drawRect((int) tr.damageHitbox.x - xLvlOffset, (int) tr.damageHitbox.y, (int) tr.damageHitbox.width, (int) tr.damageHitbox.height);
                }
            }
            if (tr.isKilled()) {
                tr.drawScoreAdded(tr.hitbox.x - xLvlOffset, tr.hitbox.y, String.valueOf(getEnemyScoreAmount(TROOPA)), g);
            }
        }
    }
}