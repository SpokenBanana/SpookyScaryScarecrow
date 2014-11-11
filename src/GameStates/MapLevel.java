package GameStates;

import AssetManagers.Map;
import AssetManagers.SoundManager;
import DataManagers.SavedFile;
import Entity.ArcadeMachine;
import Entity.Block;
import Entity.Enemies.*;
import Entity.Items.Item;
import Entity.Items.ItemSpawner;
import Entity.NPC;
import Entity.Player.Player;
import Entity.Player.PlayerHUD;
import Entity.Warp;
import Handlers.KeyInput;
import Handlers.MouseInput;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * MapLevel is the level that uses a map. The main core of this game really.
 */
public class MapLevel extends GameState {

    private Map map;
    private Player player;
    private PlayerHUD playerHUD;
    private ArrayList<Enemy> enemies;
    private ArrayList<Block> blocks;
    private ArrayList<ItemSpawner> itemSpawners;
    private ArrayList<EventState> eventStates;
    private String currentMusic, currentLevel;

    // where the player entered the current level
    private Point playerEnteredLocation;

    // current saved game
    private SavedFile currentGame;

    public MapLevel(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        soundManager = new SoundManager();
        currentGame = new SavedFile();

        blocks = new ArrayList<>();
        eventStates = new ArrayList<>();
        enemies = new ArrayList<>();
        itemSpawners = new ArrayList<>();

        // get sounds
        soundManager.addSound("confirm", "confirm.wav");

        player = new Player(keys, mouseInput);
        setLevel("level1");
        playerHUD = new PlayerHUD(player);
        playerEnteredLocation = player.getPosition().getLocation();
    }

    public MapLevel(GameStateManager manager, KeyInput keys, MouseInput mouse, SavedFile file) {
        this(manager, keys, mouse);
        currentGame = file;
        loadGameFromFile();
    }

    @Override
    public void update() {
        // Space invokes punching, so we see if he facing any enemies, if so, we deal some damage to it
        if (keyInput.isPressed(KeyEvent.VK_SPACE)) {
            for (Enemy enemy : enemies)
                if (enemy.getPosition().intersects(player.getFacingBlock()))
                    player.punch(enemy);
        }
        // pause game
        else if (keyInput.isPressed(KeyEvent.VK_ENTER)) {
            parentManager.addGame(new PauseState(parentManager, keyInput, mouseInput, this));
        }
        else if (keyInput.isPressed(KeyEvent.VK_C)) {
            parentManager.addGame(new CraftingState(parentManager, keyInput, mouseInput, player));
        }

        // event states are triggered by facing the area and pressing the action button [F]
        for (EventState eventState : eventStates) {
            eventState.update(player);

            // we don't want the player to "warp" to a new map by pressing [F] and facing it
            if (!(eventState instanceof Warp) && keyInput.isPressed(KeyEvent.VK_F) && player.getFacingBlock().intersects(eventState.eventArea)) {
                eventState.activate(parentManager, this, player);
            }
            // Warps are an eventState but are triggered when the player walks into it. So we handle that here
            else if (player.getPosition().intersects(eventState.eventArea)){
                eventState.activate(parentManager, this, player);
                playerEnteredLocation = player.getPosition().getLocation();
                break;
            }
        }

        // goes through and checks if the player has hit an item, if so, add it to the players inventory and delete it
        // from the map
        itemSpawners.removeIf(item -> {
            if (item.intersects(player.getPosition())) {
                player.addItem(item.getId());
                currentGame.addToItemIgnore(currentLevel, item.x, item.y);
                return true;
            }
            return false;
        });

        // some blocks are doors so we watch out for that.
        blocks.removeIf(block -> {
            // if the block is a door, the player is touching it, and the player has a key and equipped, then we can destroy it
            if (block.isDoor() && block.intersects(player.getFacingBlock()) && player.getCurrentItem() == Item.KEY_ID) {
                player.useCurrentItem();
                // we will treat the door like an item in that we will remember we opened it and remove it
                currentGame.addToItemIgnore(currentLevel, block.x, block.y);
                return true;
            }
            return false;
        });

        player.update(enemies);
        playerHUD.update(player, mouseInput);

        // update all enemies
        enemies.forEach(enemy -> enemy.update(player));

        // remove any any which is dead
        enemies.removeIf(enemy -> enemy.getHealth() <= 0);

        // GAME OVER player died
        if (player.getHealth() <= 0)
            parentManager.addGame(new GameOverState(parentManager, keyInput, mouseInput, this));
    }

    @Override
    public void draw(Graphics2D g) {
        // draw first two layers
        map.draw(g, 0);
        map.draw(g, 1);

        enemies.forEach(enemy -> enemy.draw(g));
        eventStates.forEach(event -> event.draw(g));

        player.draw(g);
        itemSpawners.forEach(item -> item.draw(g));

        // goes through collection and gets all which isDoor() returns true, then for each one, invokes .draw() to draw it
        blocks.stream().filter(Block::isDoor).forEach(block -> block.draw(g));

        // draw last layer
        map.draw(g, 2);

        // display player stats
        playerHUD.draw(g, player);
    }

    @Override
    public void enter() {
        if (currentMusic != null)
            soundManager.resumeSound(currentMusic, true);
    }

    /**
     * This will load the game from the contents of the currentGame file.
     */
    public void loadGameFromFile() {
        Element saveData = currentGame.getSavedGame();

        // get saved player properties
        Element playerProperties = (Element) saveData.getElementsByTagName("player").item(0);
        Element xProp = (Element) saveData.getElementsByTagName("x").item(0);
        Element yProp = (Element) saveData.getElementsByTagName("y").item(0);
        Element healthProp = (Element) saveData.getElementsByTagName("health").item(0);

        int x = Integer.parseInt(xProp.getTextContent());
        int y = Integer.parseInt(yProp.getTextContent());
        int health = Integer.parseInt(healthProp.getTextContent());

        // move player to saved location
        player.moveTo(x, y);
        player.setHealth(health);

        // give player his items
        NodeList itemArray =  saveData.getElementsByTagName("item");
        for (int i = 0; i < itemArray.getLength(); i++) {
            Element item = (Element) itemArray.item(i);
            if (!item.getAttribute("amount").equals("-1")) {
                int amount = Integer.parseInt(item.getAttribute("amount"));
                if (amount != -1){
                    player.addItem(i, amount);
                    int depreciation = Integer.parseInt(item.getAttribute("depreciation"));
                    player.getItem(i).setDepreciation(depreciation);
                }
            }
        }

        // set level to where it was last saved
        Element levelElement = (Element) saveData.getElementsByTagName("level").item(0);
        setLevel(levelElement.getTextContent());
    }

    public void saveGame() {
        currentGame.saveFile(player, currentLevel);
    }


    /**
     * Moves the player back to the location he entered the level in and restores health
     */
    public void resetLevel() {
        player.moveTo(playerEnteredLocation.x, playerEnteredLocation.y);
        player.resetStats();
        // reload the level
        setLevel(currentLevel);
    }

    /**w
     * Changes the current level to the level given. It loads all the things the file specifies it wants in that map
     * @param level the name of the .json file inside Assets/Levels/ to load
     */
    public void setLevel(String level) {
        currentLevel = level;
        map = new Map(level);
        player.bullets.clear();

        // new level, so forget about the last one
        eventStates.clear();
        blocks.clear();
        enemies.clear();
        itemSpawners.clear();

        // some maps specify a song they want to play in the background, we load that song here
        String music = getMusic();

        // when maps do not use it, we don't change anything, same thing when it wants to play a song that is already playings
        if (music != null && !music.equals(currentMusic)) {
            if (currentMusic != null)
                soundManager.deleteSound(currentMusic);
            currentMusic = music;
            soundManager.addSound(currentMusic, currentMusic);
            soundManager.playSound(currentMusic, true);
        }

        // get properties we want into the game
        extractBlocks();
        extractEnemies();
        extractItems();

        // we finished getting all enemies, so let the minions know that we are done giving them paths.
        enemies.stream().filter(enemy -> enemy instanceof Minion).forEach(enemy -> ((Minion) enemy).finishAddingPaths());

        extractNPC();
        extractSpeech();
        extractWarps();

        // some items were already picked up, so don't load them again
        removeItemsToIgnore(level);

        // only the arcade map wants the arcade machines
        if (level.equals("arcade")) {
            System.out.println("wut");
            extractArcadeMachines();
        }


        // let the player know about the collision blocks
        player.setBlocks(blocks);
    }

    /**
     * Gets all the items that we have already collected in the level and removes them so we don't always load up the
     * item even after it was picked up
     */
    private void removeItemsToIgnore(String level) {
        // the test to determine if the item should be ignored
        itemSpawners.removeIf(item -> shouldBeIgnored(item.getLocation(), level));

        // blocks contains some doors we want to remember we already opened too
        blocks.removeIf(block -> shouldBeIgnored(block.getLocation(), level));
    }

    /**
     * Determines based on whether or not the location has been stored in currentGame to load or not.
     * @param item the location of the item
     * @param level the name of the level
     * @return whether or not the item should be ignored
     */
    private boolean shouldBeIgnored(Point item, String level){
        for (Point point : currentGame.getItemsToIgnore(level))
            if (point.equals(item))
                return true; // do delete

        return false; // do not
    }

    /**
     * Some maps have a special song they want to play in the background, here is where we check if a map does, if so,
     * we return the song it wants to play
     * @return the path of the song desired to play
     */
    private String getMusic() {
        String file = null;
        NodeList props = map.get("property");
        for (int i = 0; i < props.getLength(); i++) {
            Element property = (Element) props.item(i);
            if (property.getAttribute("name").equals("music"))
                return property.getAttribute("value");
        }
        return null;
    }

    /**
     * All of these "extract[x]" do relatively the same thing, they go through the map object and check if the map has
     * the property [x], if so, it loads it into the game as its respective object so it now becomes meaningful.
     */
    private void extractItems() {
        NodeList itemObject = map.getObject("items");
        // not all maps have items, if they do not, then stop here
        if (itemObject == null)
            return;
        // get all the objects as an array

        // go through each one and convert it into an ItemSpawner
        for (int i = 0; i < itemObject.getLength(); i++) {
            Element item = (Element) itemObject.item(i);
            int id = Integer.parseInt(item.getAttribute("name"));
            Rectangle bounds = convertElementToRectangle(item);
            itemSpawners.add(new ItemSpawner(id, bounds));
        }
    }
    private void extractWarps() {
        NodeList warpObject = map.getObject("warps");
        if (warpObject == null)
            return;
        for (int i = 0; i < warpObject.getLength(); i++) {
            Element warp = (Element) warpObject.item(i);
            eventStates.add(new Warp(warp.getAttribute("name"), convertElementToRectangle(warp)));
        }
    }
    private void extractBlocks() {
        // I named the objects I want as walls "walls" in the JSON files.
        NodeList wallObjects = map.getObject("walls");

        // this map has no walls, no need to continue
        if (wallObjects == null)
            return;

        // objects contain an array of objects defined under the "objects" property.

        for (int i = 0; i < wallObjects.getLength(); i++) {
            Element wall = (Element) wallObjects.item(i);

            // we convert the object to a JSONObject to retrieve the properties we want.
            Block block = new Block(convertElementToRectangle(wall));

            // if this property even exists, we know it was meant to be a door
            NodeList properties = wall.getElementsByTagName("properties");
            if (properties != null) {
                Element property = (Element) properties.item(0);
                if (property != null){
                    Element prop = (Element) property.getElementsByTagName("property").item(0);
                    String isDoor = prop.getAttribute("name");
                    if (isDoor!= null && isDoor.equals("isDoor"))
                        block.setAsDoor();
                }
            }

            blocks.add(block);
        }
    }
    private void extractArcadeMachines() {

        NodeList arcades = map.getObject("arcades");
        if (arcades == null){
            System.out.println("none found..");
            return;
        }

        for (int i = 0; i < arcades.getLength(); i++) {
            Element arcade = (Element) arcades.item(i);
            int gameId = Integer.parseInt(arcade.getAttribute("name"));
            char direction = arcade.getAttribute("type").charAt(0);
            Rectangle asBlock = convertElementToRectangle(arcade);
            blocks.add(new Block(asBlock));
            eventStates.add(new ArcadeMachine(asBlock, gameId, direction));
        }
    }

    /**
     * Most of these objects can be used as a rectangle, like blocks, NPC, arcade machines etc. and these things
     * contain data that can be used to construct a rectangle object. (x, y, width, height) so we just get that
     * data and use it to create a rectangle object.
     *
     * @param toRectangle the object we want to transform as a rectangle
     * @return rectangle from the data given from the JSONObject
     */
    private Rectangle convertElementToRectangle(Element toRectangle) {
        // JSONObject's get() method returns the value in a generic Object-type. We expect the
        // property "x" to contain a numerical value, so to convert it to a numerical value we must
        // first convert it to a string with Object's toString() method and then we can convert it back
        // to an integer with Integer.parseInt(). We do this for all properties we expect a numerical value.
        int x = Integer.parseInt(toRectangle.getAttribute("x"));
        int y = Integer.parseInt(toRectangle.getAttribute("y"));
        int width = Integer.parseInt(toRectangle.getAttribute("width"));
        int height = Integer.parseInt(toRectangle.getAttribute("height"));
        return new Rectangle(x, y, width, height);
    }
    private void extractSpeech(){
        NodeList speechObject = map.getObject("speech");
        if (speechObject == null)
            return;

        for (int i = 0; i < speechObject.getLength(); i++) {
            Element speech = (Element) speechObject.item(i);
            String conversation = speech.getAttribute("name");

            // get the EventState ready
            Rectangle eventArea = convertElementToRectangle(speech);
            EventState state = new ConversationEvent(conversation, keyInput, eventArea);

            // now check if this was supposed to be something else
            // because not all "speech" has a type
            if (speech.hasAttribute("type")) {
                String strType = speech.getAttribute("type");

                // this tells the game that we want this speech to be a HealthEvent speech
                if (strType.equals("health"))
                    state = new HealthEvent(eventArea);
            }

            // add it into the game
            eventStates.add(state);
        }
    }
    private void extractNPC() {
        NodeList npcObject = map.getObject("npc");
        if (npcObject == null) return;

        for (int i = 0; i < npcObject.getLength(); i++) {
            Element npc = (Element) npcObject.item(i);
            String conversation = npc.getAttribute("name");
            Rectangle bounds = convertElementToRectangle(npc);
            blocks.add(new Block(bounds));
            eventStates.add(new NPC(bounds.getLocation(), conversation));
        }
    }
    private void extractEnemies() {
        NodeList enemyObject = map.getObject("enemies");
        if (enemyObject == null)
            return;
        for (int i = 0; i < enemyObject.getLength(); i++) {
            Element enemyElement = (Element) enemyObject.item(i);
            int enemyID = Integer.parseInt(enemyElement.getAttribute("name"));
            Rectangle bounds = convertElementToRectangle(enemyElement);

            // enemyID tells us which enemy we should add to the game
            Enemy enemy = null;
            switch (enemyID) {
                case Map.SHOOTER_ID:
                    enemy = new Shooter(bounds.getLocation());
                    break;
                case Map.GHOST_ID:
                    enemy = new Ghost(bounds.getLocation());
                    break;
                case Map.SKELETON_ID:
                    enemy = new Skeleton(bounds.getLocation());
                    break;
                case Map.SLIDER_ID:
                    enemy = new Slider(bounds.getLocation());
                    break;
                case Map.MINION_ID:
                    // type holds the id of the minion
                    String id = enemyElement.getAttribute("type");

                    // a flag to let us know whether or not the enemy with the id exists already
                    boolean found = false;

                    // find the minion with the id "id"
                    for (Enemy element : enemies) {
                        if (element instanceof Minion && ((Minion) element).getId().equals(id)) {
                            ((Minion) element).addPath(bounds.getLocation());
                            found = true;
                        }
                    }

                    // if we did not find one in current list, then we just make a new Minion
                    if (!found){
                        Minion minion = new Minion(bounds.getLocation(), id);
                        minion.setBlocks(blocks);
                        enemies.add(minion);
                    }
                    break;
            }
            // give them collision blocks
            if (enemy != null){
                enemy.setBlocks(blocks);
                enemies.add(enemy);
            }
        }
    }
}