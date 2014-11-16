package GameStates.ArcadeGames.MouseFritz;

import AssetManagers.Animation;

import java.awt.*;

/**
 * This is sort of the player of the game. I made it into a separate class because there is a certain logical things
 * it needs to keep track of
 */
public class Cursor extends Rectangle {
    // when the player hits a ball, he has to make sure he hits another ball of the same color
    public Ball ballChosen;
    public Animation sprite;

    public Cursor(Rectangle bounds) {
        super(bounds);
        sprite = new Animation("ArcadeGames/MouseFritz/cursor.png", 3, 100);
    }
    public void move(Point location) {
        x = location.x - (width/2);
        y = location.y - (height/2);
    }
    public void draw(Graphics2D g) {
        sprite.draw(g, this);
    }

    public boolean isMatchingBalls() {
        return ballChosen == null;
    }
    public boolean doesMatchCurrentBall(Ball ball) {
        return ball.color == ballChosen.color && ball != ballChosen;
    }
    public void resetSelectedBall() {
        ballChosen.selected = false;
        ballChosen = null;
    }
}
