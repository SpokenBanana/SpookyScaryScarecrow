package GameStates;

import DataManagers.FileManager;
import DataManagers.SavedFile;
import Handlers.*;
import Handlers.Button;

import java.awt.*;
import java.io.File;

/**
 * The menu that will show all the saved games that the player has saved and give him the option to pick which
 * to load up
 */
public class LoadGame extends GameState {
    private Button[] fileButtons;
    private Button back;

    public LoadGame(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);

        FileManager files = new FileManager();
        File[] savedFiles = files.getSavedFiles();

        fileButtons = new Button[savedFiles.length];
        back = new Button(new Rectangle(250, 100, 200, 40), "Back");

        int x = 250, y = 200;
        for (int i = 0; i < savedFiles.length; i++){
            // cannot load "tmp/" directory, it is not a saved game
            if (savedFiles[i].getName().equals("tmp"))
                continue;
            fileButtons[i] = new Handlers.Button(new Rectangle(x, y, 200, 40), savedFiles[i].getName());
            y += 60;
        }
    }

    @Override
    public void update() {
        for (Button button : fileButtons) {
            if (button == null)
                continue;
            button.setHovered(mouseInput.isMouseOver(button));
            if (mouseInput.didMouseClickOn(button)) {
                SavedFile file = new SavedFile(button.getButtonText() + "/");
                parentManager.setGame(new MapLevel(parentManager, keyInput, mouseInput, file));
            }
        }
        back.setHovered(mouseInput.isMouseOver(back));
        if (mouseInput.didMouseClickOn(back)) {
            parentManager.setGame(new Menu(parentManager, mouseInput, keyInput));
        }

    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.white);
        g.setFont(new Font("Pericles", Font.BOLD, 27));
        g.drawString("Saved Games", 250, 50);
        for (Button button : fileButtons)
            if (button != null)
                button.draw(g);
        back.draw(g);

    }
}
