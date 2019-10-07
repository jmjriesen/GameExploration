import TurtlesEngine.Turtle;
import TurtlesEngine.TurtleCanvas;

import java.awt.*;

public class PrimaryApplication {
    public static void main(String[] args) throws InterruptedException {

        TurtleCanvas.WindowSetup();
        /*
        for (int i = 0; i < 100; i++) {

            Turtle turtle = new Turtle(20, 30, 0);
            switch(i%5){

                case 0:
                    turtle.color = Color.RED;break;
                case 1:
                    turtle.color = Color.GREEN;break;
                case 2:
                    turtle.color = Color.BLUE;break;
                case 3:
                    turtle.color = Color.YELLOW;break;
                case 4:
                    turtle.color = Color.MAGENTA;break;
            }
            turtle.forward(90, .5);
            turtle.turn(1);
            turtle.locking = false;
            turtle.forward(90, .5);
            turtle.turn(-1);
            turtle.forward(90, .5);
            turtle.turn(2);
            turtle.forward(90,.5);
            turtle.turn(-1);
            turtle.forward(90,.5);
            turtle.turn(2);
            turtle.forward(90,.5);
            turtle.turn(-1);
            turtle.forward(90,.5);

            Thread.sleep(300);
        }*/


        Turtle turtle = new Turtle(50, 50, 0);
        turtle.locking = true;
        turtle.turn(1);
        turtle.forward(90, .5);

        Turtle turtle1 = new Turtle(50, 50, 0);
        turtle1.color = Color.RED;
        turtle1.forward(80, .5);
    }


}


