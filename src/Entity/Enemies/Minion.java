package Entity.Enemies;

import Entity.Player.Player;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * This is is an enemy that follows a path and deals a lot of damage to the player when he crosses it.
 * So it will be something the player will really be wary of.
 */
public class Minion extends Enemy {

    // the path the minion will follow
    private Queue<Point> path;

    // in the map data, we know what path to add to which minion by placing an id
    private String id;

    public Minion(Point spawn, String enemyId) {
        super(spawn);
        position = new Rectangle(spawn.x, spawn.y, 32,32);
        sprites.addSprite("sprite", "Enemies/Minion/minion.png", true);
        path = new LinkedList<>();
        id = enemyId;
        time = 0;
        addPath(spawn);
    }
    @Override
    public void update(Player player) {

        // only do logic when he is not "paused"
        if (time <= 0){
            decideNextNearestStep(path.peek());
            if (position.getLocation().distance(path.peek()) < 2) {
                // he goes back and forth so we just add this path to the end so it cycles
                path.add(path.remove());
            }
            if (getFacingBlock().intersects(player.getPosition())) {
                // add a delay so the enemy won't get hit every 60th of a second
                time = 40;
                attack(player);
                currentDirection = Direction.Standing;
            }
        }
        else
            time--;

        super.update();
    }

    @Override
    protected void attack(Player player) {
        player.hit(15);
    }
    @Override
    public void hit(int damage) {
        super.hit(damage);

        // the enemy gets a little stunned after being hit
        time += 5;

        // take a step back
        undoWalking();

        // they get knocked back 2 step, if knocking them back causes them to go out of the screen or in a wall, don't move
        int oldx = targetPosition.x, oldy = targetPosition.y;
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

        // little messy but basically if the new position would ram it into a wall or go off screen, restore its old location
        Rectangle newPosition = new Rectangle(targetPosition.x, targetPosition.y, position.width, position.height);
        if (blocks.stream().anyMatch(block -> block.intersects(newPosition)) ||
                targetPosition.x < 0 || targetPosition.y < 0 ||
                targetPosition.x + position.width> 608 ||
                targetPosition.y + position.height> 608 ) {
            targetPosition.x = oldx;
            targetPosition.y = oldy;
        }
    }

    /**
     * This adds a path to where the minion will travel to.
     */
    public void addPath(Point point) {
        path.add(point);
    }

    /**
     * When we add a path, we have a completely linear path, such that once the enemy reaches the end, he goes back to
     * the beginning, we want the enemy to retrace his steps back to the beginning, so this creates that cycle.
     */
    public void finishAddingPaths() {
        // exclude from adding the first and last element again. ex | 1 - 2 - 3 - 4 - 3 - 2, will naturally end back at 1 anyway
        path.add(path.remove());

        // where we will add the middle part backwards, L.I.F.O is what we want ^^^^^
        Stack<Point> dump = new Stack<>();

        for (int i = 0; i < path.size() - 2; i++) {
            dump.push(path.peek());
            path.add(path.remove());
        }

        path.add(path.remove());

        // now place those steps [3 - 2] to the end of paths [1 - 2 - 3 - 4] and we get a cycle!
        for (int i = 0; i < dump.size(); i++) {
            path.add(dump.pop());
        }
    }

    /**
     * gives the id of the enemy so we which enemy we are dealing with
     * @return the id of the enemy
     */
    public String getId() {
        return id;
    }
}
