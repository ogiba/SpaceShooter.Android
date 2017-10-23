package pl.ogiba.spaceshooter.Engine;

import android.content.Context;
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

    private SurfaceHolder surfaceHolder;
    private Context context;

    public GameThread(SurfaceHolder surfaceHolder, Context context){
        this.surfaceHolder = surfaceHolder;
        this.context = context;
    }
}
