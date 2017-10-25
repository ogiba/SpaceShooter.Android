package pl.ogiba.spaceshooter.Engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;

import pl.ogiba.spaceshooter.Engine.Nodes.OpponentNode;
import pl.ogiba.spaceshooter.Engine.Nodes.ProjectileNode;
import pl.ogiba.spaceshooter.Engine.Nodes.ShipNode;
import pl.ogiba.spaceshooter.Engine.Utils.Collisions.OnCollisionListener;

/**
 * Created by robertogiba on 23.10.2017.
 */

public class GameThread extends Thread implements OnCollisionListener {
    private static final String TAG = "GameThread";

    private int canvasWidth = 1;
    private int canvasHeight = 1;

    private long lastTime;
    private long startGeneratingTime;

    private GameState mode;

    private boolean run = false;
    private final Object runLock = new Object();

    final private SurfaceHolder surfaceHolder;
    final private Context context;

    private IGameStateHolder gameState;

    private ShipNode shipNode;
    private ArrayList<ProjectileNode> projectiles;
    private ArrayList<OpponentNode> opponents;

    private Bitmap shipBitmap;
    private Bitmap opponentBitmap;

    public GameThread(SurfaceHolder surfaceHolder, Context context) {
        this.surfaceHolder = surfaceHolder;
        this.context = context;

        this.shipNode = new ShipNode();
        this.projectiles = new ArrayList<>();
        this.opponents = new ArrayList<>();
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
            startGeneratingTime = System.currentTimeMillis() + 5000;
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

    private void doDraw(Canvas canvas) {
        canvas.drawColor(Color.BLUE);
        drawShip(canvas);
        drawOpponents(canvas);
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

    private void drawOpponents(Canvas canvas) {
        RectF srcRect = new RectF(0, 0, opponentBitmap.getWidth(), opponentBitmap.getHeight());

        for (OpponentNode opponent : new ArrayList<>(opponents)) {
            if (opponent.getRect().centerY() <= canvasHeight) {
                Matrix matrix = new Matrix();
                matrix.setRectToRect(srcRect, opponent.getRect(), Matrix.ScaleToFit.CENTER);

                canvas.drawBitmap(opponentBitmap, matrix, null);
            } else
                opponents.remove(opponent);
        }
    }

    private void updatePhysics() {
        long now = System.currentTimeMillis();

        if (lastTime > now) return;

        double elapsed = (now - lastTime) / 1000.0;

        double ratio = elapsed / 0.015d;
        shipNode.updatePosition(ratio);
        updateOpponents(ratio);
        updateProjectile(ratio);

        generateOpponents();
        this.lastTime = now;
    }

    private void updateProjectile(double ratio) {
        for (ProjectileNode projectile : new ArrayList<>(projectiles)) {
            projectile.updatePosition(ratio);

            if (projectile.checkForCollisions()) {
                projectiles.remove(projectile);
            }
        }
    }

    private void updateOpponents(double ratio) {
        for (OpponentNode opponent : new ArrayList<>(opponents)) {
            opponent.updatePosition(ratio);
        }
    }

    private void shoot() {
        final float xPos = shipNode.getCurrentPositionX() - ShipNode.SHIP_RADIUS / 2.0f;
        final float yPos = shipNode.getCurrentPositionY() - ShipNode.SHIP_RADIUS / 2.0f;

        ProjectileNode projectile = new ProjectileNode(xPos, yPos);
        projectile.addColissionables(opponents);
        projectiles.add(projectile);
    }

    private void generateOpponents() {
        final long now = System.currentTimeMillis();
        if (startGeneratingTime < now) {
            if (opponents.size() < 10) {
                for (int i = 0; i < 2; i++) {
                    OpponentNode opponentNode = new OpponentNode();
                    opponentNode.setCollisionListener(this);
                    opponentNode.setPitchSize(canvasWidth, canvasHeight);
                    opponents.add(opponentNode);
                }
            }

            this.startGeneratingTime = now + 5000;
        }
    }

    @Override
    public void onOpponentCollision(OpponentNode node) {
        opponents.remove(node);
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
            updateOpponentSceneSize(canvasWidth, canvasHeight);
        }
    }

    private void updateOpponentSceneSize(int width, int height) {
        for (OpponentNode opponent : opponents) {
            opponent.setPitchSize(width, height);
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

    public void setOpponentBitmap(Bitmap opponentBitmap) {
        this.opponentBitmap = opponentBitmap;
    }
}
