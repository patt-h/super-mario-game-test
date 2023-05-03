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
            case KeyEvent.VK_UP:
                gamePanel.getGame().getPlayer().setUp(true);
                break;
            case KeyEvent.VK_LEFT:
                gamePanel.getGame().getPlayer().setLeft(true);
                break;
            case KeyEvent.VK_DOWN:
                gamePanel.getGame().getPlayer().setDuck(true);
                break;
            case KeyEvent.VK_RIGHT:
                gamePanel.getGame().getPlayer().setRight(true);
                break;
            case KeyEvent.VK_Z:
                gamePanel.getGame().getPlayer().setJump(true);
                break;
            case KeyEvent.VK_X:
                gamePanel.getGame().getPlayer().setSprint(true);
                break;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                gamePanel.getGame().getPlayer().setUp(false);
                break;
            case KeyEvent.VK_LEFT:
                gamePanel.getGame().getPlayer().setLeft(false);
                break;
            case KeyEvent.VK_DOWN:
                gamePanel.getGame().getPlayer().setDuck(false);
                break;
            case KeyEvent.VK_RIGHT:
                gamePanel.getGame().getPlayer().setRight(false);
                break;
            case KeyEvent.VK_Z:
                gamePanel.getGame().getPlayer().setJump(false);
                break;
            case KeyEvent.VK_X:
                gamePanel.getGame().getPlayer().setSprint(false);
                break;
        }
    }
}
