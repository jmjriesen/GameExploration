package TurtlesEngine;

import java.awt.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public class Turtle {

    private CountDownLatch  lock;
    private MovementCommand currentPos;
    private Deque<MovementCommand> commandList = new LinkedList<>();
    public  Color                  color       = Color.GREEN;
    public  boolean                locking     = false;

    public Turtle(int xpos, int ypos, double bearing) {
        currentPos = new MovementCommand(xpos, ypos, bearing, 0);
       TurtleCanvas.addResident(this);
    }

    public void paint(Graphics2D g) {
        g.setColor(color);
        g.rotate(currentPos.barring, currentPos.xpos, currentPos.ypos);
        g.fillOval((int) currentPos.xpos, (int) currentPos.ypos, 30, 10);
        g.rotate(-currentPos.barring, currentPos.xpos, currentPos.ypos);

    }

    public void update() {
        MovementCommand nextPos = commandList.peek();
        if (nextPos != null) {
            if (currentPos.movetwards(nextPos)) {
                commandList.remove();
                if (commandList.isEmpty() && lock != null) {
                    lock.countDown();
                }
            }
        }
    }

    private void addCommand(MovementCommand movementCommand) throws InterruptedException {
        commandList.add(movementCommand);
        if (locking) {
            lock = new CountDownLatch(1);
            lock.await();
        }
    }

    public void moveto(double xpos, double ypos) throws InterruptedException {
        moveto(xpos, ypos, .5);
    }

    public void moveto(double xpos, double ypos, double speed) throws InterruptedException {
        addCommand(new MovementCommand(xpos, ypos, currentPos.barring, speed));

    }

    public void forward(double distance, double speed) throws InterruptedException {
        MovementCommand lastQueued = getLast();
        MovementCommand target     = new MovementCommand(lastQueued);
        target.xpos += distance * Math.cos(lastQueued.barring);
        target.ypos += distance * Math.sin(lastQueued.barring);
        target.speed = speed;
        addCommand(target);
    }

    public void turn(double angle) throws InterruptedException {
        MovementCommand target = new MovementCommand(getLast());
        target.speed = .5;
        target.barring += angle;
        addCommand(target);
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
