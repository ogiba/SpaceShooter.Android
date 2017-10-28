package pl.ogiba.spaceshooter.Engine.Nodes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

import pl.ogiba.spaceshooter.Engine.Physics.World;
import pl.ogiba.spaceshooter.Engine.Utils.BaseNode;
import pl.ogiba.spaceshooter.Engine.Utils.Vector2;

/**
 * Created by robertogiba on 24.10.2017.
 */

public class OpponentNode extends BaseNode {
    public static final float OPPONENT_RADIUS = 30f;
    public static final float DEFAULT_SPEED = 2f;

    private float speed = DEFAULT_SPEED;

    private float directionDeterminant = 1;
    @Nullable
    private Bitmap opponentBitmap;

    public OpponentNode(World world) {
        super(world);

        this.body.setRect(new RectF(0, 0, OPPONENT_RADIUS, OPPONENT_RADIUS));
        generateNewPosition();
    }

    private void generateNewPosition() {
        float newXPosition = generateNewXPosition();

        body.setPosition(new Vector2(newXPosition, 0 - body.getHeigth()));
    }

    private float generateNewXPosition() {
        final float randomValue;
        if (pitchWidth > 0) {
            final Random random = new Random();
            final int maxValue = Math.round(pitchWidth);
            randomValue = (float) random.nextInt(maxValue);
        } else {
            randomValue = (float) Math.random();
        }

        return randomValue;
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

    @Override
    public void update(float ratio) {

    }

    @Override
    public void draw(Canvas canvas) {
        if (opponentBitmap == null)
            return;

        final RectF srcRect = new RectF(0, 0, opponentBitmap.getWidth(), opponentBitmap.getHeight());

//            if (opponent.getRect().centerY() <= canvasHeight) {
        Matrix matrix = new Matrix();
        matrix.setRectToRect(srcRect, body.getRect(), Matrix.ScaleToFit.CENTER);

        canvas.drawBitmap(opponentBitmap, matrix, null);
    }

    @Override
    public void setPitchSize(float pitchWidth, float pitchHeight) {
        super.setPitchSize(pitchWidth, pitchHeight);

        generateNewPosition();
    }

    public void setOpponentBitmap(@Nullable Bitmap opponentBitmap) {
        this.opponentBitmap = opponentBitmap;
    }
}
