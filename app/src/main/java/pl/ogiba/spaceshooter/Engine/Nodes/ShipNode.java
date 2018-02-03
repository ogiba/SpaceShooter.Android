package pl.ogiba.spaceshooter.Engine.Nodes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.Log;

import pl.ogiba.spaceshooter.Engine.Physics.World;
import pl.ogiba.spaceshooter.Engine.Utils.BaseNode;
import pl.ogiba.spaceshooter.Engine.Utils.Vector2;

/**
 * Created by robertogiba on 23.10.2017.
 */

public class ShipNode extends BaseNode {
    public static final float SHIP_RADIUS = 80f;
    public static final float DEFAULT_SPEED = 0f;

    private float speed = DEFAULT_SPEED;

    private float targetX;
    private float targetY;

    @Nullable
    private Bitmap shipBitmap;

    public ShipNode(World world) {
        super(world);

        body.setRect(new RectF(0, 0, SHIP_RADIUS, SHIP_RADIUS));
    }

    public void setDefaultPosition(float viewWidth, float viewHeight) {
        this.pitchWidth = viewWidth;
        this.pitchHeight = viewHeight;

        body.setPosition(new Vector2(viewWidth / 2f, viewHeight * 5f / 6f));
    }

    public void setDefaultSpeed() {
        speed = DEFAULT_SPEED;
    }


    public void updatePosition(double ratio) {
        float currentX = body.getPosition().x;
        float currentY = body.getPosition().y;

        float speedWithRatio = speed * (float) ratio;

        final float newX;

        float properMovementWidth = pitchWidth - (SHIP_RADIUS / 2);

        if (targetX <= properMovementWidth && targetX >= 0)
            newX = currentX + (targetX - currentX) * speed;
        else if (targetX >= properMovementWidth)
            newX = currentX + (properMovementWidth - currentX) * speed;
        else
            newX = currentX + (currentX - (SHIP_RADIUS / 2)) * speed;
        Log.d("CURRENT_X", "" + currentX);


        final float newY;
        if (targetY <= pitchHeight && targetY >= pitchHeight / 3)
            newY = currentY - (currentY - targetY) * speed;
        else if (targetY < pitchHeight / 3)
            newY = currentY - (currentY - (pitchHeight / 3)) * speed;
        else
            newY = currentY + (pitchHeight - currentY - (SHIP_RADIUS / 2)) * speed;
        Log.d("CURRENT_Y", "" + currentY);

        body.setPosition(new Vector2(newX, newY));
    }

    @Override
    public void update(float ratio) {
        updatePosition(ratio);
    }

    @Override
    public void draw(Canvas canvas) {
        if (shipBitmap == null)
            return;

        RectF srcRect = new RectF(0, 0, shipBitmap.getWidth(), shipBitmap.getHeight());
        RectF dstRect = body.getRect();

        Matrix enterTheMatrix = new Matrix();
        enterTheMatrix.setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.CENTER);

        canvas.drawBitmap(shipBitmap, enterTheMatrix, null);
    }

    public void moveToPosition(float newX, float newY) {
        this.speed = 0.1f;
        this.targetX = newX;
        this.targetY = newY;
    }

    public void setShipBitmap(@Nullable Bitmap shipBitmap) {
        this.shipBitmap = shipBitmap;
    }

    public float getCenterX() {
        return body.getPosition().x + SHIP_RADIUS / 2.0f;
    }
}
