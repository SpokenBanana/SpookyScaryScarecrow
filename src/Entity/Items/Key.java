package Entity.Items;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

/**
 * Keys are used to open up doors in the game, once the key is used, it is discarded.
 */
public class Key extends Item{

    public Key() {
        id = 1;
        bounds = new Rectangle(620,120 + (id * 32),32,32);
        try{
            icon = ImageIO.read(new File("Assets/Sprites/items/key.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void action() {
        // discard when used
        if (amount > 0)
            amount--;
    }

    @Override
    public void update() {
        // no update
    }
}
