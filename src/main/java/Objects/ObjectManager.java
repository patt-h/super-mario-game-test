package Objects;

import Audio.AudioPlayer;
import Entities.Player;
import Input.KeyInputs;
import Utilities.LoadSave;
import com.company.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static States.Playing.lvlLength;
import static Objects.Coin.CoinList;
import static Objects.CoinBlock.coinBlocksList;
import static Objects.FinishBar.FinishBarList;
import static Objects.FireFlower.FireFlowerList;
import static Objects.Fireball.FireballList;
import static Objects.Mushroom.MushroomList;
import static Objects.GreenMushroom.GreenMushroomList;
import static Objects.WarpPipe.WarpPipesMap;
import static Objects.WarpPipe.WorldWarpPipesMap;
import static Utilities.Constants.Directions.LEFT;
import static Utilities.Constants.Directions.RIGHT;
import static Utilities.Constants.ObjectConstants.*;
import static Utilities.Constants.PlayerConstants.*;

public class ObjectManager {
    private Player player;

    public BufferedImage[][] animations;
    private BufferedImage[][] effects;
    public ArrayList<Mushroom> mushrooms = new ArrayList<>();
    public ArrayList<FireFlower> fireFlowers = new ArrayList<>();
    public ArrayList<Fireball> fireballs = new ArrayList<>();
    public ArrayList<Coin> coins = new ArrayList<>();
    public Map<WarpPipe, WarpPipe> warppipes = new HashMap<>();
    public Map<WarpPipe, String> worldpipes = new HashMap<>();
    public ArrayList<FinishBar> finishBar = new ArrayList<>();
    public ArrayList<GreenMushroom> greenMushrooms = new ArrayList<>();

    public ObjectManager(Player player) {
        this.player = player;
        loadImg();
        addObjects();
    }

    private void addObjects() {
        mushrooms = Mushroom.getMushrooms();
        fireFlowers = FireFlower.getFireFlowers();
        fireballs = Fireball.getFireballs();
        coins = Coin.getCoins();
        warppipes = WarpPipe.getWarpPipes();
        worldpipes = WarpPipe.getWorldPipes();
        finishBar = FinishBar.getFinishBarList();
        greenMushrooms = GreenMushroom.getGreenMushrooms();
    }

    public void checkTouched() {
        for (Coin c : coins) {
            if (c.isActive()) {
                c.update();
                if (c.objType == COIN) {
                    if (player.hitbox.intersects(c.hitbox.x, c.hitbox.y, (int) c.hitbox.width, (int) c.hitbox.height)) {
                        c.setActive(false);
                        player.coins++;
                        player.getAudioPlayer().playEffect(AudioPlayer.COIN);
                        player.score += getScoreAmount(COIN);
                    }
                }
                else if (c.objType == COIN_BRICK) {
                    if (c.aniIndex >= 8) {
                        c.setActive(false);
                    }
                }
            }
        }

        for (Mushroom m : mushrooms) {
            if (m.isActive()) {
                m.update();
                if (player.hitbox.intersects(m.hitbox.x, m.hitbox.y, (int) m.hitbox.width, (int) m.hitbox.height)) {
                    if (player.playerStatus == SMALL) {
                        player.y -= Game.TILES_SIZE;
                        player.playerStatus = BIG;
                        player.bigUpgrade = true;
                    }
                    m.setActive(false);
                    m.setCollected(true);
                    player.getAudioPlayer().playEffect(AudioPlayer.POWER_UP);
                    player.score += getScoreAmount(MUSHROOM);
                }
            }
        }
        for (GreenMushroom gm : greenMushrooms) {
            if (gm.isActive()) {
                gm.update();
                if (player.hitbox.intersects(gm.hitbox.x, gm.hitbox.y, (int) gm.hitbox.width, (int) gm.hitbox.height)) {
                    gm.setActive(false);
                    gm.setCollected(true);
                    // TODO: CHANGE IT TO 1UP SOUND
                    player.getAudioPlayer().playEffect(AudioPlayer.POWER_UP);
                    player.lives++;
                }
            }
        }
        for (FireFlower f : fireFlowers) {
            if (f.isActive()) {
                f.update();
                if (player.hitbox.intersects(f.hitbox.x, f.hitbox.y, (int) f.hitbox.width, (int) f.hitbox.height)) {
                    if (player.playerStatus == SMALL) {
                        player.y -= Game.TILES_SIZE;
                        player.playerStatus = BIG;
                        player.bigUpgrade = true;

                    }
                    else if (player.playerStatus == BIG) {
                        player.playerStatus = FIRE;
                        player.fireUpgrade = true;
                    }
                    f.setActive(false);
                    f.setCollected(true);
                    player.getAudioPlayer().playEffect(AudioPlayer.POWER_UP);
                    player.score += getScoreAmount(FIRE_FLOWER);
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
                if (fb.x - player.xLvlOffset > Game.GAME_WIDTH && fb.direction == RIGHT) {
                    fb.setActive(false);
                    KeyInputs.activeBalls--;
                }
                else if (fb.x - player.xLvlOffset < -fb.width  && fb.direction == LEFT) {
                    fb.setActive(false);
                    KeyInputs.activeBalls--;
                }
            }
        }

        warppipes.forEach((k, v) -> k.checkPlayerDuck(player));
        worldpipes.forEach((k, v) -> k.checkPlayerDuck(player));
    }

    private void loadImg() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.POWERUPS_ATLAS);
        BufferedImage ef = LoadSave.GetSpriteAtlas(LoadSave.EFFECTS_IMG);

        animations = new BufferedImage[11][5];
        effects = new BufferedImage[9][9];

        for (int y = 0; y < animations.length; y++) {
            for (int x = 0; x < animations[y].length; x++) {
                animations[y][x] = img.getSubimage((x*16)+x+1,(y*16)+y+1,16,16);
                if (animations[y][x] == animations[8][x]) {
                    animations[y][x] = img.getSubimage((x*10)+x+1,120,10,14);
                }
                if (animations[y][x] == animations[9][x]) {
                    animations[y][x] = img.getSubimage((x*8)+x+1,135,8,8);
                }
                if (animations[y][x] == animations[10][x]) {
                    animations[y][x] = img.getSubimage((x*16)+x+1,144,16,16);
                }
            }
        }
        for (int y = 0; y < effects.length; y++) {
            for (int x = 0; x < effects[y].length; x++) {
                effects[y][x] = ef.getSubimage((x*20)+x+1, (y*16)+y+1, 20,16);
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
        drawCoins(g, xLvlOffset);
        drawWarpPipes(g, xLvlOffset);
        drawFinishBar(g, xLvlOffset);
        drawGreenMushrooms(g, xLvlOffset);
    }

    public void drawCoins(Graphics g, int xLvlOffset) {
        for (Coin c : coins) {
            if (c.isActive() && c.x - xLvlOffset < Game.GAME_WIDTH && c.x - xLvlOffset + c.getWidth() > 0) {
                if (c.objType == COIN) {
                    g.drawImage(animations[8][c.getAniIndex()], (int)c.x - xLvlOffset, (int)c.y, c.getWidth(), c.getHeight(), null);
                }
                else if (c.objType == COIN_BRICK) {
                    g.drawImage(effects[0][c.getAniIndex()], (int) c.x - xLvlOffset - 5, (int) c.y-50, 20*Game.SCALE, 16*Game.SCALE, null);
                    if (c.getAniIndex() < 6) {
                        c.y-=1.5;
                    }
                    else {
                        c.y+=0.5;
                    }
                }
            }
        }
    }

    public void drawMushrooms(Graphics g, int xLvlOffset) {
        for (Mushroom m : mushrooms) {
            if (m.isActive() && m.x - xLvlOffset < Game.GAME_WIDTH && m.x - xLvlOffset + m.getWidth() > 0) {
                g.drawImage(animations[0][m.getAniIndex()], (int)m.x - xLvlOffset - 3, (int)m.y+2, m.getWidth(), m.getHeight(), null);
            }
            if (m.isCollected()) {
                m.drawScoreAdded(m.x - xLvlOffset, m.y, getScoreAmount(MUSHROOM), g);
            }
        }
    }

    public void drawGreenMushrooms(Graphics g, int xLvlOffset) {
        for (GreenMushroom gm : greenMushrooms) {
            if (gm.isActive() && gm.x - xLvlOffset < Game.GAME_WIDTH && gm.x - xLvlOffset + gm.getWidth() > 0) {
                g.drawImage(animations[0][1], (int)gm.x - xLvlOffset - 3, (int)gm.y+2, gm.getWidth(), gm.getHeight(), null);
            }
            if (gm.isCollected()) {
                gm.drawString(gm.x - xLvlOffset, gm.y, "1UP", g);
            }
        }
    }

    public void drawFireFlowers(Graphics g, int xLvlOffset) {
        for (FireFlower f : fireFlowers) {
            if (f.isActive() && f.x - xLvlOffset < Game.GAME_WIDTH && f.x - xLvlOffset + f.getWidth() > 0) {
                g.drawImage(animations[1][f.getAniIndex()], (int)f.x - xLvlOffset, (int)f.y+2, f.getWidth(), f.getHeight(), null);
            }
            if (f.isCollected()) {
                f.drawScoreAdded(f.x - xLvlOffset, f.y, getScoreAmount(FIRE_FLOWER), g);
            }
        }
    }

    public void drawFireballs(Graphics g, int xLvlOffset) {
        for (Fireball fb : fireballs) {
            if (fb.isActive() && fb.x - xLvlOffset < Game.GAME_WIDTH && fb.x - xLvlOffset + fb.getWidth() > 0) {
                if (!fb.collisionX) {
                    g.drawImage(animations[9][fb.getAniIndex()], (int) fb.x - xLvlOffset, (int) fb.y + 2, fb.getWidth(), fb.getHeight(), null);
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
                if (fb.x + Game.TILES_SIZE+1 > lvlLength * Game.TILES_SIZE || fb.x < 0){
                    KeyInputs.activeBalls--;
                    fb.setActive(false);
                }
            }
        }
    }

    public void drawWarpPipes(Graphics g, int xLvlOffset) {
        warppipes.forEach((k, v) -> {
            if (Game.debugMode) {
                g.setColor(Color.RED);
                g.drawRect((int) k.hitbox.x - xLvlOffset, (int) k.hitbox.y, (int) k.hitbox.width, (int) k.hitbox.height);
                g.drawRect((int) v.hitbox.x - xLvlOffset, (int) v.hitbox.y, (int) v.hitbox.width, (int) v.hitbox.height);
            }
        });
        worldpipes.forEach((k, v) -> {
            if (Game.debugMode) {
                g.setColor(Color.RED);
                g.drawRect((int) k.hitbox.x - xLvlOffset, (int) k.hitbox.y, (int) k.hitbox.width, (int) k.hitbox.height);
            }
        });
    }

    public void drawFinishBar(Graphics g, int xLvlOffset) {
        for (FinishBar f : finishBar) {
            if (f.isActive() && f.x - xLvlOffset < Game.GAME_WIDTH && f.x - xLvlOffset + f.getWidth() > 0) {
                f.update();
                f.checkTouched(player);
                g.drawImage(f.frames[f.getAniIndex()], (int)f.x - xLvlOffset, (int)f.y, f.getWidth(), f.getHeight(), null);

                if (Game.debugMode) {
                    g.setColor(Color.RED);
                    g.drawRect((int)f.hitbox.x - xLvlOffset, (int)f.hitbox.y, (int) f.hitbox.width, (int) f.hitbox.height );
                }
            }
        }
    }

    public void resetObjects() {
        MushroomList.clear();
        FireFlowerList.clear();
        coinBlocksList.clear();
        CoinList.clear();
        FireballList.clear();
        WarpPipesMap.clear();
        WorldWarpPipesMap.clear();
        FinishBarList.clear();
        GreenMushroomList.clear();
    }
}
