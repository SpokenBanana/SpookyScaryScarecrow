package Handlers;

import GameStates.*;
import GameStates.Menu;

import javax.swing.*;
import java.awt.*;

/**
 * The main handler of the game, it controls all the game states and make sure everything gets drawn
 * and updated.
 */
public class Game extends JPanel{

    GameStateManager gameStateManager;
    KeyInput keys;
    MouseInput mouseInput;

    public Game(KeyInput keyInput) {
        keys = keyInput;
        mouseInput = new MouseInput();
        setPreferredSize(new Dimension(708, 708));
        gameStateManager = new GameStateManager();
        setFocusable(true);
        addKeyListener(keys);
        addMouseListener(mouseInput);
        addMouseMotionListener(mouseInput);
        gameStateManager.addGame(new Menu(gameStateManager, mouseInput, keyInput));
    }


    public void gameLoop(){
        while (true) {
            gameStateManager.update();
            repaint();
            keys.update();
            try {
                // This acts like a mini "pause" so we can some of the action.
                Thread.sleep(1000/60);
            } catch (Exception e) {
                e.printStackTrace(); //print the error that occurred
            }
        }
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        g2.fillRect(0,0, getWidth(), getHeight());
        gameStateManager.draw(g2);
    }
}
