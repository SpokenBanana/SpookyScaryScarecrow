package GameStates;

import Handlers.*;
import Handlers.Button;

import java.awt.*;

/**
 * State to be displayed when the player pauses the game
 */
public class PauseState extends GameState {
    Handlers.Button resume;
    Handlers.Button exit;

    public PauseState(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        resume = new Button(new Rectangle(250, 200, 200, 40), "Resume");
        exit = new Button(new Rectangle(250, 300, 200, 40), "Exit");
    }

    @Override
    public void update() {
        resume.isHovered = mouseInput.isMouseOver(resume);
        exit.isHovered = mouseInput.isMouseOver(exit);

        if (mouseInput.didMouseClickOn(resume))
            parentManager.deleteCurrentGame();
        else if (mouseInput.didMouseClickOn(exit)) {
            parentManager.deleteCurrentGame();
            parentManager.deleteCurrentGame();
            parentManager.setGame(new Menu(parentManager, mouseInput, keyInput));
        }
    }

    @Override
    public void draw(Graphics2D g) {
        resume.draw(g);
        exit.draw(g);
    }
}