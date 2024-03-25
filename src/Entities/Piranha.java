package Entities;

import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static Utilities.Constants.Directions.LEFT;
import static Utilities.Constants.EnemyConstants.PIRANHA;

public class Piranha extends Enemy {
    public static ArrayList<Piranha> PiranhaList = new ArrayList<>();
    public boolean fireballed = false;
    private boolean movingUp = true; // Flaga wskazująca kierunek ruchu (góra/dół)
    private long lastDirectionChangeTime; // Czas ostatniej zmiany kierunku
    private int counterUp;
    private int counterDown;
    public boolean isPlayerNearby;
    private final int WAIT_TIME = 1000; // Czas oczekiwania w milisekundach

    public Piranha(float x, float y) {
        super(x, y);
        enemyType = PIRANHA;
        direction = LEFT;
        hitbox = new Rectangle2D.Float();
        damageHitbox = new Rectangle2D.Float();
        hitbox.width = 16 * Game.SCALE;
        hitbox.height = 16 * Game.SCALE;
        damageHitbox.width = 16 * Game.SCALE;
        damageHitbox.height = 16 * Game.SCALE;
    }

    private void updatePosition() {
        if (!fireballed) {
            hitbox.x = x;
            hitbox.y = y;
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastDirectionChangeTime;

            if (elapsedTime >= WAIT_TIME) {
                if (movingUp) {
                    y -= 1;
                    counterUp++;
                    if (counterUp >= 71) {
                        movingUp = false;
                        lastDirectionChangeTime = currentTime;
                    }
                } else {
                    if (counterDown < 71) {
                        y += 1;
                    }
                    counterDown++;
                    if (counterDown >= 71 && !isPlayerNearby) {
                        movingUp = true;
                        lastDirectionChangeTime = currentTime;
                        counterUp = 0;
                        counterDown = 0;
                    }
                }
            }
        }
        damageHitbox.x = x;
        damageHitbox.y = y;
    }

    public void setPlayerNearby(boolean playerNearby) {
        isPlayerNearby = playerNearby;
    }

    public static ArrayList<Piranha> getPiranha() {
        return PiranhaList;
    }

    public void update() {
        updatePosition();
        if (!fireballed) {
            checkCollisions();
        }
        updateAnimationTick();
    }

}
