package GameStates;

import Entity.Items.*;
import Entity.Player.Player;
import Handlers.KeyInput;
import Handlers.MouseInput;

import java.awt.*;

/**
 * This game state is used to craft new items for the player. The player can open up this menu and add items to
 * craft
 */
public class CraftingState extends GameState {

    // we need a reference to the player since we will be using his items and adding more items to him
    private Player player;

    // players can place an item in slot1 and slot2 and whatever they produce goes into slot3
    private Item[] slots;
    private Rectangle[] slotBounds;

    private int cursorItem;
    private Handlers.Button back;

    public CraftingState(GameStateManager manager, KeyInput keys, MouseInput mouse, Player player) {
        super(manager, keys, mouse);
        this.player = player;
        slots = new Item[3];
        slotBounds = new Rectangle[slots.length];
        int x= 150, y = 100;
        for (int i = 0; i < slotBounds.length; i++) {
            slotBounds[i] = new Rectangle(x, y, 32, 32);
            x += 100;
        }

        back = new Handlers.Button(new Rectangle(250, 500, 200, 40), "Resume");
        // no item
        cursorItem = -1;
    }

    @Override
    public void update() {

        // check if the player is trying to pick an item
        for (Item item : player.getItems()) {
            if (item != null && item.amount > 0) {
                if (mouseInput.didMouseClickOn(item.bounds)) {
                    if (cursorItem == item.id)
                        cursorItem = -1;
                    else
                        cursorItem = item.id;
                }
            }
        }

        // check if the player is trying to use the slots
        for (int i = 0; i < slots.length; i++) {
            // if the player clicked on a slot, he trying to place something there
            if (mouseInput.didMouseClickOn(slotBounds[i])) {

                // the player cannot place items on slot[2] and can only place an item if he has selected one
                if (i != 2 && cursorItem != -1 && isValidAddition(i, cursorItem)) {
                    slots[i] = player.createItem(cursorItem);
                    slots[i].add(1); // to show how much of the item will be taken from the player's inventory when he crafts this

                    // if they changed something, then we need to clear the output slot since a new item should appear
                    slots[2] = null;
                }
                // he is trying to collect the item crafted, let him get if there is an item there
                else if (i == 2 && slots[i] != null) {
                    player.addItem(slots[i].id);

                    // crafting takes one of each item
                    player.removeItem(slots[0].id);
                    player.removeItem(slots[1].id);

                    // ran out of items in that slot, so empty it out
                    if (player.getItem(slots[0].id).amount == 0)
                        slots[0] = null;
                    if (player.getItem(slots[1].id).amount == 0)
                        slots[1] = null;

                    // empty the slot either way
                    slots[2] = null;

                    // reset the current item
                    cursorItem = -1;
                }
            }
        }

        // if the player has selected two items selected for crafting then we should mix them and show the item they could get
        if (slots[2] == null && slots[1] != null && slots[0] != null) {
            slots[2] = mix(slots[0], slots[1]);
            if (slots[2] != null)
                slots[2].add(1);
        }

        back.setHovered(mouseInput.isMouseOver(back));
        if (mouseInput.didMouseClickOn(back)) {
            // if the crafting has made the player's current item to be emptied, than we un-assign it
            if (player.getCurrentItem() != -1 && player.getItem(player.getCurrentItem()).amount <= 0)
                player.setCurrentItem((short)-1);
            parentManager.deleteCurrentGame();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        // Title
        g.setFont(new Font("Chiller", Font.PLAIN, 50));
        g.setColor(Color.white);
        g.drawString("Spooky Crafting!", 150, 50);

        // description and instructions on how to craft
        g.setFont(new Font("Verdana", Font.BOLD, 13));
        g.drawString("Select an item by clicking it from the side ->", 250, 220);
        g.drawString("After selecting an item, click on any of the first two slots", 10, 180);
        g.drawString("After selecting two items to the two slots, an item should appear on the third!", 10, 300);
        g.drawString("Click the third item to get it!", 10, 325);
        g.drawString("Only certain combinations of two items produce an item, think carefully!", 10, 350);

        g.setFont(new Font("Droid Sans", Font.PLAIN, 15));

        // draw all the slots
        for (int i = 0; i < slots.length; i++) {
            g.setColor(slots[i] == null ? new Color(30,30,30) : Color.gray);
            g.fill(slotBounds[i]);
            if (slots[i] != null) {
                slots[i].draw(g, slotBounds[i].x, slotBounds[i].y);
                g.setColor(Color.white);
                g.drawString(Integer.toString(slots[i].amount), slotBounds[i].x + 25, slotBounds[i].y + 25);
            }
        }

        g.setColor(Color.white);
        g.drawString("+", 215, 120);

        g.drawString("=", 315, 120);

        int x = 620, y = 120;
        for (Item item : player.getItems()) {
            if (item == null) {
                g.setColor(new Color(30,30,30));
                g.fillRect(x, y, 32, 32);

                // border
                g.setColor(Color.black);
                g.drawRect(x, y, 32, 32);
                y += 32;
            }
            else {
                if (item.amount == 0)
                    g.setColor(new Color(30,30,30));
                else
                    g.setColor(cursorItem == item.id ? Color.green : Color.gray);

                g.fill(item.bounds);
                item.draw(g);
                g.setColor(Color.white);
                g.drawString(Integer.toString(item.amount), x + 20, y + 27);
                y += item.bounds.width;
            }
        }
        back.draw(g);
    }

    /**
     * Takes the two items and decides which combinations would make what
     * @param first the first item to mix
     * @param second the second item to mix with the first
     * @return the product of the items
     */
    private Item mix(Item first, Item second) {

        // stone and wood make a sword, doesn't matter which order
        if ((first instanceof Wood && second instanceof Stone) || (first instanceof Stone && second instanceof Wood)) {
            return new Sword();
        }

        // stone and stone make fire!
        if (first instanceof Stone && second instanceof Stone)
            return new Fire();

        // wood and grass make a bow
        if ((first instanceof Wood && second instanceof Grass) || (first instanceof Grass && second instanceof Wood))
            return new Bow();

        // grass and stone make an arrow
        if ((first instanceof Grass && second instanceof Stone) || (first instanceof Stone && second instanceof Grass))
            return new ArrowItem();

        return null;
    }

    /**
     * Determines if the player should be able to add this item to the slot. An addition is not valid if
     * the result would cause a negative amount of the item crafted. In cases where the player tries to mix
     * two of the same items but only has 1 of it, this comes in handy.
     * @param slotId the index of the slot trying to inserted
     * @param itemId the id of the item to be inserted
     * @return whether or not the insert is valid
     */
    private boolean isValidAddition(int slotId, int itemId) {
        // will give 0 if slotID = 1, 1 of slotId = 0 so that we can check the other slot
        int otherSlot = (((slotId - 1) % 2) + 2) % 2;

        // other slot is closed, so it is valid
        if (slots[otherSlot] == null)
            return true;

        // if the item  is different that the other selected item, then it is valid, if they are the same, then the
        // player better have more than one of that item, otherwise it is not valid.
        return slots[otherSlot].id != itemId || player.getItem(itemId).amount > 1;

    }
}