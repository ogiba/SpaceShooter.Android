package pl.ogiba.spaceshooter.Engine.Physics;

import android.graphics.RectF;

/**
 * Created by robertogiba on 04.11.2017.
 */

public interface OnWorldBehaviorListener {

    void onReachedEdge(Body item, WorldEdges edge);
}
