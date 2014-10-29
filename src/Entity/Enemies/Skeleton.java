package Entity.Enemies;

import Entity.Player.Player;

import java.awt.*;
import java.util.Random;

/**
    The is an enemy in the game. He only attacks when the player reaches a certain distance to him.
 */
public class Skeleton extends Enemy {

    // once the player reaches a certain distance to the skeleton, he will become alerted, and once the player is a
    // certain distance away from the player, he not be alerted.

    protected boolean alerted;

    // we don't want the enemy to continuously hit the player, we want a hit, pause, and then hit again!
    protected int attackDelayTimer;

    public Skeleton(Point spawn) {
        super(spawn);
        alerted = false;

        attackDelayTimer = new Random().nextInt(100);
        position = new Rectangle(spawn.x, spawn.y, 32, 64);
        sprites.addSprite("rightstanding", "Enemies/Skeleton/skeleton.png", true);
    }

    @Override
    public void update(Player player) {
        Rectangle playerPosition = player.getPosition();
        // very basic AI for the Skeleton
        if (alerted) {
            Direction playerDirection = player.getFacingDirection();

            // we got the player in harms reach, now attack!
            if (getFacingBlock().intersects(playerPosition)) {
                currentDirection = Direction.Standing;
                if (attackDelayTimer <= 0)
                    attack(player);
                else
                    attackDelayTimer--;
            }
            else {
                // .getLocation() returns a Point data type that decideNextNearestStep() requires.
                decideNextNearestStep(playerPosition.getLocation());
            }

            // once he is far away enough, we don't need to chase him anymore.
            if (player.getPosition().getLocation().distance(position.getLocation()) > 10 * MOVE_DISTANCE) {
                alerted = false;
                currentDirection = Direction.Standing;
            }
        }
        else {
            // once the player gets close, we chase!
            if (player.getPosition().getLocation().distance(position.getLocation()) < 10 * MOVE_DISTANCE)
                alerted = true;
        }
        super.update();
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        // the enemy gets a little stunned after being hit
        attackDelayTimer += 10;

        // take a step back
        undoWalking();

        // they get knocked back 2 step
        switch (facingDirection) {
            case Down:
                targetPosition.y -= MOVE_DISTANCE * 2;
                break;
            case Left:
                targetPosition.x += MOVE_DISTANCE * 2;
                break;
            case Up:
                targetPosition.y += MOVE_DISTANCE * 2;
                break;
            case Right:
                targetPosition.x -= MOVE_DISTANCE * 2;
                break;
        }
    }

    @Override
    protected void attack(Player player) {
        // set sprite to attacking
        player.hit(8);
        attackDelayTimer = 50;
    }
}
