package main.java;

import java.awt.*;

public interface Drawable {
    public Rectangle getBounds();
    public void setBounds(Rectangle bounds);
    public Color getColor();
    public void setColor(Color color);
    public void draw(Graphics2D g2d);
    public String getTitle();
}
