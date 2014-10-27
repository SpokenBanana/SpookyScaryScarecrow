package Entity.Enemies;

import Entity.Bullets.Bullet;
import Entity.Bullets.FireBall;
import Entity.Player.Player;

import java.awt.*;
import java.util.ArrayList;

/**
    This enemy is a ghost, he will try to keep a certain distance away from the enemy and will
    sometimes fade away, leaving the player confused on where he is.
 */
public class Ghost extends Enemy {

    int shootingDelayTime;
    public Ghost(Point spawn) {
        super(spawn);
        shootingDelayTime = 0;
        bullets = new ArrayList<Bullet>();
        sprites.addSprite("sprite", "Enemies/Ghost/ghost.png", true);
        position = new Rectangle(spawn.x, spawn.y, 32,32);

    }

    public void shoot(Point player) {
        bullets.add(new FireBall(new Rectangle(position.x, position.y, 16,16), player));
    }

    /**
        We'll go through the list finding bullets that have been maybe out of bounds of hit the target and remove
        them.
     */
    protected void filterFinishedBullets(Player player) {
        Rectangle playerPosition = player.getPosition();
        ArrayList<Bullet> toRemove = new ArrayList<Bullet>();
        for (Bullet bullet : bullets) {
            Rectangle bulletPosition = bullet.getPosition();
            if (bulletPosition.x < 0 || bulletPosition.x > 608 || bulletPosition.y < 0 || bulletPosition.y > 608) {
                toRemove.add(bullet);
            }
            else if (bulletPosition.intersects(playerPosition))
            {
                toRemove.add(bullet);
                player.hit(bullet.damage);
            }
        }
        for (Bullet bullet : toRemove)
            bullets.remove(bullet);
    }

    @Override
    public void update(Player player) {
        Rectangle playerPosition = player.getPosition();

        // The simple AI that runs this guy
        if (playerPosition.getLocation().distance(position.x, position.y) < 10 * MOVE_DISTANCE) {
            // move away
            decideNextFurthestStep(playerPosition.getLocation());
            if (shootingDelayTime <= 0){
                shoot(playerPosition.getLocation());
                shootingDelayTime = 50;
            }
            else
                shootingDelayTime--;
        }
        else{ 
           // move toward
            decideNextNearestStep(playerPosition.getLocation());
        }

        for(Bullet bullet : bullets)
            bullet.update();
        filterFinishedBullets(player);
        super.update();
    }

    @Override
    protected void attack(Player player) {

    }

    @Override
    public void draw(Graphics2D g) {
        for (Bullet bullet : bullets) bullet.draw(g);
        super.draw(g);
    }
}
