package Input;

import com.company.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInputs implements KeyListener {
    private GamePanel gamePanel;

    public KeyInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> gamePanel.getGame().getPlayer().setUp(true);
            case KeyEvent.VK_LEFT -> gamePanel.getGame().getPlayer().setLeft(true);
            case KeyEvent.VK_DOWN -> gamePanel.getGame().getPlayer().setDuck(true);
            case KeyEvent.VK_RIGHT -> gamePanel.getGame().getPlayer().setRight(true);
            case KeyEvent.VK_Z -> gamePanel.getGame().getPlayer().setJump(true);
            case KeyEvent.VK_X -> gamePanel.getGame().getPlayer().setSprint(true);
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> gamePanel.getGame().getPlayer().setUp(false);
            case KeyEvent.VK_LEFT -> gamePanel.getGame().getPlayer().setLeft(false);
            case KeyEvent.VK_DOWN -> gamePanel.getGame().getPlayer().setDuck(false);
            case KeyEvent.VK_RIGHT -> gamePanel.getGame().getPlayer().setRight(false);
            case KeyEvent.VK_Z -> gamePanel.getGame().getPlayer().setJump(false);
            case KeyEvent.VK_X -> gamePanel.getGame().getPlayer().setSprint(false);
        }
    }
}
