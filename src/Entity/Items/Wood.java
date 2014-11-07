package Entity.Items;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * This item represents a wood object. It is mainly used to craft into something more useful
 */
public class Wood extends Item{
    public Wood() {
        description = "WOOD | Doesn't look that useful, maybe I can make stuff with it";
        id = WOOD_ID;
        setBounds();
        try{
            icon = ImageIO.read(new File("Assets/Sprites/items/wood.png"));
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
