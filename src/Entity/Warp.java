package Entity;

import Entity.Player.Player;
import GameStates.EventState;
import GameStates.GameState;
import GameStates.GameStateManager;
import GameStates.MapLevel;

import java.awt.*;

/**
 * Warp is king of like a portal that transitions the player to another screen or map.
 */
public class Warp extends EventState{

    protected String levelToGo;

    public Warp(String toLevelName, Rectangle bounds) {
        levelToGo = toLevelName;
        eventArea = bounds;
    }
    @Override
    public void update(Player player) {
        // no logic
    }

    @Override
    public void draw(Graphics2D g) {
        // no drawing
    }

    @Override
    public void activate(GameStateManager manager, GameState gameState, Player player) {
        // this event only works if gameState is a map, since we want to change the map
        if (gameState instanceof MapLevel) {
            // cast it as a MapLevel and now we can call MapLevel methods
            ((MapLevel) gameState).setLevel(levelToGo);
            movePlayer(player, gameState);
        }
    }

    /**
     * This moves the player to the position that makes sense when transitioning to a new screen
     * @param playerObject
     * @param gameState
     */
    public void movePlayer(Player playerObject, GameState gameState) {
        Rectangle player = playerObject.getPosition();
        if (player.x < 0)
            playerObject.moveTo(gameState.GAME_WIDTH - 32, player.y);
        else if (player.x + player.width > gameState.GAME_HEIGHT)
            playerObject.moveTo(32, player.y);
        else if (player.y < 0)
            playerObject.moveTo(player.x, gameState.GAME_HEIGHT - 32 - player.height);
        else if (player.y + player.height > gameState.GAME_HEIGHT)
            playerObject.moveTo(player.x, 32);
    }
}
