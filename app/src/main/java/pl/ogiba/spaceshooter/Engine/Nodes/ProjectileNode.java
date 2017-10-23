package pl.ogiba.spaceshooter.Engine.Nodes;

import android.graphics.Color;
import android.graphics.RectF;

import pl.ogiba.spaceshooter.Engine.Utils.PaintNode;
import pl.ogiba.spaceshooter.Engine.Utils.Vector2;

/**
 * Created by robertogiba on 24.10.2017.
 */

public class ProjectileNode extends PaintNode {
    public static final float DEFAULT_SPEED = 5f;
    private static final float BASE_PROJECTILE_THICKNESS = 10f;
    private static final float BASE_PROJECTILE_LENGTH = 100f;

    private RectF rect = new RectF();
    private float speed = DEFAULT_SPEED;
    private float originXPos;
    private float originYPos;

    public ProjectileNode(float originXPos, float originYPos){
        super(Color.YELLOW);

        this.originXPos = originXPos + BASE_PROJECTILE_THICKNESS;
        this.originYPos = originYPos - BASE_PROJECTILE_LENGTH;
        this.currentVector = new Vector2(0.0f, -0.1f).normalize();
        generateNewPosition();
    }

    public void updatePosition(double ratio) {
        float speedWithRatio = speed * (float) ratio;
        float diffX = speedWithRatio * currentVector.x;
        float diffY = speedWithRatio * currentVector.y;
        updateRect(rect, diffX, diffY);
    }

    private void generateNewPosition() {
        rect.set(originXPos,
                originYPos,
                originXPos + BASE_PROJECTILE_THICKNESS,
                originYPos + BASE_PROJECTILE_LENGTH);
    }

    private void updateRect(RectF rect, float diffX, float diffY) {
        float nextX = rect.left + diffX;
        float nextY = rect.top + diffY;
        rect.set(nextX, nextY, nextX + rect.width(), nextY + rect.height());
    }

    public RectF getRect() {
        return rect;
    }
}
