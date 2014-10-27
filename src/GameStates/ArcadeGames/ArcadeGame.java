package GameStates.ArcadeGames;

import GameStates.GameState;
import GameStates.GameStateManager;
import Handlers.KeyInput;
import Handlers.MouseInput;

public abstract class ArcadeGame extends GameState {
    public enum State {
        Playing, Menu, GameOver, Pause
    }
    protected State state;

    public ArcadeGame(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        state = State.Menu;
    }

}
