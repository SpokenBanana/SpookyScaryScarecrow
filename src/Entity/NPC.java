package Entity;

import Entity.Player.Player;
import GameStates.EventState;
import GameStates.GameState;
import GameStates.GameStateManager;
import GameStates.Talking;
import Handlers.KeyInput;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/*
    NPC (Non-Playable Character) is a class that will represent people the player can talk to and interact with.
    Since this is a small game, interactions will probably be limited to talking. Because of this, it makes sense
    to be a subclass of EventState, since once the player approaches the NPC, they will talk, much like a
    ConversationEvent.
 */
public class NPC extends EventState{

    protected String speech;
    protected BufferedImage sprite;
    protected KeyInput keys;

    public NPC(Point position, String speechText) {
        eventArea = new Rectangle(position.x, position.y, 32, 64);
        speech = speechText;
        try{
            sprite = ImageIO.read(new File("Assets/Sprites/npcs/citizen.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void update(Player player) {

    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(sprite, eventArea.x, eventArea.y, eventArea.width, eventArea.height, null);
    }

    @Override
    public void activate(GameStateManager manager, GameState gameState, Player player) {
        manager.addGame(new Talking(manager, gameState, speech, gameState.keyInput, gameState.mouseInput));
    }
}
