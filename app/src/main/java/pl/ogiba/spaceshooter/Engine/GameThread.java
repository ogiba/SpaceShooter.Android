package pl.ogiba.spaceshooter.Engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;

import pl.ogiba.spaceshooter.Engine.Nodes.OpponentNode;
import pl.ogiba.spaceshooter.Engine.Nodes.ProjectileNode;
import pl.ogiba.spaceshooter.Engine.Nodes.ShipNode;
import pl.ogiba.spaceshooter.Engine.Physics.Body;
import pl.ogiba.spaceshooter.Engine.Physics.World;
import pl.ogiba.spaceshooter.Engine.Utils.BaseNode;

/**
 * Created by robertogiba on 23.10.2017.
 */

public class GameThread extends Thread {
    private static final String TAG = "GameThread";

    private int canvasWidth = 1;
    private int canvasHeight = 1;
    private int numberOfOpponents = 0;

    private long lastTime;
    private long startGeneratingTime;

    private GameState mode;

    private boolean run = false;
    private final Object runLock = new Object();

    final private SurfaceHolder surfaceHolder;
    final private Context context;
    final private World world;

    private IGameStateHolder gameState;

    private ShipNode shipNode;

    private Bitmap opponentBitmap;

    public GameThread(SurfaceHolder surfaceHolder, Context context) {
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        this.world = new World();

        this.shipNode = new ShipNode(world);
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
                    if (mode == GameState.RUNNING) update();

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

        for (Body worldItem : world.getItems()) {
            final BaseNode node = (BaseNode) worldItem.getData();
            node.draw(canvas);
        }
    }

    private void update() {
        long now = System.currentTimeMillis();

        if (lastTime > now) return;

        double elapsed = (now - lastTime) / 1000.0;

        double ratio = elapsed / 0.015d;

        world.update((float) ratio);
        for (int i = 0; i < world.getItems().size(); i++) {
            final BaseNode node = (BaseNode) world.getItems().get(i).getData();
            node.update((float) ratio);
        }

        generateOpponents();
        this.lastTime = now;
    }

    private void shoot() {
        final float xPos = shipNode.getCurrentPositionX() - ShipNode.SHIP_RADIUS / 2.0f;
        final float yPos = shipNode.getCurrentPositionY() - ShipNode.SHIP_RADIUS / 2.0f;

        new ProjectileNode(shipNode, world);
    }

    private void generateOpponents() {
        final long now = System.currentTimeMillis();
        if (startGeneratingTime < now) {
            if (numberOfOpponents < 10) {
                for (int i = 0; i < 2; i++) {
                    numberOfOpponents++;
                    OpponentNode opponentNode = new OpponentNode(world);
                    opponentNode.setOpponentBitmap(opponentBitmap);
                    opponentNode.setPitchSize(canvasWidth, canvasHeight);
                }
            }

            this.startGeneratingTime = now + 5000;
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
        this.shipNode.setShipBitmap(shipBitmap);
    }

    public void setOpponentBitmap(Bitmap opponentBitmap) {
        this.opponentBitmap = opponentBitmap;
    }
}
