package pl.ogiba.spaceshooter.Engine.Physics;

import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.Size;

import java.util.ArrayList;

/**
 * Created by robertogiba on 28.10.2017.
 */

public class World {
    private static final String TAG = "World";
    private ArrayList<Body> items;

    @Nullable
    private OnCollisionListener collisionCallback;

    @Nullable
    private OnWorldBehaviorListener worldBehaviorCallback;

    private Size boundaries;

    public World() {
        this.items = new ArrayList<>();
    }

    public void update(float ratio) {
        for (int i = 0; i < items.size(); i++) {
            final Body item = items.get(i);
            item.update(ratio);

            checkCollisionsWithItems(i);

            checkItemPositionInWorld(item);

            if (item.isDestroyed()) {
                items.remove(i);
                i--;
            }
        }
    }

    public void checkCollisionsWithItems(int position) {
        final Body item1 = items.get(position);
        for (int j = 0; j < items.size(); j++) {
            if (j == position)
                continue;

            final Body item2 = items.get(j);

            if (RectF.intersects(item1.getRect(), item2.getRect()))
                invokeCollisionCallback(item1, item2);
        }
    }

    private void invokeCollisionCallback(Body source, Body dest) {
        if (collisionCallback == null)
            return;

        collisionCallback.onCollision(source, dest);
    }

    private void checkItemPositionInWorld(Body item) {
        if (worldBehaviorCallback == null)
            return;

        if (item.getRect().right >= boundaries.getWidth())
            worldBehaviorCallback.onReachedEdge(item, WorldEdges.RIGHT);
        else if (item.getRect().left <= 0)
            worldBehaviorCallback.onReachedEdge(item, WorldEdges.LEFT);
        else if (item.getRect().top <= -60)
            worldBehaviorCallback.onReachedEdge(item, WorldEdges.TOP);
        else if (item.getRect().bottom >= boundaries.getHeight())
            worldBehaviorCallback.onReachedEdge(item, WorldEdges.BOTTOM);

    }

    public Body createBody() {
        final Body body = new Body(this);
        items.add(body);
        return body;
    }

    public ArrayList<Body> getItems() {
        return items;
    }

    public void setCollisionListener(@Nullable OnCollisionListener collisionListener) {
        this.collisionCallback = collisionListener;
    }

    public void setWorldBehaviorListener(@Nullable OnWorldBehaviorListener worldBehaviorListener) {
        this.worldBehaviorCallback = worldBehaviorListener;
    }

    public void setBoundaries(Size boundaries) {
        this.boundaries = boundaries;
    }

    public Size getBoundaries() {
        return boundaries;
    }
}
