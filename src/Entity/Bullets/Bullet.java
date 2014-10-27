package Entity.Bullets;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Bullet {
    protected double angle;
    protected Rectangle position;
    protected BufferedImage sprite;


    public int damage;
    public int vel;

    public Bullet(Rectangle location, Point target) {
        position = location;
        vel = 4;
        damage = 5;
        // formula to determine angle position need to travel to target.
        angle = Math.atan2(target.x - position.x, target.y - position.y);
    }

    public void update() {
        // add to the location by the angle we want multiplied by the speed we want to get the right effect
        position.x += (int) (Math.sin(angle) * vel);
        position.y += (int) (Math.cos(angle) * vel);
    }

    public Rectangle getPosition() {
        return position;
    }

    public void draw(Graphics2D g) {
        g.drawImage(sprite, position.x, position.y, position.width, position.height, null);
    }

}
