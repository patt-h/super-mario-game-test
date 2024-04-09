package Input;

import Audio.AudioPlayer;
import Objects.Fireball;
import com.company.Game;
import com.company.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static Objects.Fireball.FireballList;
import static Utilities.Constants.Directions.LEFT;
import static Utilities.Constants.Directions.RIGHT;
import static Utilities.Constants.ObjectConstants.FIRE_BALL;
import static Utilities.Constants.PlayerConstants.FIRE;

public class KeyInputs implements KeyListener {
    private GamePanel gamePanel;
    private boolean blockFire;
    private boolean blockJump;
    public static int activeBalls;

    public KeyInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if (!gamePanel.getGame().getPlayer().isBlockMovement()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> gamePanel.getGame().getPlayer().setUp(true);
                case KeyEvent.VK_LEFT -> {
                    if (!gamePanel.getGame().getPlayer().duck) {
                        gamePanel.getGame().getPlayer().setLeft(true);
                    }
                }
                case KeyEvent.VK_DOWN -> gamePanel.getGame().getPlayer().setDuck(true);
                case KeyEvent.VK_RIGHT -> {
                    if (!gamePanel.getGame().getPlayer().duck) {
                        gamePanel.getGame().getPlayer().setRight(true);
                    }
                }
                case KeyEvent.VK_Z -> {
                    if (!blockJump && !gamePanel.getGame().getPlayer().inAir) {
                        gamePanel.getGame().getPlayer().setJump(true);
                        gamePanel.getGame().getPlayer().getAudioPlayer().playEffect(AudioPlayer.JUMP);
                        blockJump = true;
                    }
                }
                case KeyEvent.VK_X -> {
                    gamePanel.getGame().getPlayer().setSprint(true);
                    if (gamePanel.getGame().getPlayer().playerStatus == FIRE && !blockFire && activeBalls < 2 && !gamePanel.getGame().getPlayer().isDuck()) {
                        if (gamePanel.getGame().getPlayer().direction == RIGHT) {
                            FireballList.add(new Fireball((int) gamePanel.getGame().getPlayer().x + Game.TILES_SIZE / 2, (int) gamePanel.getGame().getPlayer().y + Game.TILES_SIZE / 2, FIRE_BALL, gamePanel.getGame().getPlayer().direction));
                        }
                        else if (gamePanel.getGame().getPlayer().direction == LEFT) {
                            FireballList.add(new Fireball((int) gamePanel.getGame().getPlayer().x, (int) gamePanel.getGame().getPlayer().y + Game.TILES_SIZE / 2, FIRE_BALL, gamePanel.getGame().getPlayer().direction));
                        }
                        blockFire = true;
                        activeBalls++;
                    }
                }

                case KeyEvent.VK_PAGE_UP -> Game.debugMode = true;
                case KeyEvent.VK_PAGE_DOWN -> Game.debugMode = false;
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        if (!gamePanel.getGame().getPlayer().isBlockMovement()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> gamePanel.getGame().getPlayer().setUp(false);
                case KeyEvent.VK_LEFT -> {
                    if (!gamePanel.getGame().getPlayer().duck) {
                        gamePanel.getGame().getPlayer().setLeft(false);
                    }
                }
                case KeyEvent.VK_DOWN -> gamePanel.getGame().getPlayer().setDuck(false);
                case KeyEvent.VK_RIGHT -> {
                    if (!gamePanel.getGame().getPlayer().duck) {
                        gamePanel.getGame().getPlayer().setRight(false);
                    }
                }
                case KeyEvent.VK_Z -> {
                    gamePanel.getGame().getPlayer().setJump(false);
                    blockJump = false;
                }
                case KeyEvent.VK_X -> {
                    gamePanel.getGame().getPlayer().setSprint(false);
                    blockFire = false;
                }
            }
        }
    }
}
