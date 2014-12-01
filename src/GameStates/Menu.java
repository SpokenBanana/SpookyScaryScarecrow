package GameStates;

import AssetManagers.SoundManager;
import DataManagers.FileManager;
import Handlers.Button;
import Handlers.KeyInput;
import Handlers.MouseInput;

import java.awt.*;

/**
 * The menu of the game. It just makes the game look a little more fancier.
 */
public class Menu extends GameState {

    private Handlers.Button start, load;
    private FileManager fileManager;
    boolean hovered;

    public Menu(GameStateManager manager, MouseInput mouse, KeyInput keys) {
        super(manager, keys, mouse);
        mouseInput = mouse;
        hovered = false;

        // used to clear data from "tmp/" directory for player to use
        fileManager = new FileManager();
        parentManager = manager;

        soundManager = new SoundManager();
        soundManager.addSound("confirm", "confirm.wav");
        soundManager.addSound("music", "Music/menu.wav");
        soundManager.playSound("music", true);

        // buttons to trigger what to do next (like start a game or load a previous one
        start = new Button(new Rectangle(250,200,200,40), "Start Game!");
        load = new Button(new Rectangle(250, 300,200,40), "Load Game!");
    }
    @Override
    public void leave() {
        soundManager.clearAllSounds();
    }

    @Override
    public void update() {
        start.setHovered(mouseInput.isMouseOver(start));
        load.setHovered(mouseInput.isMouseOver(load));

        // start the player off in a new game
        if (mouseInput.didMouseClickOn(start)){
            soundManager.playSound("confirm");

            // he isn't in a saved game yet, so put all game files in this directory instead until he decides to save
            fileManager.createSaveDirectory("tmp/");
            parentManager.setGame(new MapLevel(parentManager, keyInput, mouseInput));
        }
        // he clicked this, then he trying to load a game so go to the load game screen
        else if (mouseInput.didMouseClickOn(load)) {
            soundManager.playSound("confirm");
            parentManager.setGame(new LoadGame(parentManager, keyInput, mouseInput));
        }
    }

    @Override
    public void draw(Graphics2D g) {
        // title screen
        g.setColor(Color.white);
        g.setFont(new Font("Chiller", Font.PLAIN, 48));
        g.drawString("Spooky Scary Scarecrow", 100,100);

        // instructions
        g.setFont(new Font("Pericles", Font.PLAIN, 20));
        g.drawString("Use the arrow keys or WASD to move!", 50, 500);
        g.drawString("You can talk and interact with people or things by pressing F!", 5, 550);
        g.drawString("Click Start Game to begin playing", 5, 600);
        g.drawString("Click Load Game to load a saved game!", 5, 650);

        // draw buttons
        start.draw(g);
        load.draw(g);
    }

}
