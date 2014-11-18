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

    Handlers.Button start, load;
    FileManager fileManager;
    boolean hovered;

    public Menu(GameStateManager manager, MouseInput mouse, KeyInput keys) {
        super(manager, keys, mouse);
        mouseInput = mouse;
        hovered = false;
        fileManager = new FileManager();
        parentManager = manager;
        soundManager = new SoundManager();
        soundManager.addSound("confirm", "confirm.wav");
        soundManager.addSound("music", "Music/menu.wav");
        soundManager.playSound("music", true);
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

        if (mouseInput.didMouseClickOn(start)){
            soundManager.playSound("confirm");
            soundManager.deleteSound("music");
            fileManager.createSaveDirectory("tmp/");
            parentManager.setGame(new MapLevel(parentManager, keyInput, mouseInput));
        }
        if (mouseInput.didMouseClickOn(load)) {
            soundManager.playSound("confirm");
            soundManager.deleteSound("music");
            soundManager.deleteSound("hover");
            parentManager.setGame(new LoadGame(parentManager, keyInput, mouseInput));
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.white);
        g.setFont(new Font("Chiller", Font.PLAIN, 78));
        g.drawString("Spooky Scary Scarecrow", 100,100);
        start.draw(g);
        load.draw(g);
    }

}
