package pl.ogiba.spaceshooter.Engine;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by robertogiba on 23.10.2017.
 */

public class GameThread extends Thread {
    private int canvasWidth = 1;
    private int canvasHeight = 1;

    private long lastTime;

    private GameState mode;

    private boolean run = false;
    private final Object runLock = new Object();

    final private SurfaceHolder surfaceHolder;
    private Context context;
    private IGameStateHolder gameState;

    public GameThread(SurfaceHolder surfaceHolder, Context context) {
        this.surfaceHolder = surfaceHolder;
        this.context = context;
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
                    break;
            }
        } else {
            handled = false;
        }

        return handled;
    }

    private void doDraw(Canvas canvas) {

    }

    private void updatePhysics() {
        long now = System.currentTimeMillis();

        if (lastTime > now) return;

        double elapsed = (now - lastTime) / 1000.0;

        double ratio = elapsed / 0.015d;

        this.lastTime = now;
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
}
