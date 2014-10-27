package Entity.Enemies;

import Entity.Bullets.Bullet;
import Entity.Entity;
import Entity.Player.Player;

import java.awt.*;
import java.util.ArrayList;

/**
    This class will be the base class for all enemies. They all share characteristics that is defined here.
 */
public abstract class Enemy extends Entity {
    protected int damage;
    ArrayList<Bullet> bullets;
    // just to enforce this constructor. Each map will have locations for enemies, so we don't want to
    // spawn an enemy without any default location
    public Enemy(Point spawn) {
        super();
        oldPosition = new Point(spawn.x, spawn.y);
        targetPosition = new Point(oldPosition.x, oldPosition.y);
    }
    public abstract void update(Player player);

    /**
        Every enemy will also have some way of attacking the player so we want to enforce that.
     */
    protected abstract void attack(Player player);
    /**
        Most of the enemies will need an AI that require the next step that will lead
        them to their target (most cases will be towards the player). This will help decide where the enemy
        should move if he wants to get closer to the target. This is a very simple algorithm and there are much
        better ones out there but it will work for this small game.
     */
    protected void decideNextNearestStep(Point targetLocation){
        // make sure we cover even the largest distance it this can compute to
        double shortestDistance = Integer.MAX_VALUE;

        // stores the index of which location of the shortest path, -1 == have not found one
        int distanceBlockID = -1;

        // we store all the locations we are going to try here
        Point[] locations =  { new Point(position.x - MOVE_DISTANCE, position.y),   // left
                               new Point(position.x + MOVE_DISTANCE, position.y),   // right
                               new Point(position.x, position.y - MOVE_DISTANCE),   // up
                               new Point(position.x, position.y + MOVE_DISTANCE) }; //down

        // iterate through locations and decide which one is the shortest path
        for (int i = 0; i < locations.length; i++) {
            double distance = locations[i].distance(targetLocation);
            // we found a shorter distance!
            if (distance < shortestDistance){
                shortestDistance = distance;
                distanceBlockID = i;
            }
        }

        // now face the direction we want to move to.
        switch (distanceBlockID) {
            case 0:
                facingDirection = currentDirection = Direction.Left;
                break;
            case 1:
                facingDirection = currentDirection = Direction.Right;
                break;
            case 2:
                facingDirection = currentDirection = Direction.Up;
                break;
            case 3:
                facingDirection = currentDirection = Direction.Down;
                break;
            default:
                // we found nothing... so just stand there.
                currentDirection = Direction.Standing;
        }
    }
    /**
        Sometimes we want the AI to move away from something, this does exactly that. It finds the next step that
        will bring it furthest way from player.
     */
    protected void decideNextFurthestStep(Point targetLocation){
        // the smallest distance() can return is 0
        double longestDistance = 0;

        // stores the index of which location of the longest path, -1 == have not found one
        int distanceBlockID = -1;

        // we store all the locations we are going to try here
        Point[] locations =  { new Point(position.x - MOVE_DISTANCE, position.y),   // left
                new Point(position.x + MOVE_DISTANCE, position.y),   // right
                new Point(position.x, position.y - MOVE_DISTANCE),   // up
                new Point(position.x, position.y + MOVE_DISTANCE) }; //down

        // iterate through locations and decide which one is the longest path
        for (int i = 0; i < locations.length; i++) {
            double distance = locations[i].distance(targetLocation);
            // we found a longer distance!
            if (distance > longestDistance){
                longestDistance = distance;
                distanceBlockID = i;
            }
        }

        // now face the direction we want to move to.
        switch (distanceBlockID) {
            case 0:
                facingDirection = currentDirection = Direction.Left;
                break;
            case 1:
                facingDirection = currentDirection = Direction.Right;
                break;
            case 2:
                facingDirection = currentDirection = Direction.Up;
                break;
            case 3:
                facingDirection = currentDirection = Direction.Down;
                break;
            default:
                // we found nothing... so just stand there.
                currentDirection = Direction.Standing;
        }
    }
}
