package GameStates;

import AssetManagers.SoundManager;
import Handlers.Button;
import Handlers.KeyInput;
import Handlers.MouseInput;

import java.awt.*;

/**
 * The menu of the game. It just makes the game look a little more fancier.
 */
public class Menu extends GameState {

    Handlers.Button start;
    boolean hovered;

    public Menu(GameStateManager manager, MouseInput mouse, KeyInput keys) {
        super(manager, keys, mouse);
        mouseInput = mouse;
        hovered = false;
        parentManager = manager;
        soundManager = new SoundManager();
        soundManager.addSound("confirm", "confirm.wav");
        soundManager.addSound("hover", "hover.wav");
        soundManager.addSound("music", "Music/menu.wav");
        soundManager.playSound("music", true);
        start = new Button(new Rectangle(250,200,200,40), "Start Game!");
    }

    @Override
    public void update() {
        start.isHovered = mouseInput.isMouseOver(start);

        // this logic is simply to play the "hover" sound effect only once the mouse first arrives on the button
        if (start.isHovered && !hovered){
            hovered = true;
            soundManager.playSound("hover");
        }
        else if (!start.isHovered)
            hovered = false;

        if (mouseInput.didMouseClickOn(start)){
            soundManager.playSound("confirm");
            soundManager.stopSound("music");
            parentManager.setGame(new MapLevel(parentManager, keyInput, mouseInput));
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.white);
        g.setFont(new Font("Chiller", Font.PLAIN, 78));
        g.drawString("Spooky Scary Scarecrow", 100,100);
        start.draw(g);
    }

}
