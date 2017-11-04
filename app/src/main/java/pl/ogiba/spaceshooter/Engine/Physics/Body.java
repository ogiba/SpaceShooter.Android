package pl.ogiba.spaceshooter.Engine.Physics;

import android.graphics.RectF;
import android.util.Size;

import java.util.ArrayList;
import java.util.Objects;

import pl.ogiba.spaceshooter.Engine.Utils.Vector2;

/**
 * Created by robertogiba on 28.10.2017.
 */

public class Body {
    private RectF rect;
    private World world;
    private Vector2 velocity;

    private Object data;

    private boolean isDestroyed;
    private int moveDirectionDeterminant = 1;

    public Body(World world) {
        this.world = world;
        this.isDestroyed = false;
        this.velocity = new Vector2(0f, 0f);
    }

    public void update(float ratio) {
        final Vector2 vectorDiff = Vector2.multiply(velocity, ratio);
        final Size boundaries = world.getBoundaries();

        if (boundaries != null) {
            if (rect.right >= world.getBoundaries().getWidth()) {
                moveDirectionDeterminant = -1;
            } else if (rect.left <= 0) {
                moveDirectionDeterminant = 1;
            }
        }

        this.setPosition(Vector2.add(getPosition(), Vector2.multiplyXAxist(vectorDiff, moveDirectionDeterminant)));
    }

    public void destroy() {
        isDestroyed = true;
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

    public float getHeight() {
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
