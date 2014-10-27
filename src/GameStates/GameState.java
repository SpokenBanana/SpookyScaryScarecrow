package GameStates;

import AssetManagers.SoundManager;
import Handlers.KeyInput;
import Handlers.MouseInput;

import java.awt.*;

public abstract class GameState {
    // we hold a reference to the game state manager so we can switch states inside the game
    public GameState(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        parentManager = manager;
        keyInput = keys;
        mouseInput = mouse;
    }
    public GameStateManager parentManager;
    public MouseInput mouseInput;
    public KeyInput keyInput;
    public SoundManager soundManager;
    public final int GAME_WIDTH = 608, GAME_HEIGHT = 608;
    public abstract void update();
    public abstract void draw(Graphics2D g);
}
