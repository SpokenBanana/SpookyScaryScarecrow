package Entity.Player;

import Entity.Items.Item;
import Handlers.MouseInput;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
    This will display the player's information on the side of the screen. It will show things like his status,
    items in current hand, etc.
 */
public class PlayerHUD {
    protected BufferedImage healthBar;

    // we need to know how much 100% health is.
    protected int startingHealth;

    // bar will be that green bar we will manipulate with.
    protected BufferedImage bar;

    public PlayerHUD(Player player) {
        startingHealth = player.getHealth();
        try {
            healthBar = ImageIO.read(new File("Assets/Sprites/Player/healthbar.png"));
            bar = ImageIO.read(new File("Assets/universal.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Player player, MouseInput mouseInput) {
        for (Item item : player.getItems()) {
            if (item == null)
                continue;
            if (mouseInput.didMouseClickOn(item.bounds))
                // if the player clicked on an item that is already equipped, then we assume he is un-equipping it
                if (player.getCurrentItem() == item.id)
                    player.setCurrentItem((short)-1);
                else
                    player.setCurrentItem((short) item.id);
        }
    }

    public void draw(Graphics2D g, Player player) {
        g.setFont(new Font("Droid Sans", Font.PLAIN, 15));
        float healthPercentageLeft = player.getHealth() / (float) startingHealth;
        Color healthBarColor = Color.green;
        if (healthPercentageLeft < 0.3)
            healthBarColor = Color.red;
        else if (healthPercentageLeft < 0.6)
            healthBarColor = Color.orange;

        g.drawImage(bar, 608, 50, (int) (healthBar.getWidth() * healthPercentageLeft), healthBar.getHeight(), healthBarColor, null);
        g.drawImage(healthBar, 608, 50, null);

        g.setColor(Color.white);
        g.drawString("Inventory", 620,100);
        int x = 620, y = 120;
        for (Item item : player.getItems()) {
            if (item == null){
                g.setColor(new Color(30,30,30));
                g.fillRect(x, y, 32, 32);
                g.setColor(Color.black);
                g.drawRect(x, y, 32, 32);
                y += 32;
            }
            else{
                g.setColor(player.getCurrentItem() == item.id ? Color.green : Color.gray);
                g.fill(item.bounds);
                item.draw(g);
                g.setColor(Color.white);
                g.drawString(Integer.toString(item.amount), x + 20, y + 27);
                y += item.bounds.height;
            }
        }
    }
}
