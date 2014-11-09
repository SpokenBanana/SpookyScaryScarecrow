package GameStates;

import Entity.Player.Player;

import java.awt.*;

/**
 * This event replenishes the players health once activated, this is for the fountains in the game.
 */
public class HealthEvent extends EventState{
    public HealthEvent (Rectangle area) {
        eventArea = area;
    }
    @Override
    public void update(Player player) {
        // no updating
    }

    @Override
    public void draw(Graphics2D g) {
        // no drawing
    }

    @Override
    public void activate(GameStateManager manager, GameState gameState, Player player) {
        String conversation = "Your health was replenished!";

        // just to make it a little funnier, different responses occur when you are at certain health
        if (player.getHealth() == 100)
            conversation = "Alright that's enough fatty!";
        else if (player.getHealth() < 30)
            conversation = "Health is now full! Jeez, try to be more careful!";

        player.recover(100);

        // change game state to talking to let the player know what just happened
        manager.addGame(new Talking(manager, gameState, conversation,gameState.keyInput, gameState.mouseInput));
    }
}
