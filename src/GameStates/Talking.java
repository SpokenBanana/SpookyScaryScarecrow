package GameStates;

import AssetManagers.SoundManager;
import Handlers.KeyInput;
import Handlers.MouseInput;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

/**
    This state will be used whenever we want someone to talk to the player
 */
public class Talking extends GameState{
    private final char PAUSE_CONVO = '`';

    private String currentScreenText;
    private String conversation;
    private GameState previousState;
    private KeyInput keyInput;
    private BufferedImage textBox;
    private int position;


    public Talking(GameStateManager manager, GameState prev, String convo, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        // we want to keep referencing the previous state because we want to have it still
        // be drawn on the screen as the text gets read
        parentManager = manager;
        currentScreenText = "";
        position = 0;
        previousState = prev;
        conversation = convo;
        soundManager = new SoundManager();
        soundManager.addSound("confirm", "confirm.wav");
        keyInput = keys;
        try {
            textBox = ImageIO.read(new File("Assets/UI/textbox.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void update() {
        // check this first because allowing the rest of the "else" logic when position == conversation.length
        // will result in an out of bounds error.
        if (position == conversation.length()) {
            // done with conversation, back to game play!
            if (keyInput.isPressed(KeyEvent.VK_F)){
                soundManager.playSound("confirm");
                parentManager.deleteCurrentGame();
            }
        }
        else{
            if (conversation.charAt(position) == PAUSE_CONVO && keyInput.isPressed(KeyEvent.VK_F)) {
                currentScreenText = "";
                position++;
                soundManager.playSound("confirm");
            }
            else if (conversation.charAt(position) != PAUSE_CONVO) {
                currentScreenText += conversation.charAt(position++);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        previousState.draw(g);
        g.setFont(new Font("Droid Sans", Font.PLAIN, 20));
        g.drawImage(textBox, 0, 608, null);
        g.setColor(Color.white);
        g.drawString(currentScreenText, 40, 650);
    }
}
