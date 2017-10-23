package pl.ogiba.spaceshooter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import pl.ogiba.spaceshooter.Engine.GameState;
import pl.ogiba.spaceshooter.Engine.GameSurfaceView;
import pl.ogiba.spaceshooter.Engine.GameThread;
import pl.ogiba.spaceshooter.Engine.IGameStateHolder;

public class MainActivity extends AppCompatActivity
        implements IGameStateHolder, View.OnClickListener {

    private View overlay;
    private GameSurfaceView surfaceView;
    private GameThread gameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overlay = findViewById(R.id.overlay);
        overlay.setOnClickListener(this);
        surfaceView = (GameSurfaceView) findViewById(R.id.game_view);
        surfaceView.setGameStateListener(this);
        gameThread = surfaceView.getThread();
    }

    @Override
    protected void onPause() {
        gameThread.pause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (gameThread.isGameInStateRunning()) {
            gameThread.pause();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.overlay:
                if (gameThread.isGameInStateReady()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            overlay.setVisibility(View.INVISIBLE);
                            gameThread.doStart();
                        }
                    });
                } else if (gameThread.isGameInStatePaused()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            overlay.setVisibility(View.INVISIBLE);
                            gameThread.doStart();
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void notifyPlayerFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameThread.setState(GameState.READY);
            }
        });
    }

    @Override
    public void notifyGamePaused() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
