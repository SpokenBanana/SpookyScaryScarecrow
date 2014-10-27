import Handlers.Game;
import Handlers.KeyInput;

import javax.swing.*;

/**
 * This pretty much gets the game on the screen, it sets up the frame and gets together all the screens we want
 * in the configurations we want it.
 */
public class Launcher extends JFrame {
    public static void main(String[] args) {
        new Launcher();
    }
    public Launcher() {
        // mostly boiler plate code to get a JFrame up and running
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        KeyInput keys = new KeyInput();
        Game game = new Game(keys);
        add(game);
        setFocusable(true);
        addKeyListener(keys);
        validate();
        pack(); // fit the frame to size of the game
        setLocationRelativeTo(null); // center it
        setResizable(false);
        setVisible(true);

        // START THE GAME!
        game.gameLoop();
    }
}
