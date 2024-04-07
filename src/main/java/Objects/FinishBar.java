package Objects;

import Actions.FinishLevel;
import Entities.Player;
import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


public class FinishBar extends GameObject {
    public static ArrayList<FinishBar> FinishBarList = new ArrayList<>();
    private boolean goingUp = true;
    private boolean goingDown = false;
    public static boolean GotTaken = false;
    public boolean finishSoundPlayed = false;
    private int counter = 0;

    public FinishBar (int x, int y, int objType) {
        super(x, y, objType);
        width = 66;
        height = 24;
        doAnimation = false;
        hitbox = new Rectangle2D.Float();
        hitbox.width = 22 * Game.SCALE;
        hitbox.height = 8 * Game.SCALE;
        loadFrames(1, 44, "CrossingBar.png");
    }

    public void update() {
        updatePosition();
    }

    private void updatePosition() {
        hitbox.x = x;
        hitbox.y = y;
        if (goingUp) {
            counter += 1;
            y -= 2;
            if (counter == 180) {
                counter = 0;
                goingUp = false;
                goingDown = true;
            }
        }
        else if (goingDown) {
            counter += 1;
            y += 2;
            if (counter == 180) {
                counter = 0;
                goingDown = false;
                goingUp = true;
            }
        }
    }

    public void checkTouched(Player player) {
        if (player.hitbox.intersects(hitbox)) {
            FinishLevel.playFinishSound(player);
            FinishLevel.startTime = System.nanoTime();
            setActive(false);
            GotTaken = true;
        }
    }

    public static ArrayList<FinishBar> getFinishBarList() {
        return FinishBarList;
    }
}
