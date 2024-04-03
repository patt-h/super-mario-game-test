package Entities;

import com.company.Game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static Utilities.Constants.Directions.LEFT;
import static Utilities.Constants.Directions.RIGHT;
import static Utilities.Constants.EnemyConstants.GOOMBA;

public class Goomba extends Enemy {
    public static ArrayList<Goomba> GoombaList = new ArrayList<>();
    public boolean stepped = false;
    public boolean fireballed = false;

    public Goomba(float x, float y) {
        super(x, y);
        width = 120;
        height = 120;
        enemyType = GOOMBA;
        direction = LEFT;
        hitbox = new Rectangle2D.Float();
        damageHitbox = new Rectangle2D.Float();
        hitbox.width = 16 * Game.SCALE;
        hitbox.height = 16 * Game.SCALE;
        damageHitbox.width = 16 * Game.SCALE;
        damageHitbox.height = 2 * Game.SCALE;
    }

    private void updatePosition()  {
        if (inAir) {
            y += airSpeed;
            airSpeed += gravity;
        }
        if (!killed && !killedByShell) {
            hitbox.x = x;
            hitbox.y = y;
        }
        damageHitbox.x = x;
        damageHitbox.y = y - 2 * Game.SCALE;

        if (!stepped) {
            if (direction == RIGHT) {
                x += 1;
            } else {
                x -= 1;
            }
        }
        if (fireballed) {
            y -= 5;
            inAir = true;
        }
    }

    public void update() {
        updatePosition();
        if (!fireballed) {
            checkCollisions();
        }
        updateAnimationTick();
    }

    public static ArrayList<Goomba> getGoomba() {
        return GoombaList;
    }
}
