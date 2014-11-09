package Entity;

import AssetManagers.SpriteManager;
import Entity.Player.Player;
import GameStates.EventState;
import GameStates.GameState;
import GameStates.GameStateManager;
import GameStates.Talking;
import Handlers.KeyInput;

import java.awt.*;
import java.util.Random;

/**
    NPC (Non-Playable Character) is a class that will represent people the player can talk to and interact with.
    Since this is a small game, interactions will probably be limited to talking. Because of this, it makes sense
    to be a subclass of EventState, since once the player approaches the NPC, they will talk, much like a
    ConversationEvent.
 */
public class NPC extends EventState {

    protected String speech;
    protected SpriteManager sprites;
    protected KeyInput keys;
    private Random random;

    public NPC(Point position, String speechText) {
        eventArea = new Rectangle(position.x, position.y, 32, 64);
        speech = speechText;
        sprites = new SpriteManager();
        sprites.addSprite("left", "npcs/citizenLeft.png");
        sprites.addSprite("right", "npcs/citizenRight.png");
        sprites.addSprite("up", "npcs/citizenUp.png");
        sprites.addSprite("down", "npcs/citizenDown.png");

        // each npc starts off in a random position
        setDirection(getRandomDirection());
    }

    /**
     * Changes the direction of the npc
     * @param direction the direction to face the npc
     */
    public void setDirection(Entity.Direction direction) {
        switch (direction) {
            case Left:
                sprites.setCurrent("left");
                break;
            case Right:
                sprites.setCurrent("right");
                break;
            case Up:
                sprites.setCurrent("up");
                break;
            case Down:
                sprites.setCurrent("down");
                break;
        }
    }
    @Override
    public void update(Player player) {
        // only change directions 1% of the time
        if (random.nextInt(100) < 1)
            setDirection(getRandomDirection());
    }


    @Override
    public void draw(Graphics2D g) {
        sprites.draw(g, eventArea);
    }

    @Override
    public void activate(GameStateManager manager, GameState gameState, Player player) {
        // make the npc face the player when talking to him
        setDirection(player.getOppositeDirectionOf(player.facingDirection));
        manager.addGame(new Talking(manager, gameState, speech, gameState.keyInput, gameState.mouseInput));
    }

    /**
     * returns a random direction
     * @return a random direction
     */
    private Entity.Direction getRandomDirection() {
        Entity.Direction[] directions = {Entity.Direction.Left, Entity.Direction.Right, Entity.Direction.Up, Entity.Direction.Down };
        random = new Random();
        return directions[random.nextInt(directions.length)];
    }

}
