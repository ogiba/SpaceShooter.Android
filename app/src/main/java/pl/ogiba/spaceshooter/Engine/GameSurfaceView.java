package pl.ogiba.spaceshooter.Engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import pl.ogiba.spaceshooter.R;

/**
 * Created by robertogiba on 23.10.2017.
 */

public class GameSurfaceView extends SurfaceView
        implements SurfaceHolder.Callback {

    private GameThread thread;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new GameThread(holder, context);

        setFocusable(true);
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        thread.setShipBitmap(bitmap);
        thread.setOpponentBitmap(bitmap);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
        thread.setState(GameState.READY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return thread.doTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
    }

    public GameThread getThread() {
        return thread;
    }

    public void setGameStateListener(IGameStateHolder listener){
        thread.setRefree(listener);
    }
}
