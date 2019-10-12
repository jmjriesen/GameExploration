package TurtlesEngine;

import java.awt.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public class Turtle {

    private CountDownLatch  commandLock;
    private MovementCommand currentPos;
    private Deque<MovementCommand> commandList = new LinkedList<>();
    public  Color                  color       = Color.GREEN;

    public  boolean commandsLockThreads = false;

    /**
     * Creates a new Turtle and regestrues it with the Canvas.
     * @param xpos starting x position.
     * @param ypos starting y position.
     * @param bearing starting bearing.
     */
    public Turtle(int xpos, int ypos, double bearing) {
        currentPos = new MovementCommand(xpos, ypos, bearing, 0);
       TurtleCanvas.addResident(this);
    }

    /**
     * Draws the Turtle on the graphic that is passed to it. Called in the update loop when redrawing the screen.
     * @param g graphic we want to paint on.
     */
    public void paint(Graphics2D g) {
        g.setColor(color);
        g.rotate(currentPos.barring, currentPos.xpos, currentPos.ypos);
        g.fillOval((int) currentPos.xpos, (int) currentPos.ypos, 30, 10);
        g.rotate(-currentPos.barring, currentPos.xpos, currentPos.ypos);

    }

    /**
     * Updates the current position of the turtle. Moves closer to the position at the head of the queue if there is one.
     * Also updates the commandLock that controls whether commands methods delay until commands are executed.
     */
    public void update() {
        MovementCommand nextPos = commandList.peek();
        if (nextPos != null) {
            //update position.
            if (currentPos.movetwards(nextPos)) {
                commandList.remove();

                if (commandList.isEmpty() && commandLock != null) {
                    commandLock.countDown();
                }
            }
        }
    }

    private void enqueueCommand(MovementCommand movementCommand) throws InterruptedException {
        commandList.add(movementCommand);
        if (commandsLockThreads) {
            commandLock = new CountDownLatch(1);
            commandLock.await();
        }
    }

    public void moveTo(double xpos, double ypos) throws InterruptedException {
        moveTo(xpos, ypos, .5);
    }

    public void moveTo(double xpos, double ypos, double speed) throws InterruptedException {
        enqueueCommand(new MovementCommand(xpos, ypos, currentPos.barring, speed));

    }

    public void forward(double distance, double speed) throws InterruptedException {
        MovementCommand lastQueued = getLast();
        //Set target coordinates.
        MovementCommand target     = new MovementCommand(lastQueued);
        target.xpos += distance * Math.cos(lastQueued.barring);
        target.ypos += distance * Math.sin(lastQueued.barring);
        target.speed = speed;
        enqueueCommand(target);
    }

    public void turn(double angle) throws InterruptedException {
        MovementCommand target = new MovementCommand(getLast());
        target.speed = .5;
        target.barring += angle;
        enqueueCommand(target);
    }

    private MovementCommand getLast() {

        if (commandList.isEmpty()) {
            return currentPos;
        } else {
            return commandList.getLast();
        }
    }


    /////////////////////////

    private class MovementCommand {
        double xpos;
        double ypos;
        double barring;
        double speed;

        MovementCommand(double xpos, double ypos, double barring, double speed) {
            this.xpos = xpos;
            this.ypos = ypos;
            this.barring = barring; //radians
            this.speed = speed;
        }

        MovementCommand(MovementCommand movementCommand) {
            this.xpos = movementCommand.xpos;
            this.ypos = movementCommand.ypos;
            this.barring = movementCommand.barring; //radians
            this.speed = movementCommand.speed;
        }

        private double updateValue(double current, double target, double delta) {
            double diff = target - current;
            if (diff != 0) {
                if (Math.abs(delta) < Math.abs(diff)) {
                    return current + delta;
                } else {
                    return target;
                }
            }
            return current;
        }

        boolean movetwards(MovementCommand target) {
            xpos = updateValue(xpos, target.xpos, target.speed * Math.cos(target.barring));
            ypos = updateValue(ypos, target.ypos, target.speed * Math.sin(target.barring));
            barring = updateValue(barring, target.barring, Math.signum(target.barring - barring) * target.speed / 10);
            return equals(target);
        }


        /**
         * Equality cares about xpos ypos and barring
         *
         * @param obj object to compare ageist
         * @return equal or not
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj.getClass() == this.getClass())) {
                return false;
            }
            MovementCommand mv = (MovementCommand) obj;
            return xpos == mv.xpos && ypos == mv.ypos && barring == mv.barring;
        }
    }


}
