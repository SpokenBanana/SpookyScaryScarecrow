package Entity.Items;

import Entity.Player.Player;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * This item is a weapon the player can use to defeat enemies easier. It deals more damage than his hands
 */
public class Sword extends Item {
    private int damage;
    private Player player;

    public Sword() {
        description = "SWORD | Pressing [SPACE] deals more damage with this equipped!";
        damage = 15;
        depreciation = 100;
        id = Item.SWORD_ID;
        setBounds();
        try {
            icon = ImageIO.read(new File("Assets/Sprites/items/sword.png"));
        } catch (Exception e) {

        }
    }
    public Sword(Player player) {
        this();
        this.player = player;
    }

    @Override
    public void update() {
    }

    @Override
    public void action() {
        // decrease depreciation on every use, if it goes lower than or equal to 0, we broke it
        if ((depreciation -= 5) <= 0) {
            // reduce amount
            amount--;

            // reset depreciation either way
            depreciation = 100;
        }
    }
}
