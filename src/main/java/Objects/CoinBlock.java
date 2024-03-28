package Objects;

import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class CoinBlock {
    public static ArrayList<CoinBlock> coinBlocksList = new ArrayList<>();
    public Rectangle2D.Float hitbox;
    public int coinsInside = 10;
    private boolean active = true;
    private int movedCoinBlockX;
    public int coinBricksDownCounter = 20;
    public int coinBricksCounter;
    public boolean movedBlock = false;

    public CoinBlock(int x, int y) {
        hitbox = new Rectangle2D.Float();
        hitbox.x = x;
        hitbox.y = y;
        hitbox.width = Game.TILES_SIZE;
        hitbox.height = Game.TILES_SIZE;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getMovedCoinBlockX() {
        return movedCoinBlockX;
    }

    public void setMovedCoinBlockX(int movedCoinBlockX) {
        this.movedCoinBlockX = movedCoinBlockX;
    }
}
