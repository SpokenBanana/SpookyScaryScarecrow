package GameStates;

import Handlers.KeyInput;
import Handlers.MouseInput;

import java.awt.*;

/**
 * This screen is shown when the player dies in the game. He is given the option to quit, save, resume, or go back to menu,
 * much like the pause state.
 */
public class GameOverState extends PauseState{
    public GameOverState(GameStateManager manager, KeyInput keys, MouseInput mouse, MapLevel prevLevel) {
        super(manager, keys, mouse, prevLevel);
        prevLevel.resetLevel();
        resume.changeText("Continue");
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        g.setFont(new Font("Chiller", Font.BOLD, 60));
        g.drawString("GAME OVER", 200, 100);
    }
}
