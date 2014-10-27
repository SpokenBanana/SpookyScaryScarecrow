import Handlers.Game;
import Handlers.KeyInput;

import javax.swing.*;

public class Launcher extends JFrame {
    public static void main(String[] args) {
        new Launcher();
    }
    public Launcher() {
        // mostly boiler plate code to get a JFrame up and runnning
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
        /*
        JSONObject jsonObject = new JSONObject();
        Rectangle rectangle = new Rectangle(0,0,30,30);
        JSONObject rectangleObject = new JSONObject();
        rectangleObject.put("x", rectangle.x);
        jsonObject.put("Rectangle", rectangleObject);
        JSONObject otherObject = null;
        try {
            otherObject = (JSONObject) new JSONParser().parse(jsonObject.toJSONString());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        System.out.println(jsonObject.toJSONString());
        System.out.println(otherObject.toJSONString());
        */
    }
}
