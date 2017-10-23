package pl.ogiba.spaceshooter.Engine;

/**
 * Created by robertogiba on 23.10.2017.
 */

public interface IGameStateHolder {
    void notifyPlayerFailed();

    void notifyGamePaused();
}
