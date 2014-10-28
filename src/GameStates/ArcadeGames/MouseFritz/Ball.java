package GameStates.ArcadeGames.MouseFritz;

import java.awt.*;
import java.util.Random;

/**
 * In the game, there are balls that bounce around the screen, and they behave differently during
 * different times at the game. We still want to know it's bounds, having it extend Rectangle makes
 * everything easier instead of giving it a field of Rectangle.
 */
public class Ball extends Rectangle {

    public Color color;

    // if the player hit this ball, make sure he doesn't match it with himself
    public boolean selected;

    // this is true during the time of the game we want to avoid the balls;
    public boolean onTheFritz;

    private int xVel, yVel;


    public Ball(Rectangle bounds, Color c) {
        super(bounds);
        color = c;
        onTheFritz = false;
        selected = false;
        Random random = new Random();

        // to keep them unpredictable, we randomize the direction they go each time
        xVel = (random.nextInt(100) % 2 == 0) ? 4 : -4;
        yVel = (random.nextInt(100) % 2 == 0) ? 4 : -4;
    }

    public void update(int gameWidth, int gameHeight) {
        if (x < 0 || x > gameWidth - width)
            xVel *= -1;
        if (y < 0 || y > gameHeight - height)
            yVel *= -1;
        x += xVel;
        y += yVel;
    }

    public void draw(Graphics2D g) {
        if (onTheFritz)
            g.setColor(Color.red);
        else{
            // the ball glows when selected to remind the player what he selected
            if (selected) {
                g.setColor(Color.white);
                g.fill(this);
            }
            g.setColor(color);
        }

        g.fillOval(x, y, width, height);
    }
}
