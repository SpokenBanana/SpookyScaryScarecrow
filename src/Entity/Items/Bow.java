package Entity.Items;

import Entity.Bullets.Arrow;
import Entity.Player.Player;
import Handlers.MouseInput;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

/**
 * This is a bow object. The player will be able to shoot arrows at the enemies that deal more damage than fire. It uses
 * arrows as it's ammo and depreciates over time. We have it extend fire because it works the same way as fire, just
 * with more damage, different sprites, and more damage.
 */
public class Bow extends Fire {
    public Bow() {
        initialize();
    }
    public Bow(MouseInput mouseInput, Player player) {
        super(mouseInput, player);
        initialize();
    }
    private void initialize() {
        description = "BOW | Aw yes. Point and click with mouse to shoot powerful arrows!";
        id = Item.Bow_ID;
        setBounds();
        try{
            icon = ImageIO.read(new File("Assets/Sprites/items/bow.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void action() {
        player.bullets.add(new Arrow(new Rectangle(player.getPosition().x, player.getPosition().y, 16, 16), mouseInput.getMouseLocation()));
        player.removeItem(Item.ARROW_ID);
    }

    @Override
    public void update() {

        // only shoot if he clicked on the screen and if the player has arrows
        if (mouseInput.didMouseClickOn(screen) && player.hasItem(Item.ARROW_ID)) {
            action();
            // decrease depreciation, is it lower than 0, then remove an amount, if the amount then after is 0, un-equip
            changeDepreciation(-3);
            if (depreciation <= 0){
                if (--amount == 0)
                    player.setCurrentItem((short)-1);
                depreciation = 100;
            }
        }
    }
}
