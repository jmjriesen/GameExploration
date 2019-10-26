package TurtlesEngine;

import sun.plugin.dom.exception.InvalidStateException;

import javax.swing.*;
import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.ListIterator;

public class TurtleCanvas extends JPanel {
    static private final ArrayList<WeakReference<Turtle>> population = new ArrayList<>();


    // Override paintComponent to perform your own painting
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);     // paint parent's background
        setBackground(Color.BLACK);  // set background color for this JPanel

        synchronized (population) {
            if (!population.isEmpty()) {
                ListIterator<WeakReference<Turtle>> iterator = population.listIterator();

                WeakReference<Turtle> reference;

                while (iterator.hasNext()) {
                    reference = iterator.next();

                    Turtle turtle = reference.get();
                    if (turtle == null) {
                        iterator.remove();
                    } else {
                        turtle.paint((Graphics2D) g);
                        turtle.update();
                    }
                }
            }
        }
    }

    static void addResident(Turtle turtle) {
        synchronized (population) {
            population.add(new WeakReference<>(turtle));
        }
    }

    private static boolean windowSetupLatch = false;

    /**
     * Sets up the one window for the Turtle application. If called a more then one time it will thorough an error.
     */
    public static void WindowSetup() {
        if (windowSetupLatch) {
            throw new InvalidStateException("Window already set up");
        }
        windowSetupLatch = true;

        // Gui Set up
        JFrame frame = new JFrame();
        frame.setTitle("TurtlesEngine.Turtle styal graphics");

        JPanel mainPanel = new TurtleCanvas();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        frame.add(mainPanel);
        frame.pack();
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //Update thread set up.
        Thread updateThread = new Thread(() -> {
            while (true) {
                frame.repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        updateThread.start();

    }
}
