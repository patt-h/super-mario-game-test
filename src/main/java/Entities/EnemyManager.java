package Entities;

import Input.KeyInputs;
import Objects.Fireball;
import Utilities.LoadSave;
import com.company.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Entities.Goomba.GoombaList;
import static Entities.Piranha.PiranhaList;
import static Entities.Troopa.TroopaList;
import static Utilities.Constants.Directions.LEFT;
import static Utilities.Constants.Directions.RIGHT;
import static Utilities.Constants.EnemyConstants.*;
import static Utilities.Constants.PlayerConstants.*;

public class EnemyManager {
    private Player player;
    public BufferedImage[][] animations;
    private int counter;

    public ArrayList<Goomba> goombas = new ArrayList<>();
    public ArrayList<Troopa> troopas = new ArrayList<>();
    public ArrayList<Piranha> piranhas = new ArrayList<>();

    public ArrayList<Fireball> fireballs = new ArrayList<>();

    public EnemyManager(Player player) {
        this.player = player;
        loadAnimation();
        addEnemies();
    }

    private void addEnemies() {
        goombas = Goomba.getGoomba();
        troopas = Troopa.getTroopa();
        piranhas = Piranha.getPiranha();
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
                            go.setKilledByShell(true);
                            go.fireballed = true;
                            if (tr.killstreak < 9) {
                                player.score += getScoreKillstreak(tr.killstreak);
                            }
                            else {
                                player.lives++;
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
                                player.score += getScoreKillstreak(tr.killstreak);
                            }
                            else {
                                player.lives++;
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
                if (player.hitbox.intersects(go.damageHitbox) && !player.hitbox.intersects(go.hitbox) && player.airSpeed > 0 && !go.fireballed && !go.stepped && player.playerStatus != DEAD) {
                    go.stepped = true;
                    go.setKilled(true);
                    player.y -= 48;
                    player.airSpeed = -5;
                    player.score += getEnemyScoreAmount(GOOMBA);
                }
                else if (player.hitbox.intersects(go.hitbox) && !go.stepped && !go.fireballed) {
                    if (player.playerStatus == FIRE && !player.immortality) {
                        player.playerStatus = BIG;
                        player.immortality = true;
                        player.gotHit = true;
                    }
                    if (player.playerStatus == BIG && !player.immortality) {
                        player.playerStatus = SMALL;
                        player.immortality = true;
                        player.gotHit = true;
                        if (!player.inAir) {
                            player.y = player.y + 48;
                        }
                    }
                    if (player.playerStatus == SMALL && !player.immortality) {
                        player.playerStatus = DEAD;
                        player.inAir = false;
                    }
                }
                for (Fireball fb : fireballs) {
                    if (fb.isActive()) {
                        if (fb.hitbox.intersects(go.hitbox) && !go.stepped && !go.fireballed) {
                            go.fireballed = true;
                            go.setKilled(true);
                            fb.setActive(false);
                            KeyInputs.activeBalls--;
                            player.score += getEnemyScoreAmount(GOOMBA);
                        }
                    }
                }
            }
        }
        for (Troopa tr : troopas) {
            if (tr.isActive()) {
                tr.update();
                //STEPPING
                if (player.hitbox.intersects(tr.damageHitbox) && !player.hitbox.intersects(tr.hitbox) && player.airSpeed > 0 && player.playerStatus != DEAD && !tr.fireballed && !tr.kicked && !tr.stepped) {
                    player.y -= 48;
                    player.airSpeed = -5;
                    player.score += getEnemyScoreAmount(TROOPA);
                    tr.stepped = true;
                    tr.killed = true;
                }
                //KICKING SHELL
                if (player.hitbox.intersects(tr.hitbox) && tr.stepped && player.airSpeed >= 0 && player.playerStatus != DEAD && !tr.kicked) {
                    tr.tempImmortal = 0;
                    tr.kicked = true;
                    tr.stepped = false;
                    tr.enemyType = TROOPA_KICKED;
                    if (player.direction == RIGHT) {
                        tr.direction = RIGHT;
                    }
                    else {
                        tr.direction = LEFT;
                    }
                }
                //STOPPING SHELL
                if (tr.kicked && player.hitbox.intersects(tr.damageHitbox) && !player.hitbox.intersects(tr.hitbox) && !tr.stepped && player.airSpeed > 0 && player.playerStatus != DEAD) {
                    tr.tempImmortal = 0;
                    player.y -= 48;
                    player.airSpeed = -5;
                    tr.stepped = true;
                    tr.kicked = false;
                    tr.killstreak = 0;
                    tr.enemyType = TROOPA;
                }
                //GETTING HIT
                if (player.hitbox.intersects(tr.hitbox) && !tr.fireballed && tr.tempImmortal == 40) {
                    if (player.playerStatus == FIRE && !player.immortality) {
                        player.playerStatus = BIG;
                        player.immortality = true;
                        player.gotHit = true;
                    }
                    if (player.playerStatus == BIG && !player.immortality) {
                        player.playerStatus = SMALL;
                        player.immortality = true;
                        player.gotHit = true;
                        if (!player.inAir) {
                            player.y = player.y + 48;
                        }
                    }
                    if (player.playerStatus == SMALL && !player.immortality) {
                        player.playerStatus = DEAD;
                        player.inAir = false;
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
                            player.score += getEnemyScoreAmount(TROOPA);
                        }
                    }
                }
            }
        }
        for (Piranha pi : piranhas) {
            //GETTING HIT
            if (pi.isActive()) {
                pi.update();

                if (Math.abs(player.x - pi.x) <= 174 && Math.abs(player.x - pi.x) >= 0) {
                    pi.setPlayerNearby(true);
                }
                else {
                    if (pi.isPlayerNearby) {
                        pi.setPlayerNearby(false);
                    }
                }

                if (player.hitbox.intersects(pi.hitbox) && !pi.fireballed) {
                    if (player.playerStatus == FIRE && !player.immortality) {
                        player.playerStatus = BIG;
                        player.immortality = true;
                        player.gotHit = true;
                    }
                    if (player.playerStatus == BIG && !player.immortality) {
                        player.playerStatus = SMALL;
                        player.immortality = true;
                        player.gotHit = true;
                        if (!player.inAir) {
                            player.y = player.y + 48;
                        }
                    }
                    if (player.playerStatus == SMALL && !player.immortality) {
                        player.playerStatus = DEAD;
                        player.inAir = false;
                    }
                }
                for (Fireball fb : fireballs) {
                    if (fb.isActive()) {
                        if (fb.hitbox.intersects(pi.hitbox) && !pi.fireballed) {
                            pi.fireballed = true;
                            pi.setKilled(true);
                            fb.setActive(false);
                            KeyInputs.activeBalls--;
                            player.score += getEnemyScoreAmount(PIRANHA);
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
        drawPiranhas(g, xLvlOffset);
    }

    public void drawGoombas(Graphics g, int xLvlOffset) {
        for (Goomba go : goombas) {
            if (go.isActive() && go.x - xLvlOffset < Game.GAME_WIDTH && go.x - xLvlOffset + go.getWidth() > 0) {
                if (!go.stepped && !go.fireballed) {
                    g.drawImage(animations[go.accurateAnimationRow][go.getAniIndex()], (int) go.x - xLvlOffset, (int) go.y + 1, go.getWidth(), go.getHeight(), null);
                }
                if (go.stepped) {
                    g.drawImage(animations[go.accurateAnimationRow][2], (int) go.x - xLvlOffset, (int) go.y + 13, go.getWidth(), go.getHeight(), null);
                    counter++;
                    if (counter >= 144) {
                        go.setActive(false);
                        counter = 0;
                    }
                }
                if (go.fireballed) {
                    g.drawImage(animations[go.accurateAnimationRow][3], (int) go.x - xLvlOffset, (int) go.y, go.getWidth(), go.getHeight(), null);
                    if (go.y > Game.GAME_HEIGHT) {
                        go.setActive(false);
                    }
                }
                if (Game.debugMode) {
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
            if (tr.isActive() && tr.x - xLvlOffset < Game.GAME_WIDTH && tr.x - xLvlOffset + tr.getWidth() > 0) {
                if (!tr.stepped && !tr.kicked && !tr.fireballed) {
                    g.drawImage(animations[2][tr.getAniIndex()], (int) tr.x - xLvlOffset, (int) tr.y - 20, tr.getWidth(), tr.getHeight(), null);
                }
                if (tr.stepped && !tr.fireballed) {
                    g.drawImage(animations[3][0], (int) tr.x - xLvlOffset, (int) tr.y - 14, tr.getWidth(), tr.getHeight(), null);
                }
                if (tr.kicked && !tr.fireballed) {
                    g.drawImage(animations[3][tr.getAniIndex()], (int) tr.x - xLvlOffset, (int) tr.y - 14, tr.getWidth(), tr.getHeight(), null);
                }
                if (tr.fireballed) {
                    g.drawImage(animations[3][4], (int) tr.x - xLvlOffset, (int) tr.y, tr.getWidth(), tr.getHeight(), null);
                    if (tr.y > Game.GAME_HEIGHT) {
                        tr.setActive(false);
                    }
                }
                if (Game.debugMode) {
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

    public void drawPiranhas(Graphics g, int xLvlOffset) {
        for (Piranha pi : piranhas) {
            if (pi.isActive() && pi.x - xLvlOffset < Game.GAME_WIDTH && pi.x - xLvlOffset + pi.getWidth() > 0) {
                if (!pi.fireballed) {
                    g.drawImage(animations[4][pi.getAniIndex()], (int) pi.x - xLvlOffset, (int) pi.y - 20, pi.getWidth(), pi.getHeight(), null);
                }
                if (pi.fireballed) {
                    pi.setActive(false);
                }
                if (Game.debugMode) {
                    g.drawRect((int) pi.hitbox.x - xLvlOffset, (int) pi.hitbox.y, (int) pi.hitbox.width, (int) pi.hitbox.height);
                    g.setColor(Color.RED);
                    g.drawRect((int) pi.damageHitbox.x - xLvlOffset, (int) pi.damageHitbox.y, (int) pi.damageHitbox.width, (int) pi.damageHitbox.height);
                }
            }
            if (pi.isKilled()) {
                pi.drawScoreAdded(pi.hitbox.x - xLvlOffset, pi.hitbox.y, String.valueOf(getEnemyScoreAmount(PIRANHA)), g);
            }
        }
    }

    public void resetEnemy() {
        GoombaList.clear();
        TroopaList.clear();
        PiranhaList.clear();
    }
}