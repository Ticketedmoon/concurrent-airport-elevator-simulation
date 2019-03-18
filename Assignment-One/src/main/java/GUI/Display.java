package main.java.GUI;

import javax.swing.*;

public class Display {

    public Display() {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(30, 30, 300, 300);
        window.getContentPane().add(new DrawPerson());
        window.setVisible(true);
    }
}
