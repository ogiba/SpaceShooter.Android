package pl.ogiba.spaceshooter.Engine.Utils;

import android.graphics.Color;
import android.graphics.Paint;

import pl.ogiba.spaceshooter.Engine.Physics.World;

/**
 * Created by robertogiba on 23.10.2017.
 */

public abstract class PaintNode extends BaseNode {
    protected Paint currentPaint;

    public PaintNode(World world) {
        this(world ,Color.BLACK);
    }

    public PaintNode(World world, int color) {
        super(world);
        currentPaint = new Paint();
        currentPaint.setColor(color);
    }

    public Paint getCurrentPaint() {
        return currentPaint;
    }

    public void setCurrentPaint(Paint currentPaint) {
        this.currentPaint = currentPaint;
    }
}

