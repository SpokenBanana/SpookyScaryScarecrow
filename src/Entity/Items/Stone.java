package Entity.Items;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * This item represents a stone object. This is mainly used to create more useful items
 */
public class Stone extends Item {
    public Stone() {
        id = Item.STONE_ID;
        setBounds();
        try{
            icon = ImageIO.read(new File("Assets/Sprites/items/stone.png"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void action() {

    }

    @Override
    public void update() {

    }

}
