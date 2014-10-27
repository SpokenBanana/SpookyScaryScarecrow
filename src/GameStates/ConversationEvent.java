package GameStates;

import Entity.Player.Player;
import Handlers.KeyInput;

import java.awt.*;

/*
    Once this event is triggered, it will change the screen to the "Talking" game state.
 */
public class ConversationEvent extends EventState{
    protected String conversation;
    protected KeyInput keys;

    public ConversationEvent(String convo, KeyInput key) {
        conversation = convo;
        keys = key;
    }
    public ConversationEvent(String convo, KeyInput key, Rectangle area) {
        this(convo, key); // calls the other constructor matching these parameters so we don't have to re-write it
        eventArea = area;
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
        manager.addGame(new Talking(manager, gameState, conversation, keys, gameState.mouseInput));
    }
}
