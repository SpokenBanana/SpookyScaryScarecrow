package Entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/*
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

    public void draw(Graphics2D g, Player player) {
        float healthPercentageLeft = player.getHealth() / (float) startingHealth;
        Color healthBarColor = Color.green;
        if (healthPercentageLeft < 0.3)
            healthBarColor = Color.red;
        else if (healthPercentageLeft < 0.6)
            healthBarColor = Color.orange;

        g.drawImage(bar, 608, 50, (int) (healthBar.getWidth() * healthPercentageLeft), healthBar.getHeight(), healthBarColor, null);
        g.drawImage(healthBar, 608, 50, null);
    }
}
