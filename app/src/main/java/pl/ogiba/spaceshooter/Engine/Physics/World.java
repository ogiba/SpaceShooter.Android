package pl.ogiba.spaceshooter.Engine.Physics;

import android.support.annotation.Nullable;
import android.util.Size;

import java.util.ArrayList;

/**
 * Created by robertogiba on 28.10.2017.
 */

public class World {
    private ArrayList<Body> items;
    @Nullable
    private OnCollisionListener collisionCallback;
    private Size boundaries;

    public World() {
        this.items = new ArrayList<>();
    }

    public void update(float ratio) {
        for (int i = 0; i < items.size(); i++) {
            final Body item = items.get(i);
            item.update(ratio);

            checkCollisionsWithItems(i);
//
//            if (item.isDestroyed()) {
//                items.remove(i);
//                i--;
//            }
        }
    }

    public void checkCollisionsWithItems(int position) {
        final Body item1 = items.get(position);
        for (int j = position + 1; j < items.size(); j++) {
            final Body item2 = items.get(j);

            if (item2.getRect().intersect(item1.getRect()))
                invokeCollisionCallback(item1, item2);
        }
    }

    private void invokeCollisionCallback(Body source, Body dest) {
        if (collisionCallback == null)
            return;

        collisionCallback.onCollision(source, dest);
    }

    public Body createBody() {
        final Body body = new Body(this);
        items.add(body);
        return body;
    }

    public ArrayList<Body> getItems() {
        return items;
    }

    public void setCollisionCallback(@Nullable OnCollisionListener collisionCallback) {
        this.collisionCallback = collisionCallback;
    }

    public void setBoundaries(Size boundaries) {
        this.boundaries = boundaries;
    }

    public Size getBoundaries() {
        return boundaries;
    }
}
