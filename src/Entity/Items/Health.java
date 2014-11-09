package Entity.Items;

import Entity.Player.Player;
import Handlers.KeyInput;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * This class gives health to the player when used
 */
public class Health extends Item {
    private Player player;
    private KeyInput keys;
    public Health() {
        description = "HEALTH | Press [R] to recover some health!";
        id = Item.HEALTH_ID;
        setBounds();
        try {
            icon = ImageIO.read(new File("Assets/Sprites/items/health.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Health (Player player, KeyInput keyInput) {
        this();
        this.player = player;
        this.keys = keyInput;
    }
    @Override
    public void action() {
        if (amount > 0){
            player.recover(10);
            amount--;
        }
    }

    @Override
    public void update() {
        if (keys.isPressed(KeyEvent.VK_R))
            player.useCurrentItem();
    }
}
