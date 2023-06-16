package Objects;

import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Coin extends GameObject {
    public static ArrayList<Coin> CoinList = new ArrayList<>();

    public Coin(int x, int y, int objType) {
        super(x, y, objType);
        doAnimation = true;
        hitbox = new Rectangle2D.Float();
        hitbox.width = 10*Game.SCALE;
        hitbox.height = 14*Game.SCALE;
    }

    public static ArrayList<Coin> getCoins() {
        return CoinList;
    }

    public void update() {
        hitbox.x = x;
        hitbox.y = y;
        updateAnimationTick();
    }
}
