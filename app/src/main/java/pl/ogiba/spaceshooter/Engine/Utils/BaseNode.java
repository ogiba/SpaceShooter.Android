package pl.ogiba.spaceshooter.Engine.Utils;

import android.graphics.Canvas;

import pl.ogiba.spaceshooter.Engine.Physics.Body;
import pl.ogiba.spaceshooter.Engine.Physics.World;

/**
 * Created by robertogiba on 23.10.2017.
 */

public abstract class BaseNode {
    protected float pitchWidth;
    protected float pitchHeight;

    protected Body body;

    public BaseNode(World world) {
        this.body = world.createBody();
        body.setData(this);
    }

    public float getPitchWidth() {
        return pitchWidth;
    }

    public void setPitchWidth(float pitchWidth) {
        this.pitchWidth = pitchWidth;
    }

    public float getPitchHeight() {
        return pitchHeight;
    }

    public void setPitchHeight(float pitchHeight) {
        this.pitchHeight = pitchHeight;
    }

    public void setPitchSize(float pitchWidth, float pitchHeight) {
        this.pitchWidth = pitchWidth;
        this.pitchHeight = pitchHeight;
    }

    public float getCurrentPositionX() {
        return body.getPosition().x;
    }


    public float getCurrentPositionY() {
        return body.getPosition().y;
    }

    public abstract void update(float ratio);

    public abstract void draw(Canvas canvas);
}

