package Entity.Enemies;

import AssetManagers.Animation;
import Entity.Player.Player;

import java.awt.*;
import java.util.Random;

/**
 * This is an enemy that randomly appears around certain areas and tries to attack the player. It will
 * appear, attack, then disappear, only to re-appear at a random time.
 */
public class Slider extends Enemy{

    // this will be used to keep track of when we want the enemy to re-appear
    private int timeUntilRespawn;
    private Random random;
    private boolean respawned;
    private Point startingLocation;
    public Slider(Point spawn) {
        super(spawn);
        // we will use this variable to be amount of time the enemy will wait before attacking
        time = 60;
        random = new Random();
        timeUntilRespawn = random.nextInt(50) + 60;
        sprites.addAnimation("chargingLeft", new Animation("Enemies/Slider/sliderChargeLeft.png", 2, 150));
        sprites.addAnimation("chargingRight", new Animation("Enemies/Slider/sliderChargeRight.png", 2, 150));
        sprites.addAnimation("chargingDown", new Animation("Enemies/Slider/sliderChargeDown.png", 2, 150));
        sprites.addAnimation("chargingUp", new Animation("Enemies/Slider/sliderChargeUp.png", 2, 150));
        sprites.addSprite("waiting", "Enemies/Slider/sliderWait.png", true);
        respawned = false;
        position = new Rectangle(spawn.x, spawn.y, 32,32);
    }

    @Override
    public void update(Player player) {
        if (timeUntilRespawn <= 0) {
            if (!respawned)
                reSpawn();

            // time is used to wait, give the player a chance to see the enemy before it attacks
            if (time <= 0){
                decideNextNearestStep(player.getPosition().getLocation());
                syncSprite();
                if (position.intersects(player.getPosition())) {
                    // it stops because he kind of bumped, into the player, so there's a little pause for that
                    time = 30;
                    player.hit(5);
                    currentDirection = Direction.Standing;
                    sprites.setCurrent("waiting");
                }

            }
            else
                time--;

            // if the enemy gets a certain distance away from the it's spawn, it decides to go away
            if (startingLocation.distance(position.getLocation()) >= 10 * MOVE_DISTANCE || timeUntilRespawn <= -500){
                sprites.setCurrent("waiting");
                timeUntilRespawn = random.nextInt(50) + 60;
                currentDirection = Direction.Standing;
                respawned = false;
            }
            else // decrement this timer since we are now using it to control when the enemy will disappear again
                timeUntilRespawn--;
        }
        else
            timeUntilRespawn--;
        super.update();
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        time += 10;
    }
    @Override
    public void draw(Graphics2D g) {
        if (timeUntilRespawn <= 0 && respawned)
            super.draw(g);
    }
    @Override
    protected void attack(Player player) {
    }

    /**
     * When the enemy switches directions, we want to make sure that the enemy sprite matches the direction he is facing
     */
    protected void syncSprite() {
        if (currentDirection == Direction.Standing)
            sprites.setCurrent("waiting");
        else {
            switch (facingDirection) {
                case Down:
                    sprites.setCurrent("chargingDown");
                    break;
                case Up:
                    sprites.setCurrent("chargingUp");
                    break;
                case Left:
                    sprites.setCurrent("chargingLeft");
                    break;
                case Right:
                    sprites.setCurrent("chargingRight");
                    break;
            }
        }
    }
    /**
     * This places the enemy in a new position to surprise the player
     */
    protected void reSpawn() {
        // 19 is the amount of tiles in the word, MOVE_DISTANCE is multiplied to make sure it is moving tile-to-tile
        do {
            startingLocation = new Point(random.nextInt(19) * MOVE_DISTANCE, random.nextInt(19) * MOVE_DISTANCE);

            // keep looking for a random position until you find one that doesn't spawn him inside a block
        } while (blocks.stream().anyMatch(block -> block.contains(startingLocation)));

        moveTo(startingLocation.x, startingLocation.y);
        respawned = true;
    }
}
