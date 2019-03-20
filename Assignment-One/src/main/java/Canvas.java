package main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

public class Canvas extends JComponent {

    private List<Drawable> drawables;
    private Elevator elevatorA;
    private Airport airport;
    private JFrame frame;

    public Canvas(Airport airport, JFrame frame) {
        this.drawables = new LinkedList<>();
        this.airport = airport;
        this.elevatorA = airport.getElevatorA();
        this.frame = frame;
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public void add(Drawable drawable) {
        drawables.add(drawable);
        repaint();
    }

    public void remove(Drawable drawable) {
        drawables.remove(drawable);
        repaint();
    }

    public int getDrawableCount() {
        return drawables.size();
    }

    public Drawable getDrawableAt(int index) {
        return drawables.get(index);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g.create();
        int elevatorPosition = elevatorA.getCurrentFloor() * 38;
        drawObjectsToScreen(g2d, elevatorPosition);

        g2d.dispose();
        repaintAndWait();
    }

    private void repaintAndWait() {
        try {
            if (!airport.isServiceFinished()) {
                repaint(1000);
            }
            else {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawObjectsToScreen(Graphics2D g2d, int elevatorPosition) {
        for (Drawable d : drawables) {
            d.draw(g2d);
            // Elevator
            if(d.getTitle().equals("elevator")) {
                d.setBounds(new Rectangle(getPreferredSize().width-100, getPreferredSize().height-100-elevatorPosition, getPreferredSize().width/15, getPreferredSize().height/8));
                d.draw(g2d);
            }
        }
    }
}
