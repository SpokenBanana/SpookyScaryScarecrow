package Entity.Items;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

/**
 * This item is a weapon the player can use to defeat enemies easier. It deals more damage than his hands
 */
public class Sword extends Item {
    private int damage;
    public Sword() {
        damage = 15;
        id = 0;
        bounds = new Rectangle(620,120 + (id * 32),32,32);
        try {
            icon = ImageIO.read(new File("Assets/Sprites/items/sword.png"));
        } catch (Exception e) {

        }
    }

    @Override
    public void update() {
        // no updating for sword
    }

    @Override
    public void action() {

    }
}
