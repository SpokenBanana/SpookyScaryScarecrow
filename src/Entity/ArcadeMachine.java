package Entity;

import Entity.Player.Player;
import GameStates.ArcadeGames.Breakout.BreakoutGame;
import GameStates.ArcadeGames.MouseFritz.MouseFritzGame;
import GameStates.ArcadeGames.Pong.PongGame;
import GameStates.ArcadeGames.Snake.SnakeGame;
import GameStates.EventState;
import GameStates.GameState;
import GameStates.GameStateManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
    This class is used to make that transition to the game the arcade game. Id represents the ID of the game
    the arcade machine will play.
 */
public class ArcadeMachine extends EventState {

    // id of game
    protected int id;
    BufferedImage sprite;
    private String path = "Assets/Sprites/misc/arcade";

    public ArcadeMachine(Rectangle bounds, int gameId, char direction) {
        eventArea = bounds;
        id = gameId;
        try{
            switch (direction) {
                case 'd': // down
                    sprite = ImageIO.read(new File(path + "Down.png"));
                    break;
                case 'r': // right
                    sprite = ImageIO.read(new File(path + "Right.png"));
                    break;
                case 'u': // up
                    sprite = ImageIO.read(new File(path + "Up.png"));
                    break;
            }
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
        switch (id) {
            case 0:
                manager.addGame(new SnakeGame(manager, gameState.keyInput, gameState.mouseInput));
                break;
            case 1:
                manager.addGame(new PongGame(manager, gameState.keyInput, gameState.mouseInput));
                break;
            case 2:
                manager.addGame(new BreakoutGame(manager, gameState.keyInput, gameState.mouseInput));
                break;
            case 3:
                manager.addGame(new MouseFritzGame(manager, gameState.keyInput, gameState.mouseInput));
                break;
        }
    }
}
