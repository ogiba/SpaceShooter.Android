package pl.ogiba.spaceshooter.Engine.Nodes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Random;

import pl.ogiba.spaceshooter.Engine.Physics.Body;
import pl.ogiba.spaceshooter.Engine.Physics.World;
import pl.ogiba.spaceshooter.Engine.Utils.BaseNode;
import pl.ogiba.spaceshooter.Engine.Utils.Vector2;

/**
 * Created by robertogiba on 24.10.2017.
 */

public class OpponentNode extends BaseNode {
    public static final float OPPONENT_RADIUS = 80f;
    public static final float DEFAULT_SPEED = 4f;
    public static final float DEFAULT_Y_SPEED = 2f;
    private static final String TAG = "OpponentNode";

    private float speed = DEFAULT_SPEED;

    private float directionDeterminant = 1;
    @Nullable
    private Bitmap opponentBitmap;

    public OpponentNode(World world) {
        super(world);

        this.body.setRect(new RectF(0, 0, OPPONENT_RADIUS, OPPONENT_RADIUS));
        this.body.setVelocity(new Vector2(speed, DEFAULT_Y_SPEED));
    }

    private void generateNewPosition(World world) {
        float newXPosition = generateNewXPosition();

        while (!validateNewPosition(world, newXPosition)) {
            newXPosition = generateNewXPosition();
        }

        body.setPosition(new Vector2(newXPosition, 0 - body.getHeight()));
    }

    private float generateNewXPosition() {
        final float randomValue;
        if (pitchWidth > 0) {
            final Random random = new Random();
            final int maxValue = Math.round(pitchWidth - OPPONENT_RADIUS);
            randomValue = (float) random.nextInt(maxValue);
        } else {
            randomValue = (float) Math.random();
        }

        return randomValue;
    }

    private boolean validateNewPosition(World world, float newPosition) {
        for (Body item : world.getItems()) {
            if (item.getData() instanceof OpponentNode) {
                if (item.getRect().left < newPosition
                        && item.getRect().right > newPosition) {
                    Log.d(TAG, "validateNewPosition: Invalid");
                    return false;
                } else if (item.getRect().left > newPosition
                        && item.getRect().left < newPosition + OPPONENT_RADIUS
                        && item.getRect().right > newPosition + OPPONENT_RADIUS) {
                    Log.d(TAG, "validateNewPosition: Invalid");
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void update(float ratio) {

    }

    @Override
    public void draw(Canvas canvas) {
        if (opponentBitmap == null)
            return;

        final RectF srcRect = new RectF(0, 0, opponentBitmap.getWidth(), opponentBitmap.getHeight());

        Matrix matrix = new Matrix();
        matrix.setRectToRect(srcRect, body.getRect(), Matrix.ScaleToFit.CENTER);

        canvas.drawBitmap(opponentBitmap, matrix, null);
    }

    @Override
    public void setPitchSize(float pitchWidth, float pitchHeight) {
        super.setPitchSize(pitchWidth, pitchHeight);
    }

    public void locate(World world) {
        generateNewPosition(world);
    }

    public void increaseSpeed(float speed) {
        this.speed = speed;
        this.body.setVelocity(new Vector2(speed, DEFAULT_Y_SPEED));
    }

    public void setOpponentBitmap(@Nullable Bitmap opponentBitmap) {
        this.opponentBitmap = opponentBitmap;
    }
}
