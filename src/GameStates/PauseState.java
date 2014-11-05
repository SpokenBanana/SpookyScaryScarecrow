package GameStates;

import Handlers.*;
import Handlers.Button;

import java.awt.*;

/**
 * State to be displayed when the player pauses the game
 */
public class PauseState extends GameState {
    Handlers.Button resume, exit, save;
    MapLevel prevState;
    String status;

    public PauseState(GameStateManager manager, KeyInput keys, MouseInput mouse, MapLevel previousState) {
        super(manager, keys, mouse);
        resume = new Button(new Rectangle(250, 200, 200, 40), "Resume");
        save = new Button(new Rectangle(250, 300, 200, 40), "Save");
        exit = new Button(new Rectangle(250, 400, 200, 40), "Exit");
        prevState = previousState;
        status = "";
    }

    @Override
    public void update() {
        resume.isHovered = mouseInput.isMouseOver(resume);
        exit.isHovered = mouseInput.isMouseOver(exit);
        save.isHovered = mouseInput.isMouseOver(save);

        if (mouseInput.didMouseClickOn(resume))
            parentManager.deleteCurrentGame();
        else if (mouseInput.didMouseClickOn(exit)) {
            parentManager.deleteCurrentGame();
            prevState.soundManager.stopCurrentSound();
            parentManager.deleteCurrentGame();
            parentManager.setGame(new Menu(parentManager, mouseInput, keyInput));
        }
        else if (mouseInput.didMouseClickOn(save)) {
            // save file
            prevState.saveGame();
            status = "Game Saved!";
        }
    }

    @Override
    public void draw(Graphics2D g) {
        resume.draw(g);
        save.draw(g);
        g.drawString(status, save.x + save.width + 10, save.y);
        exit.draw(g);
    }
}