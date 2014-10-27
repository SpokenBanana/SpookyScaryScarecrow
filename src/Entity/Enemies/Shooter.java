package Entity.Enemies;

import Entity.Bullets.Bullet;
import Entity.Bullets.Pellet;
import Entity.Player.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
    This guy sits in one area and shoots if the player is a certain distance away. If the player gets a certain
    distance close to the enemy, he hides and the player is unable to do any harm.
 */

public class Shooter extends Enemy {
    int shootingTimeDelay;

    public Shooter(Point spawn) {
        super(spawn);
        bullets = new ArrayList<Bullet>();
        sprites.addSprite("shooting", "Enemies/Shooter/shooter.png", true);
        sprites.addSprite("hiding", "Enemies/Shooter/hidingShooter.png");
        position = new Rectangle(spawn.x, spawn.y, 32,32);
        shootingTimeDelay = 0;
        shootingTimeDelay = new Random().nextInt(100);
    }

    @Override
    public void update(Player player) {
        Rectangle playerLocation = player.getPosition();
        if (position.getLocation().distance(playerLocation.getLocation()) < 10 * MOVE_DISTANCE) {
            // hide
            sprites.setCurrent("hiding");
        }
        else {
            sprites.setCurrent("shooting");
            // we don't want him to shoot too much, only in intervals
            if (shootingTimeDelay <= 0) {
                attack(player);
                shootingTimeDelay = 200;
            }
            else {
               shootingTimeDelay--;
            }
        }

        for (Bullet bullet : bullets)
            bullet.update();

        filterFinishedBullets(player);

    }


    @Override
    public void draw(Graphics2D g) {
        bullets.forEach(bullet -> bullet.draw(g));
        super.draw(g);
    }
    @Override
    protected void attack(Player player) {
        bullets.add(new Pellet(new Rectangle(position.x, position.y, 16,16), player.getPosition().getLocation()));
    }

    /**
     * Goes and deletes all bullets that we longer need to keep track of such as ones that went off the screen
     * and ones that hit the player
     * @param player
     */
    protected void filterFinishedBullets(Player player) {
        Rectangle playerPosition = player.getPosition();
        // stores bullets we want to get rid of
        ArrayList<Bullet> toRemove = new ArrayList<Bullet>();
        for (Bullet bullet : bullets) {
            Rectangle bulletPosition = bullet.getPosition();
            // not on the screen? DELETE!
            if (bulletPosition.x < 0 || bulletPosition.x > 608 || bulletPosition.y < 0 || bulletPosition.y > 608) {
                toRemove.add(bullet);
            }
            // hit player? its done it's job, go now!
            else if (bulletPosition.intersects(playerPosition))
            {
                toRemove.add(bullet);
                player.hit(bullet.damage); // deal the damage
            }
        }
        // go and remove the bullets we are done with.
        // this line of code goes though each element in toRemove, and 'for each' element, it calls
        // bullet's remove() function and passes in the element.
        toRemove.forEach(bullets::remove);
    }
}
