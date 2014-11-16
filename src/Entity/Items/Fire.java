package Entity.Items;

import Entity.Bullets.FireBall;
import Entity.Player.Player;
import Handlers.MouseInput;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The player can use fire as an item. He pretty much throws a fire ball at the enemy, It does pretty good damage but
 * get depreciated after a certain amount of uses.
 */
public class Fire extends Item {
    // we need a reference to the mouse input because the player aims with the mouse
    protected MouseInput mouseInput;
    protected BufferedImage mouseCursor;
    protected Rectangle screen;

    // needs reference to player to add fire bullets to the player's bullet list
    protected Player player;

    public Fire() {
        description = "FIRE | Aim with your mouse and click to shoot!";
        depreciation = 100;
        id = Item.FIRE_ID;
        screen = new Rectangle(0,0,608, 608);
        setBounds();
        try{
            icon = ImageIO.read(new File("Assets/Sprites/items/fire.png"));
            mouseCursor = ImageIO.read(new File("Assets/Sprites/items/cursor.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Fire(MouseInput mouseInput, Player player) {
        this();
        this.mouseInput = mouseInput;
        this.player = player;
    }


    @Override
    public void action() {
        // throws fire ball at the direction of the mouse
        player.bullets.add(new FireBall(new Rectangle(player.getPosition().x, player.getPosition().y, 16,16), mouseInput.getMouseLocation()));
    }

    @Override
    public void update() {
        if (mouseInput.didMouseClickOn(screen)) {
            action();
            // decrease depreciation, is it lower than 0, then remove an amount, if the amount then after is 0, un-equip
            changeDepreciation(-5);
            if (depreciation <= 0){
                if (--amount == 0)
                    player.setCurrentItem((short)-1);

                // reset depreciation
                depreciation = 100;
            }
        }
    }

    @Override
    public void actionDraw(Graphics2D g) {
        // draws it centered on the mouse
        g.drawImage(mouseCursor, mouseInput.getMouseLocation().x - (mouseCursor.getWidth() / 2),
                mouseInput.getMouseLocation().y - (mouseCursor.getHeight() / 2), null);
    }
}
