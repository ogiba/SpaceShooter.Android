package pl.ogiba.spaceshooter.Engine.Physics;

import pl.ogiba.spaceshooter.Engine.Nodes.OpponentNode;

/**
 * Created by robertogiba on 25.10.2017.
 */

public interface OnCollisionListener {
    void onCollision(Body source, Body dest);
}
