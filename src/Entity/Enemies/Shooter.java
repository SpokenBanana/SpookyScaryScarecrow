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
    private int shootingTimeDelay;
    private boolean hiding;
    public Shooter(Point spawn) {
        super(spawn);
        health = 25;
        bullets = new ArrayList<Bullet>();
        sprites.addSprite("shooting", "Enemies/Shooter/shooter.png", true);
        sprites.addSprite("hiding", "Enemies/Shooter/hidingShooter.png");
        position = new Rectangle(spawn.x, spawn.y, 32,32);
        shootingTimeDelay = new Random().nextInt(100);
        currentDirection = Direction.Standing;
        hiding = false;
    }

    @Override
    public void update(Player player) {
        Rectangle playerLocation = player.getPosition();
        if (position.getLocation().distance(playerLocation.getLocation()) < 10 * MOVE_DISTANCE) {
            // hide
            hiding = true;
            sprites.setCurrent("hiding");
        }
        else {
            hiding = false;
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

        filterBullets(player);
        super.update();
    }


    @Override
    public void draw(Graphics2D g) {
        bullets.forEach(bullet -> bullet.draw(g));
        super.draw(g);
    }
    @Override
    public void hit(int damage) {
        // the player cannot hit this enemy when it is hiding
        if (!hiding)
            super.hit(damage);
    }
    @Override
    protected void attack(Player player) {
        bullets.add(new Pellet(new Rectangle(position.x, position.y, 16,16), player.getPosition().getLocation()));
    }
}
