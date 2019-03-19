package main.java;

import java.awt.*;

public abstract class AbstractDrawable {
    private Rectangle bounds;
    private Color color;

    public AbstractDrawable(Rectangle bounds, Color color) {
        setBounds(bounds);
        setColor(color);
    }

    public Rectangle getBounds() {
        return bounds;
    }
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public abstract void draw(Graphics2D g2d);
}
