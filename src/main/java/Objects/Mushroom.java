package Objects;

import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static Utilities.Constants.Directions.RIGHT;

public class Mushroom extends GameObject {
    public static ArrayList<Mushroom> MushroomList = new ArrayList<>();
    private int counter;
    public boolean ready = false;

    public Mushroom(int x, int y, int objType) {
        super(x, y, objType);
        doAnimation = false;
        hitbox = new Rectangle2D.Float();
        hitbox.width = Game.TILES_SIZE-6;
        hitbox.height = Game.TILES_SIZE;
    }

    private void updatePosition() {
        if (inAir) {
            y += airSpeed;
            airSpeed += gravity;
        }
        hitbox.x = x;
        hitbox.y = y;
        if (ready) {
            if (direction == RIGHT) {
                x += 1;
            } else {
                x -= 1;
            }
        } else {
            y--;
            counter++;
            if (counter == Game.TILES_SIZE-4) {
                ready = true;
            }
        }
    }

    public static ArrayList<Mushroom> getMushrooms() {
        return MushroomList;
    }

    public void update() {
        updatePosition();
        checkCollisions();
        updateAnimationTick();
    }
}
