package Entity.Enemies;

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
        sprites.addSprite("charging" , "Enemies/Slider/sliderCharge.png");
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
                sprites.setCurrent("charging");
                if (position.intersects(player.getPosition())) {
                    // it stops because he kind of bumped, into the player, so there's a little pause for that
                    time = 30;
                    player.hit(5);
                    sprites.setCurrent("waiting");
                }
                super.update();
            }
            else
                time--;

            // if the enemy gets a certain distance away from the it's spawn, it decides to go away
            if (startingLocation.distance(position.getLocation()) >= 10 * MOVE_DISTANCE){
                sprites.setCurrent("waiting");
                timeUntilRespawn = random.nextInt(50) + 60;
                respawned = false;
            }
        }
        else
            timeUntilRespawn--;
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
     * This places the enemy in a new position to surprise the player
     */
    protected void reSpawn() {
        // 19 is the amount of tiles in the word, MOVE_DISTANCE is multiplied to make sure it is moving tile-to-tile
        startingLocation = new Point(random.nextInt(19) * MOVE_DISTANCE, random.nextInt(19) * MOVE_DISTANCE);
        moveTo(startingLocation.x, startingLocation.y);
        respawned = true;
    }
}
