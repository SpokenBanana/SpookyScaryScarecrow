package GameStates;

import AssetManagers.SoundManager;
import Handlers.KeyInput;
import Handlers.MouseInput;

import java.awt.*;

/**
 * You can think of GameStates as the "screens" of the game such as menu, pause screens, or actual game play.
 */
public abstract class GameState {
    public GameStateManager parentManager;
    public MouseInput mouseInput;
    public KeyInput keyInput;
    public SoundManager soundManager;
    public final int GAME_WIDTH = 608, GAME_HEIGHT = 608;

    // we hold a reference to the game state manager so we can switch states inside the game
    public GameState(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        parentManager = manager;
        keyInput = keys;
        mouseInput = mouse;
        soundManager = new SoundManager();
    }

    /**
     * A method to be called once we enter the game state, we don't want to force any game state to use this, so
     * we keep an empty implementation and classes that want to use this can override this method.
     */
    public void enter() {}
    /**
     * A method to be called once we leave the game state, we don't want to force any game state to use this, so
     * we keep an empty implementation and classes that want to use this can override this method.
     */
    public void leave() {

    }

    public abstract void update();
    public abstract void draw(Graphics2D g);
}
