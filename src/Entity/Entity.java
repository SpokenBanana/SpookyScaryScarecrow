package Entity;

import AssetManagers.SpriteManager;

import java.awt.*;
import java.util.ArrayList;

public abstract class Entity {
    public enum Direction {
        Left, Right, Down, Up, Standing
    }
    protected Rectangle position;
    protected Point oldPosition, targetPosition;
    protected int velocity, health;
    protected final int MOVE_DISTANCE = 16;

    // every entity needs to know where he can and cannot move across, blocks represents places they collide with.
    protected ArrayList<Block> blocks;

    // will help to display a different sprite to let the player know he has been hurt
    protected boolean hurt;

    // each entity may need time to recover from certain events (getting hit etc) so
    // each one will have a time variable to manage that.
    protected int time;

    // facingDirection is where the player is facing, so it can never be Direction.Standing
    // currentDirection pretty much tells us the players movement, facingDirection tells us where he is facing.
    protected Direction currentDirection, facingDirection;

    // keeps track of how far we are in moving to a tile
    protected float completed;

    public SpriteManager sprites;

    // the class will just initialize values to prevent null pointer exceptions
    public Entity() {
        sprites = new SpriteManager();
        blocks = new ArrayList<Block>();
        currentDirection = Direction.Standing;
        facingDirection = Direction.Down;
        position = new Rectangle();
        oldPosition = new Point();
        velocity = 5;
        targetPosition = new Point();
    }
    public void update() {
        updateMovement();
    }
    public void draw(Graphics2D g) {
        sprites.draw(g, position);
    }
    public void updateMovement() {
        // in lerp() we use 1 / (MOVE_DISTANCE - velocity) making 1 the number that we reach when we want to stop
        if (completed >= 1) {
            // he is done walking, he now standing
            if (currentDirection != Direction.Standing) facingDirection = currentDirection;
            finishWalking(); // make sure he makes it to the tile

            return;
        }
        if (completed == 0)
            getNextPosition();
        moveEntity();
    }
    public int getHealth(){
        return health;
    }
    /*
        Invoke a "hit" on the player and lowers his health
     */
    public void hit(int damage) {
        health -= damage;
    }
    /*
        heals the player by the amount given
     */
    public void recover(int amount) {
        health += amount;
    }

    /*
        This teleports the player to a point. This method helps to make sure there is
        consistency with oldposition and target position
     */
    public void moveTo(int x, int y) {
        oldPosition.x = targetPosition.x = position.x = x;
        oldPosition.y = targetPosition.y = position.y = y;
    }
    public void setBlocks(ArrayList<Block> blocksToSet) {
        blocks = blocksToSet;
    }

    public Direction getFacingDirection() {
        return facingDirection;
    }
    public Direction getCurrentDirection() {
        return currentDirection;
    }
    public Rectangle getPosition() {
        return position;
    }
    /*
        Returns the opposite direction of a given direction. Can be useful for when we want something facing someone
     */
    public Direction getOppositeDirectionOf(Direction direction) {
        switch (direction) {
            case Down:
                return Direction.Up;
            case Up:
                return Direction.Down;
            case Left:
                return Direction.Right;
            case Right:
                return Direction.Left;
        }
        return null;
    }
    /*
        gets the area in front of the player. This can be useful to know if the player is in front of door. You would
        take the Rectangle given from this method and check if it intersects with the bounds of the door, if so,
        you know he is facing a door.
     */
    public Rectangle getFacingBlock() {
        switch (facingDirection) {
            case Down:
                return new Rectangle(position.x, position.y + position.height / 2, position.width, position.height);
            case Up:
                return new Rectangle(position.x, position.y - position.height / 2, position.width, position.height);
            case Left:
                return new Rectangle(position.x - position.width, position.y, position.width, position.height);
            case Right:
                return new Rectangle(position.x + position.width, position.y, position.width, position.height);
        }
        return null;
    }
    /*
        This will set the next target position depending on what currentDirection the entity is facing.
     */
    protected void getNextPosition() {
        Rectangle newPosition = null;
        switch (currentDirection) {
            case Down:
                newPosition = new Rectangle(position.x, position.y + MOVE_DISTANCE, position.width, position.height);
                break;
            case Left:
                newPosition = new Rectangle(position.x - MOVE_DISTANCE, position.y, position.width, position.height);
                break;
            case Right:
                newPosition = new Rectangle(position.x + MOVE_DISTANCE, position.y, position.width, position.height);
                break;
            case Up:
                newPosition = new Rectangle(position.x, position.y - MOVE_DISTANCE, position.width, position.height);
                break;
        }
        if (newPosition != null) {
            // we don't want to move the player to new position if it makes him collide with a block
            for (Block block : blocks)
                if (block.intersects(newPosition))
                    return;
            targetPosition = new Point(newPosition.x, newPosition.y);
        }
    }
    /*
        Sometimes a bug comes a long where the player isn't in a coordinate where x and y is
        a multiple of 32, which can be trouble some in the movement and collision detection. This
        method is called to make sure that there is no doubt the player fully made it to the target
        position
     */
    protected void finishWalking() {
        completed = 0;
        oldPosition.x = position.x = targetPosition.x;
        oldPosition.y = position.y = targetPosition.y;
    }
    /*
         Sometimes it makes more sense to go back than go forwards, for example, if the player presses the forward
         button but then decides not to (in case of a cliff or something) we want him to be able to pull away from the
         button fast enough so he basically "undo" the walk. I also found a bug where the skeleton will jump forward to
         his targetPosition, which makes him close enough to continue following the player, not allowing him to
         ever stop. This will make him go backward, and get rid of that jerking motion
     */
    protected void undoWalking() {
        completed = 0;
        targetPosition.x = position.x = oldPosition.x;
        targetPosition.y = position.y = oldPosition.y;
    }
    /*
        This is a formula to smoothly move from one coordinate to the other. It works by
        taking the difference between the two position (b - a) and then given how far they are in the
        movement, we add that to the old position.
     */
    protected float lerp(float old, float target, float soFarCompleted) {
        if (soFarCompleted <= 0)
            return old;
        return old + soFarCompleted * (target - old);
    }
    /*
        This uses lerp() to smoothly move the player. The velocity gives us the amount of time it will
         take to move fully, to the next coordinate. Lerp takes how far we are in the movement (completed) and
         gives us back to coordinates to smoothly move the player.
     */
    protected void moveEntity() {
        // move distance is the highest velocity a player can have since that is basically teleporting to the next tile
        // so to make higher numbers seem faster, we must subtract from MOVE_DISTANCE creating a bigger fraction to add
        // to completed, making it faster to reach 1.
        completed += (float) 1 / (MOVE_DISTANCE - velocity);
        position.x = (int) lerp(oldPosition.x, targetPosition.x, completed);
        position.y = (int) lerp(oldPosition.y, targetPosition.y, completed);
    }
}
