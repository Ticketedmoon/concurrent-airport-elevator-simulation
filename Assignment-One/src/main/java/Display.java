package main.java;

import javax.swing.*;
import java.awt.*;

public class Display {

    private Airport airport;

    JFrame frame = new JFrame("Airport Elevator System GUI");

    public Display(Airport airport) {
        this.airport = airport;
    }

    public void initialize() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setBackground( Color.black );
                frame.setResizable(true);

                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        private Canvas canvas;

        public TestPane() {
            setLayout(new BorderLayout());
            canvas = new Canvas(airport, frame);
            add(canvas);

            Dimension canvasDetails = canvas.getPreferredSize();

            // Background
            canvas.add(new DrawableRectangle(new Rectangle(0, 0, canvasDetails.width, canvasDetails.height), Color.LIGHT_GRAY, "background"));

            // Structure
            canvas.add(new DrawableRectangle(new Rectangle(10, 10, canvasDetails.width-20, canvasDetails.height-20), Color.black, "black Structure"));

            // 10-Floors
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-60, canvasDetails.width-150, canvasDetails.height/15), Color.BLUE, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-120, canvasDetails.width-150, canvasDetails.height/15), Color.BLUE, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-180, canvasDetails.width-150, canvasDetails.height/15), Color.BLUE, "floor"));

            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-240, canvasDetails.width-150, canvasDetails.height/15), Color.BLUE, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-300, canvasDetails.width-150, canvasDetails.height/15), Color.BLUE, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-340, canvasDetails.width-150, canvasDetails.height/15), Color.BLUE, "floor"));

            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-400, canvasDetails.width-150, canvasDetails.height/15), Color.BLUE, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-460, canvasDetails.width-150, canvasDetails.height/15), Color.BLUE, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-520, canvasDetails.width-150, canvasDetails.height/15), Color.BLUE, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-580, canvasDetails.width-150, canvasDetails.height/15), Color.BLUE, "floor"));

            DrawableRectangle elevator = new DrawableRectangle(new Rectangle(canvasDetails.width-100, canvasDetails.height-100, canvasDetails.width/15, canvasDetails.height/8), Color.RED, "elevator");
            canvas.add(elevator);

        }

    }
}
