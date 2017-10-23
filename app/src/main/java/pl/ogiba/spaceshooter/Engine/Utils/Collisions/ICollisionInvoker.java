package pl.ogiba.spaceshooter.Engine.Utils.Collisions;

import pl.ogiba.spaceshooter.Engine.Utils.Vector2;

/**
 * Created by robertogiba on 23.10.2017.
 */

public interface ICollisionInvoker {
    void updateVector(Vector2 angle);
    Vector2 getCurrentVector();

    void updatePositionDirectly(float newX, float newY);
    void updatePosition(double ratio);
    float getCurrentPositionX();
    float getCurrentPositionY();

    float getCurrentSpeed();
    void setSpeedDirectly(float speed);
    void updateSpeedWithRatio(float ratio);
}
