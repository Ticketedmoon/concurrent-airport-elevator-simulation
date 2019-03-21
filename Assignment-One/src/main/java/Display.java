package main.java;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
                frame.setResizable(true);
                frame.setLayout(new GridBagLayout());

                try {
                    frame.add(new TestPane());
                    frame.pack();

                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class TestPane extends JPanel {

        private Canvas canvas;

        public TestPane() {
            BorderLayout layout = new BorderLayout();
            setLayout(layout);
            canvas = new Canvas(airport, frame);
            canvas.setBackground(Color.black);
            add(canvas);

            Dimension canvasDetails = canvas.getPreferredSize();

            // border
            canvas.add(new DrawableRectangle(new Rectangle(0, 0, canvasDetails.width, canvasDetails.height), Color.LIGHT_GRAY, "background"));

            // Background
            canvas.add(new DrawableRectangle(new Rectangle(10, 10, canvasDetails.width-20, canvasDetails.height-20), Color.gray, "black Structure"));

            // 10-Floors
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-60, canvasDetails.width-350, canvasDetails.height/16), Color.white, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-118, canvasDetails.width-350, canvasDetails.height/16), Color.white, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-176, canvasDetails.width-350, canvasDetails.height/16), Color.white, "floor"));

            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-234, canvasDetails.width-350, canvasDetails.height/16), Color.white, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-292, canvasDetails.width-350, canvasDetails.height/16), Color.white, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-350, canvasDetails.width-350, canvasDetails.height/16), Color.white, "floor"));

            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-408, canvasDetails.width-350, canvasDetails.height/16), Color.white, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-466, canvasDetails.width-350, canvasDetails.height/16), Color.white, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-522, canvasDetails.width-350, canvasDetails.height/16), Color.white, "floor"));
            canvas.add(new DrawableRectangle(new Rectangle(25, canvasDetails.height-580, canvasDetails.width-350, canvasDetails.height/16), Color.white, "floor"));

            // text-box
            DrawableRectangle textBox = new DrawableRectangle(new Rectangle(565, canvasDetails.height-200, 220, 175), Color.lightGray, "textBox");
            canvas.add(textBox);

            // elevator
            DrawableRectangle elevator = new DrawableRectangle(new Rectangle(0, 0, canvasDetails.width, canvasDetails.height/5), Color.red, "elevator");
            canvas.add(elevator);

        }

    }
}
