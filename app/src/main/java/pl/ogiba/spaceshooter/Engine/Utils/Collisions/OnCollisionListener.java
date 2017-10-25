package pl.ogiba.spaceshooter.Engine.Utils.Collisions;

import pl.ogiba.spaceshooter.Engine.Nodes.OpponentNode;

/**
 * Created by robertogiba on 25.10.2017.
 */

public interface OnCollisionListener {
    void onOpponentCollision(OpponentNode node);
}
