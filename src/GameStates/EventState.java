package GameStates;

import Entity.Player.Player;

import java.awt.*;

/*
    An event State is used to sort be a trigger to an event. Once triggered, they may do many things such as
    switch to another screen or change something about the player. An event is triggered once the activate()
    method is called, which the user can call at any time he feels the event should be triggered.
 */
public abstract class EventState {
    // sometimes you may want to trigger an event only in a certain area defined by eventArea.
    public Rectangle eventArea;
    public abstract void update(Player player);
    public abstract void draw(Graphics2D g);
    public abstract void activate(GameStateManager manager, GameState gameState, Player player);
}
