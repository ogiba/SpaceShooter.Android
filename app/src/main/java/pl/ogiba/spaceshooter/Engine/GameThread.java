package pl.ogiba.spaceshooter.Engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;

import pl.ogiba.spaceshooter.Engine.Nodes.ProjectileNode;
import pl.ogiba.spaceshooter.Engine.Nodes.ShipNode;

/**
 * Created by robertogiba on 23.10.2017.
 */

public class GameThread extends Thread {
    private static final String TAG = "GameThread";

    private int canvasWidth = 1;
    private int canvasHeight = 1;

    private long lastTime;

    private GameState mode;

    private boolean run = false;
    private final Object runLock = new Object();

    final private SurfaceHolder surfaceHolder;
    final private Context context;

    private IGameStateHolder gameState;

    private ShipNode shipNode;
    private ArrayList<ProjectileNode> projectiles;

    private Bitmap shipBitmap;

    public GameThread(SurfaceHolder surfaceHolder, Context context) {
        this.surfaceHolder = surfaceHolder;
        this.context = context;

        this.shipNode = new ShipNode();
        this.projectiles = new ArrayList<>();
    }

    public boolean isGameInStateReady() {
        return mode == GameState.READY;
    }

    public boolean isGameInStateRunning() {
        return mode == GameState.RUNNING;
    }

    public boolean isGameInStatePaused() {
        return mode == GameState.PAUSE;
    }

    public void doStart() {
        synchronized (surfaceHolder) {
            lastTime = System.currentTimeMillis() + 100;
            setState(GameState.RUNNING);
        }
    }

    public void pause() {
        synchronized (surfaceHolder) {
            if (mode == GameState.RUNNING)
                setState(GameState.PAUSE);
        }
    }

    public void unpasue() {
        synchronized (surfaceHolder) {
            lastTime = System.currentTimeMillis() + 100;
            setState(GameState.RUNNING);
        }
    }

    public synchronized void restoreState(Bundle savedState) {
        synchronized (surfaceHolder) {
            setState(GameState.PAUSE);
        }
    }

    public void gameOver() {
        setState(GameState.PLAYER_FAILED);
        shipNode.setDefaultSpeed();
        gameState.notifyPlayerFailed();
    }

    @Override
    public void run() {
        while (run) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    if (mode == GameState.RUNNING) updatePhysics();

                    synchronized (runLock) {
                        if (run) doDraw(canvas);
                    }
                }
            } finally {
                if (canvas != null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public boolean doTouchEvent(MotionEvent event) {
        boolean handled = true;
        if (mode == GameState.RUNNING) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    Log.d(TAG, "ACTION_DOWN");
                    shoot();
                    break;
                case MotionEvent.ACTION_MOVE:
                    shipNode.moveToPosition(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    Log.d(TAG, "ACTION_UP");
                    shipNode.setDefaultSpeed();
                    break;
            }
        } else {
            handled = false;
        }

        return handled;
    }

    private void shoot() {
        final float xPos = shipNode.getCurrentPositionX() - ShipNode.SHIP_RADIUS / 2.0f;
        final float yPos = shipNode.getCurrentPositionY() - ShipNode.SHIP_RADIUS / 2.0f;

        projectiles.add(new ProjectileNode(xPos, yPos));
    }

    private void doDraw(Canvas canvas) {
        canvas.drawColor(Color.BLUE);
        drawShip(canvas);
        drawProjectiles(canvas);
    }

    private void drawShip(Canvas canvas) {
        if (shipNode == null)
            return;

        RectF srcRect = new RectF(0, 0, shipBitmap.getWidth(), shipBitmap.getHeight());
        RectF dstRect = new RectF((int) (shipNode.getCurrentX() - ShipNode.SHIP_RADIUS),
                (int) (shipNode.getCurrentY() - ShipNode.SHIP_RADIUS),
                (int) (shipNode.getCurrentX() + ShipNode.SHIP_RADIUS),
                (int) (shipNode.getCurrentY() + ShipNode.SHIP_RADIUS));

        Matrix enterTheMatrix = new Matrix();
        enterTheMatrix.setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.CENTER);

        canvas.drawBitmap(shipBitmap, enterTheMatrix, null);
    }

    private void drawProjectiles(Canvas canvas) {
        for (ProjectileNode projectile : new ArrayList<>(projectiles)) {
            if (projectile.getRect().centerY() >= 0)
                canvas.drawRect(projectile.getRect(), projectile.getCurrentPaint());
            else
                projectiles.remove(projectile);
        }
    }

    private void updatePhysics() {
        long now = System.currentTimeMillis();

        if (lastTime > now) return;

        double elapsed = (now - lastTime) / 1000.0;

        double ratio = elapsed / 0.015d;
        shipNode.updatePosition(ratio);
        updateProjectile(ratio);
        this.lastTime = now;
    }

    private void updateProjectile(double ratio) {
        for (ProjectileNode projectile : projectiles) {
            projectile.updatePosition(ratio);
        }
    }

    public void setRunning(boolean isRunning) {
        synchronized (runLock) {
            this.run = isRunning;
        }
    }

    public void setSurfaceSize(int canvasWidth, int canvasHeight) {
        synchronized (surfaceHolder) {
            this.canvasWidth = canvasWidth;
            this.canvasHeight = canvasHeight;

            shipNode.setDefaultPosition(canvasWidth, canvasHeight);
        }
    }

    public void setState(GameState state) {
        synchronized (surfaceHolder) {
            setState(state, null);
        }
    }

    public void setState(GameState state, @Nullable CharSequence message) {
        synchronized (surfaceHolder) {
            this.mode = state;

            if (mode == GameState.PAUSE) {
                gameState.notifyGamePaused();
            }
        }
    }

    public void setRefree(IGameStateHolder refree) {
        this.gameState = refree;
    }

    public void setShipBitmap(Bitmap shipBitmap) {
        this.shipBitmap = shipBitmap;
    }
}
