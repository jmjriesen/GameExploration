package TurtlesEngine;

import sun.plugin.dom.exception.InvalidStateException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TurtleCanvas extends JPanel {
    static private final ArrayList<Turtle> population = new ArrayList<>();

    private static boolean windowSetup = false;



    // Override paintComponent to perform your own painting
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);     // paint parent's background
        setBackground(Color.BLACK);  // set background color for this JPanel

        synchronized (population) {
            for (Turtle turtle : population) {
                turtle.paint((Graphics2D) g);
                turtle.update();
            }
        }
    }
     static void addResident(Turtle turtle){
        synchronized (population){
            population.add(turtle);
        }
    }


    public static void WindowSetup() {
        if (windowSetup) {
            throw new InvalidStateException("Window already set up");
        }
        windowSetup = true;
        // Create frame with title Registration Demo
        JFrame frame = new JFrame();
        frame.setTitle("TurtlesEngine.Turtle styal graphics");

        // Panel to define the layout. We are using GridBagLayout
        JPanel mainPanel = new TurtleCanvas();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        frame.add(mainPanel);
        frame.pack();
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);


        Thread thread = new Thread(() -> {
            while (true) {
                frame.repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }
}
