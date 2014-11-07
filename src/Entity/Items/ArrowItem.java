package Entity.Items;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * This is an arrow item, this class is mainly used as a counter to how many arrows the player has and also a means
 * to collect arrows in the game as well as craft them.
 */
public class ArrowItem extends Item {

    public ArrowItem() {
        description = "ARROW | Not much use by itself, looks like I need a bow";
        id = Item.ARROW_ID;
        setBounds();
        try{
            icon = ImageIO.read(new File("Assets/Sprites/items/arrowItem.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void action() {
        // no action
    }

    @Override
    public void update() {
        // no update;
    }
}
