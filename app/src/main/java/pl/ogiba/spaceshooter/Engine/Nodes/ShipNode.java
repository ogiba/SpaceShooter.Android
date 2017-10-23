package pl.ogiba.spaceshooter.Engine.Nodes;

import android.util.Log;

import pl.ogiba.spaceshooter.Engine.Utils.BaseNode;
import pl.ogiba.spaceshooter.Engine.Utils.Collisions.ICollisionInvoker;
import pl.ogiba.spaceshooter.Engine.Utils.Vector2;

/**
 * Created by robertogiba on 23.10.2017.
 */

public class ShipNode extends BaseNode implements ICollisionInvoker {
    public static final float SHIP_RADIUS = 30f;
    public static final float DEFAULT_SPEED = 0f;

    private float speed = DEFAULT_SPEED;
    private float viewWidth;
    private float viewHeight;

    private float targetX;
    private float targetY;

    public ShipNode() {
        currentVector = new Vector2(0.1f, 0.0f).normalize();
    }

    public void setDefaultPosition(float viewWidth, float viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        currentX = viewWidth / 2f;
        currentY = viewHeight * 5f / 6f;
    }

    public void setDefaultSpeed() {
        speed = DEFAULT_SPEED;
    }

    @Override
    public void updateVector(Vector2 angle) {
        this.currentVector = angle;
    }

    @Override
    public void updatePositionDirectly(float newX, float newY) {
        this.currentX = newX;
        this.currentY = newY;
    }

    @Override
    public void updatePosition(double ratio) {
        float speedWithRatio = speed * (float) ratio;

//        currentX = currentX + speedWithRatio * currentVector.x;
        final float newX = currentX + (targetX - currentX) * speed;
        if (newX <= viewWidth && newX >= 0)
            currentX = newX;
        Log.d("CURRENT_X", "" + currentX);
//        currentY = currentY + speedWithRatio * currentVector.y;
        final float newY = currentY - (currentY - targetY) * speed;
        if (newY <= viewHeight && newY >= viewHeight / 3)
            currentY = newY;
        Log.d("CURRENT_Y", "" + currentY);

        currentVector.x += ratio;
        currentVector.normalize();
    }

    @Override
    public float getCurrentPositionX() {
        return currentX;
    }

    @Override
    public float getCurrentPositionY() {
        return currentY;
    }

    @Override
    public float getCurrentSpeed() {
        return speed;
    }

    @Override
    public void setSpeedDirectly(float speed) {
        this.speed = speed;
    }

    @Override
    public void updateSpeedWithRatio(float ratio) {
        this.speed = speed * ratio;
    }

    public void moveToPosition(float newX, float newY) {
        this.speed = 0.1f;
        this.targetX = newX;
        this.targetY = newY;
    }
}
