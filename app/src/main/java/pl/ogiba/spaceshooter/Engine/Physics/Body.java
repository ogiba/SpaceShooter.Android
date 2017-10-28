package pl.ogiba.spaceshooter.Engine.Physics;

import android.graphics.RectF;

import java.util.ArrayList;

import pl.ogiba.spaceshooter.Engine.Utils.Vector2;

/**
 * Created by robertogiba on 28.10.2017.
 */

public class Body {
    private RectF rect;
    private World world;
    private Vector2 velocity;

    private boolean isDestroyed;

    public Body(World world) {
        this.world = world;
        this.isDestroyed = false;
        this.velocity = new Vector2(0f, 0f);
    }

    public void update() {
        this.setPosition(Vector2.add(getPosition(), velocity));
    }

    public void destroy() {
        isDestroyed = true;
    }

    private void updateRect(RectF rect, float diffX, float diffY) {
        float nextX = rect.left + diffX;
        float nextY = rect.top + diffY;
        rect.set(nextX, nextY, nextX + rect.width(), nextY + rect.height());
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public RectF getRect() {
        return rect;
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }

    public Vector2 getPosition() {
        return new Vector2(rect.left, rect.top);
    }

    public float getWidth() {
        return rect.width();
    }

    public float getHeigth() {
        return rect.height();
    }

    public void setPosition(Vector2 point) {
        rect.set(point.x, point.y, point.x + rect.width(), point.y + rect.height());
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
