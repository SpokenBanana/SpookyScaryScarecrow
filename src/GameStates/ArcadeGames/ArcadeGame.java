package GameStates.ArcadeGames;

import AssetManagers.SoundManager;
import GameStates.GameState;
import GameStates.GameStateManager;
import Handlers.KeyInput;
import Handlers.MouseInput;

public abstract class ArcadeGame extends GameState {
    public enum State {
        Playing, Menu, GameOver, Pause
    }
    protected final String ARCADE_FONT = "Verdana";
    protected State state;

    public ArcadeGame(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        state = State.Menu;
        soundManager = new SoundManager();
    }
    @Override
    public void leave() {
        // make sure no sounds get left in memory
        soundManager.stopCurrentSound();
        soundManager.clearAllSounds();
        soundManager = null;
    }

}
