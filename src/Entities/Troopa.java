package Entities;

import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static Utilities.Constants.Directions.LEFT;
import static Utilities.Constants.Directions.RIGHT;
import static Utilities.Constants.EnemyConstants.TROOPA;

public class Troopa extends Enemy {
    public static ArrayList<Troopa> TroopaList = new ArrayList<>();
    public boolean stepped = false;
    public boolean fireballed = false;
    public boolean kicked = false;

    public int tempImmortal = 40;

    public Troopa(float x, float y) {
        super(x, y);
        enemyType = TROOPA;
        direction = LEFT;
        hitbox = new Rectangle2D.Float();
        damageHitbox = new Rectangle2D.Float();
        hitbox.width = 16 * Game.SCALE;
        hitbox.height = 16 * Game.SCALE;
        damageHitbox.width = 16 * Game.SCALE;
        damageHitbox.height = 2 * Game.SCALE;
    }

    private void updatePosition() {
        if (inAir) {
            y += airSpeed;
            airSpeed += gravity;
        }
        hitbox.x = x;
        hitbox.y = y;
        damageHitbox.x = x;
        damageHitbox.y = y - 2 * Game.SCALE;

        if (!stepped && !fireballed && !kicked) {
            if (direction == RIGHT) {
                x += 1;
            } else {
                x -= 1;
            }
        }
        if (kicked) {
            if (tempImmortal < 40) {
                tempImmortal++;
            }
            if (direction == RIGHT) {
                x += 3;
            } else {
                x -= 3;
            }
        }
        if (fireballed) {
            y -= 5;
            inAir = true;
        }
    }

    public static ArrayList<Troopa> getTroopa() {
        return TroopaList;
    }

    public void update() {
        updatePosition();
        if (!fireballed) {
            checkCollisions();
        }
        updateAnimationTick();
    }
}
