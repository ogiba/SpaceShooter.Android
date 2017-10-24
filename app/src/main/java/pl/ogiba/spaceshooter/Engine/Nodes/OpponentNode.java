package pl.ogiba.spaceshooter.Engine.Nodes;

import android.graphics.RectF;

import pl.ogiba.spaceshooter.Engine.Utils.BaseNode;
import pl.ogiba.spaceshooter.Engine.Utils.Collisions.ICollisionInterpreter;
import pl.ogiba.spaceshooter.Engine.Utils.Collisions.ICollisionInvoker;
import pl.ogiba.spaceshooter.Engine.Utils.Vector2;

/**
 * Created by robertogiba on 24.10.2017.
 */

public class OpponentNode extends BaseNode implements ICollisionInterpreter {
    public static final float OPPONENT_RADIUS = 30f;
    public static final float DEFAULT_SPEED = 2f;

    private float speed = DEFAULT_SPEED;

    private RectF rect = new RectF();
    private float directionDeterminant = 1;

    public OpponentNode() {
        currentVector = new Vector2(0.2f, 0.1f).normalize();

        generateNewPosition();
    }

    @Override
    public boolean checkForCollision(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
        return false;
    }

    private void generateNewPosition() {
        float newXPosition = generateNewXPosition();

        rect.set(newXPosition,
                0,
                newXPosition + OPPONENT_RADIUS,
                0 + OPPONENT_RADIUS);
    }

    private float generateNewXPosition() {
        float random = (float) Math.random();
        return OPPONENT_RADIUS * random;
    }

    public void setDefaultSpeed() {
        speed = DEFAULT_SPEED;
    }

    public void updateVector(Vector2 angle) {
        this.currentVector = angle;
    }

    private void updateRect(RectF rect, float diffX, float diffY) {
        if (rect.right >= pitchWidth) {
            directionDeterminant = -1;
        } else if (rect.left <= 0) {
            directionDeterminant = 1;
        }

        float nextX = rect.left + (diffX * directionDeterminant);
        float nextY = rect.top + diffY;
        rect.set(nextX, nextY, nextX + rect.width(), nextY + rect.height());
    }

//    public void updatePosition(double ratio) {
//        float speedWithRatio = speed * (float) ratio;
//
////        currentX = currentX + speedWithRatio * currentVector.x;
//        final float newX = currentX + (targetX - currentX) * speed;
//        if (newX <= pitchWidth && newX >= 0)
//            currentX = newX;
//        Log.d("CURRENT_X", "" + currentX);
////        currentY = currentY + speedWithRatio * currentVector.y;
//        final float newY = currentY - (currentY - targetY) * speed;
//        if (newY <= pitchHeight && newY >= pitchHeight / 3)
//            currentY = newY;
//        Log.d("CURRENT_Y", "" + currentY);
//
//        currentVector.x += ratio;
//        currentVector.normalize();
//    }

    public void updatePosition(double ratio) {
        float speedWithRatio = speed * (float) ratio;
        float diffX = speedWithRatio * currentVector.x;
        float diffY = speedWithRatio * currentVector.y;

        updateRect(rect, diffX, diffY);

//        if (rect.right < 0) {
//            generateNewPosition();
//        }
    }

    public RectF getRect() {
        return rect;
    }
}
