package main.java;

import java.awt.*;

public class DrawableRectangle extends AbstractDrawable implements Drawable {

    public String title;

    public DrawableRectangle(Rectangle bounds, Color color) {
        super(bounds, color);
    }

    public DrawableRectangle(Rectangle bounds, Color color, String title) {
        super(bounds, color);
        this.title = title;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(getColor());
        g2d.fill(getBounds());

        g2d.setColor(Color.black);
        g2d.draw(getBounds());
    }

    public String getTitle() {
        return title;
    }
}
