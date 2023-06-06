package Objects;

import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class FireFlower extends GameObject {
    public static ArrayList<FireFlower> FireFlowerList = new ArrayList<>();
    private int counter;
    private boolean ready = false;

    public FireFlower(int x, int y, int objType) {
        super(x, y, objType);
        doAnimation = true;
        hitbox = new Rectangle2D.Float();
        hitbox.width = Game.TILES_SIZE;
        hitbox.height = Game.TILES_SIZE;
    }

    private void updatePosition() {
        hitbox.x = x;
        hitbox.y = y;
        if (!ready) {
            y--;
            counter++;
            if (counter == Game.TILES_SIZE-4) {
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
