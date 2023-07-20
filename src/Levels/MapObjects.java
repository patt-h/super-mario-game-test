package Levels;

import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class MapObjects {
    public static ArrayList<MapObjects> coinBlocksList = new ArrayList<>();
    Rectangle2D.Float hitbox;
    public int coinsInside = 10;
    private boolean active = true;

    public MapObjects(int x, int y) {
        hitbox = new Rectangle2D.Float();
        hitbox.x = x;
        hitbox.y = y;
        hitbox.width = Game.TILES_SIZE;
        hitbox.height = Game.TILES_SIZE;
    }

    public static ArrayList<MapObjects> getCoinBlocks() {
        return coinBlocksList;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
