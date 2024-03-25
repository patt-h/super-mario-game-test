package Levels;

import com.company.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class MapObjects {
    public static ArrayList<MapObjects> coinBlocksList = new ArrayList<>();
    public Rectangle2D.Float hitbox;
    public int coinsInside = 10;
    private boolean active = true;
    private int movedCoinBlockX;
    public int coinBricksDownCounter = 20;
    public int coinBricksCounter;
    public boolean movedBlock = false;

    public MapObjects(int x, int y) {
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
