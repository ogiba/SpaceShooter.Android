package pl.ogiba.spaceshooter.Engine.Nodes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

import java.util.ArrayList;

import pl.ogiba.spaceshooter.Engine.Physics.World;
import pl.ogiba.spaceshooter.Engine.Utils.PaintNode;
import pl.ogiba.spaceshooter.Engine.Utils.Vector2;

/**
 * Created by robertogiba on 24.10.2017.
 */

public class ProjectileNode extends PaintNode {
    public static final float DEFAULT_SPEED = 5f;
    public static final float BASE_PROJECTILE_THICKNESS = 10f;
    public static final float BASE_PROJECTILE_LENGTH = 100f;

    private float originXPos;
    private float originYPos;

    public ProjectileNode(ShipNode shipNode, World world) {
        super(world, Color.YELLOW);

        this.originXPos = shipNode.getCenterX() - BASE_PROJECTILE_THICKNESS / 2.0f;
        this.originYPos = shipNode.getCurrentPositionY() - BASE_PROJECTILE_LENGTH;

        body.setRect(new RectF(0, 0, BASE_PROJECTILE_THICKNESS, BASE_PROJECTILE_LENGTH));
        body.setVelocity(new Vector2(0.0f, -6f));

        generateNewPosition();
    }

    @Override
    public void update(float ratio) {

    }

    @Override
    public void draw(Canvas canvas) {
//            if (projectile.getCurrentPositionY() >= 0)
        canvas.drawRect(body.getRect(), currentPaint);

    }

    private void generateNewPosition() {
        body.setPosition(new Vector2(originXPos, originYPos));
    }

    private void updateRect(RectF rect, float diffX, float diffY) {
        float nextX = rect.left + diffX;
        float nextY = rect.top + diffY;
        rect.set(nextX, nextY, nextX + rect.width(), nextY + rect.height());
    }
}
