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

    // to hold item description
    protected String itemDesc;

    // bar will be that green bar we will manipulate with.
    protected BufferedImage bar;

    public PlayerHUD(Player player) {
        startingHealth = player.getHealth();
        itemDesc = "";
        try {
            healthBar = ImageIO.read(new File("Assets/Sprites/Player/healthbar.png"));
            bar = ImageIO.read(new File("Assets/universal.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Player player, MouseInput mouseInput) {
        // see whether or not the player is hovering over the items
        boolean hovered = false;

        for (Item item : player.getItems()) {
            if (item == null)
                continue;
            if (mouseInput.isMouseOver(item.bounds)) {
                itemDesc = item.getDescription();
                hovered = true;
            }

            if (mouseInput.didMouseClickOn(item.bounds))
                // if the player clicked on an item that is already equipped, then we assume he is un-equipping it
                if (player.getCurrentItem() == item.id)
                    player.setCurrentItem((short)-1);
                else
                    player.setCurrentItem((short) item.id);
        }

        // if the player isn't hovering over the items, don't show any description
        if (!hovered)
            itemDesc = "";
    }

    public void draw(Graphics2D g, Player player) {
        g.setFont(new Font("Pericles", Font.PLAIN, 15));

        // draw health bar
        float healthPercentageLeft = player.getHealth() / (float) startingHealth;
        Color healthBarColor = determineColor(healthPercentageLeft);
        g.drawImage(bar, 608, 50, (int) (healthBar.getWidth() * healthPercentageLeft), healthBar.getHeight(), healthBarColor, null);
        g.drawImage(healthBar, 608, 50, null);

        drawItems(g, player);

        // crafting prompt
        g.setColor(Color.white);
        g.setFont(new Font("Pericles", Font.PLAIN, 10));
        g.drawString("Press [C] to craft!", 610, 150 + (Item.ITEM_AMOUNT * 32));

        g.setFont(new Font("Pericles", Font.BOLD, 15));
        g.drawString(itemDesc, 20, 630);

    }

    /**
     * Draws the player's items
     * @param g the component we can draw with
     * @param player the player of the game
     */
    private void drawItems(Graphics2D g, Player player) {
        g.setColor(Color.white);
        g.drawString("Inventory", 620,100);
        int x = 620, y = 120;
        for (Item item : player.getItems()) {
            if (item == null){
                drawEmptySlot(g, x, y);
            }
            else{
                if (item.amount == 0)
                    g.setColor(new Color(30,30,30));
                else
                    g.setColor(player.getCurrentItem() == item.id ? Color.green : Color.gray);

                g.fill(item.bounds);
                item.draw(g);
                g.setColor(Color.white);
                g.drawString(Integer.toString(item.amount), x + 20, y + 27);

                // if it not on full health
                float depreciation = item.getDepreciation() / 100.0f;
                if (depreciation < 1) {
                    Color itemBar = determineColor(depreciation);

                    // determine height of bar depending on how much is left
                    int itemBarHeight = (int) (32 * depreciation);

                    g.setColor(itemBar);
                    g.fillRect(x + 35, y + (32 - itemBarHeight), 10, itemBarHeight);
                }
            }
            y += 32;
        }
    }

    /**
     * Draws an empty slot at the location given
     * @param g the component we draw with
     * @param x the x position of location desired
     * @param y the y position of location desired
     */
    private void drawEmptySlot(Graphics2D g,  int x, int y) {
        g.setColor(new Color(30,30,30));
        g.fillRect(x, y, 32, 32);
        g.setColor(Color.black);
        g.drawRect(x, y, 32, 32);
    }

    /**
     * Health and depreciation bar colors are determine by the percentage of health/depreciation left. So we decide that
     * here
     * @param percentage the percentage of the amount left
     * @return the color to set the bar to
     */
    private Color determineColor(float percentage) {
        Color toReturn = Color.green;
        if (percentage < 0.3)
            toReturn = Color.red;
        else if (percentage < 0.6)
            toReturn = Color.orange;
        return toReturn;
    }

}
