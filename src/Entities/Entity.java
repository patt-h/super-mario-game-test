package Entities;

import java.awt.geom.Rectangle2D;

public abstract class Entity {
    public static float x, y;
    public static int direction;
    public static Rectangle2D.Float hitbox;

    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
