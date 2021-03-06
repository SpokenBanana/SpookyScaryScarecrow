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
    private Item[] craftingSlots;
    private Rectangle[] slotBounds;

    private int cursorItem;
    private Handlers.Button back;

    public CraftingState(GameStateManager manager, KeyInput keys, MouseInput mouse, Player player) {
        super(manager, keys, mouse);
        this.player = player;
        craftingSlots = new Item[3];
        slotBounds = new Rectangle[craftingSlots.length];

        // placing the slots in the right position
        int x= 150, y = 100;
        for (int i = 0; i < slotBounds.length; i++) {
            slotBounds[i] = new Rectangle(x, y, 32, 32);
            x += 100;
        }

        back = new Handlers.Button(new Rectangle(250, 500, 200, 40), "Resume");
        // no item
        cursorItem = Item.NO_ITEM;
    }

    /**
     * In this update method, we need to constantly check if the player is interacting with the slots. We check if he
     * is trying to select an item, we check if he trying to put the selected item in any slots, and check if he is
     * trying to pick up a crafted item. All of this and we will also be verifying if he has sufficient amount of items
     * and that the two items actually create an item.
     */
    @Override
    public void update() {
        // check if the player is trying to pick an item
        for (Item item : player.getItems()) {
            if (item == null)
                continue;

            // cannot choose an item that he has ran out of
            if (player.hasItem(item.id)) {
                if (mouseInput.didMouseClickOn(item.bounds)) {
                    // deselect if he is clicking on an item he already has selected
                    if (cursorItem == item.id)
                        cursorItem = Item.NO_ITEM;
                    else // set the selected item to the item he clicked
                        cursorItem = item.id;
                }
            }
        }

        // check if the player is trying to use the craftingSlots
        for (int i = 0; i < 2; i++) {
            // the player cannot place items on slot[2] and can only place an item if he has selected one
            if (mouseInput.didMouseClickOn(slotBounds[i]) && cursorItem != Item.NO_ITEM && isValidAddition(i, cursorItem))  {
                //placing an item in a slot
                craftingSlots[i] = player.createItem(cursorItem);
                craftingSlots[i].add(1);

                // if they changed something, then we need to clear the output slot since a new item should appear
                craftingSlots[2] = null;
            }
        }

        // trying to collect the item crafted, let him get if there is an item there
        if (mouseInput.didMouseClickOn(slotBounds[2]) && craftingSlots[2] != null) {
            collectCraftedItem();
        }

        if (craftingSlots[2] == null && craftingSlots[1] != null && craftingSlots[0] != null) {
            // if the player has selected two items selected for crafting then we should mix them and show the item they could get
            craftingSlots[2] = mix(craftingSlots[0], craftingSlots[1]);
            if (craftingSlots[2] != null)
                craftingSlots[2].add(1);
        }

        back.setHovered(mouseInput.isMouseOver(back));
        if (mouseInput.didMouseClickOn(back)) {
            // if the crafting has made the player's current item to be emptied, than we un-assign it
            if (player.getCurrentItem() != Item.NO_ITEM && player.getItem(player.getCurrentItem()).amount <= 0)
                player.setCurrentItem((short)Item.NO_ITEM);
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

        // draw all the craftingSlots
        for (int i = 0; i < craftingSlots.length; i++) {
            g.setColor(craftingSlots[i] == null ? new Color(30,30,30) : Color.gray);
            g.fill(slotBounds[i]);
            if (craftingSlots[i] != null) {
                craftingSlots[i].draw(g, slotBounds[i].x, slotBounds[i].y);
                g.setColor(Color.white);
                g.drawString(Integer.toString(craftingSlots[i].amount), slotBounds[i].x + 25, slotBounds[i].y + 25);
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
            }
            y += 32;
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
        if (isDesiredMixture(first, second, Item.STONE_ID, Item.WOOD_ID)) {
            return new Sword();
        }

        // stone and stone make fire!
        if (isDesiredMixture(first, second, Item.STONE_ID, Item.STONE_ID))
            return new Fire();

        // wood and grass make a bow
        if (isDesiredMixture(first, second, Item.WOOD_ID, Item.GRASS_ID))
            return new Bow();

        // grass and stone make an arrow
        if (isDesiredMixture(first, second, Item.GRASS_ID, Item.STONE_ID))
            return new ArrowItem();

        if (isDesiredMixture(first, second, Item.GRASS_ID, Item.GRASS_ID))
            return new Health();

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
        if (craftingSlots[otherSlot] == null)
            return true;

        // if the item  is different that the other selected item, then it is valid, if they are the same, then the
        // player better have more than one of that item, otherwise it is not valid.
        return craftingSlots[otherSlot].id != itemId || player.getItem(itemId).amount > 1;

    }

    /**
     * Will tell us if the two items mixing are items we want to mix. The user will give the two items to mix, and the
     * ids of what they want to items to be and we will check if they are indeed the items we are looking for
     * @param first the first item to mix
     * @param second the second item to mix
     * @param firstIdDesired the id of the item they want one of the items to be
     * @param secondIdDesired the id of the item they want the other items to be
     * @return whether or not the two items are the items the user wants to mix
     */
    private boolean isDesiredMixture(Item first, Item second, int firstIdDesired, int secondIdDesired) {
        return (first.id == firstIdDesired && second.id == secondIdDesired) || (first.id == secondIdDesired && second.id == firstIdDesired);
    }

    /**
     * When the player has crafted an item out of two items, there will be an item on the third slot where he can pick it
     * before he can, we have to verify some things.
     */
    private void collectCraftedItem() {
        player.addItem(craftingSlots[2].id);

        // crafting takes one of each item
        player.removeItem(craftingSlots[0].id);
        player.removeItem(craftingSlots[1].id);

        if (craftingSlots[0].id == craftingSlots[1].id && player.getItem(craftingSlots[0].id).amount < 2) {
            // if he was two of the same item in each craftingSlots and doesn't have 2 of them anymore, then he can't use both
            craftingSlots[0] = craftingSlots[1] = null;
        }
        else {
            // if the player ran out of items in that slot, so empty it out
            if (player.getItem(craftingSlots[0].id).amount == 0)
                craftingSlots[0] = null;
            if (player.getItem(craftingSlots[1].id).amount == 0)
                craftingSlots[1] = null;
        }

        // empty the slot either way
        craftingSlots[2] = null;

        // reset the current item
        cursorItem = Item.NO_ITEM;
    }
}