package Objects;

import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class FireFlower extends GameObject {
    public static ArrayList<FireFlower> FireFlowerList = new ArrayList<>();
    private int counter;
    public boolean ready = false;

    public FireFlower(int x, int y, int objType) {
        super(x, y, objType);
        width = Game.TILES_SIZE;
        height = Game.TILES_SIZE;
        doAnimation = true;
        hitbox = new Rectangle2D.Float();
        hitbox.width = Game.TILES_SIZE;
        hitbox.height = Game.TILES_SIZE;
    }

    private void updatePosition() {
        hitbox.x = x;
        hitbox.y = y;
        if (!ready) {
            y -= 0.25;
            counter++;
            if (counter == 4 * Game.TILES_SIZE - Game.TILES_SIZE / 4) {
                ready = true;
            }
        }
    }

    public static ArrayList<FireFlower> getFireFlowers() {
        return FireFlowerList;
    }

    public void update() {
        updatePosition();
        updateAnimationTick();
    }
}
