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
        int elevatorPosition;

        if (elevatorA.getCurrentFloor() == 10)
            elevatorPosition = (8 * 55) + 40;
        else if (elevatorA.getCurrentFloor() != 0 && elevatorA.getCurrentFloor() != 1)
            elevatorPosition = (elevatorA.getCurrentFloor()-1) * 55;
        else
            elevatorPosition = 0;

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
                d.setBounds(new Rectangle(getPreferredSize().width-300, getPreferredSize().height-100-elevatorPosition, getPreferredSize().width/15, getPreferredSize().height/8));
                d.draw(g2d);
            }
        }

        // draw text - floors
        g2d.drawString("Floor 10", 50, 45);
        g2d.drawString("Floor 9", 50, 100);

        g2d.drawString("Floor 8", 50, 160);
        g2d.drawString("Floor 7", 50, 215);

        g2d.drawString("Floor 6", 50, 275);
        g2d.drawString("Floor 5", 50, 335);

        g2d.drawString("Floor 4", 50, 390);
        g2d.drawString("Floor 3", 50, 450);

        g2d.drawString("Floor 2", 50, 505);
        g2d.drawString("Floor 1", 50, 565);

        // draw text - text-box
        g2d.setColor(Color.blue);
        g2d.drawString("Information Area: " + elevatorA.getElevatorID(), 625, 420);

        // Changing fields
        if (elevatorA.getCurrentFloor() == 0 || elevatorA.getCurrentFloor() == 1)
            g2d.drawString("Current Elevator Floor: " + 1, 610, 440);
        else
            g2d.drawString("Current Elevator Floor: " + elevatorA.getCurrentFloor(), 610, 440);

        // Current Passengers
        g2d.drawString("Current Passengers: ", 585, 470);
        int y_counter = 490;
        for(Person person : elevatorA.getCurrentPassengers()) {
            g2d.drawString(person.toString(), 590, y_counter);
            y_counter += 15;
        }

        // Current Elevator Weight
        g2d.drawString("Weight: " + elevatorA.getCurrentElevatorWeight(), 705, 470);

    }
}
