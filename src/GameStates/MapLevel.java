package GameStates;

import AssetManagers.Map;
import AssetManagers.SoundManager;
import DataManagers.SavedFile;
import Entity.ArcadeMachine;
import Entity.Block;
import Entity.Enemies.*;
import Entity.Items.ItemSpawner;
import Entity.NPC;
import Entity.Player.Player;
import Entity.Player.PlayerHUD;
import Entity.Warp;
import Handlers.KeyInput;
import Handlers.MouseInput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

        player = new Player(keys);
        setLevel("route2.json");
        playerHUD = new PlayerHUD(player);
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
        else if (keyInput.isPressed(KeyEvent.VK_ENTER)) {
            parentManager.addGame(new PauseState(parentManager, keyInput, mouseInput, this));
        }

        // event states are triggered by facing the area and pressing the action button [F]
        for (EventState eventState : eventStates) {
            // we don't want the player to "warp" to a new map by pressing [F] and facing it
            if (!(eventState instanceof Warp) && keyInput.isPressed(KeyEvent.VK_F) && player.getFacingBlock().intersects(eventState.eventArea)) {
                eventState.activate(parentManager, this, player);
            }
            // Warps are an eventState but are triggered when the player walks into it. So we handle that here
            else if (player.getPosition().intersects(eventState.eventArea)){
                eventState.activate(parentManager, this, player);
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

        player.update();
        playerHUD.update(player, mouseInput);

        // update all enemies
        enemies.forEach(enemy -> enemy.update(player));

        // remove any any which is dead
        enemies.removeIf(enemy -> enemy.getHealth() <= 0);
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
        JSONObject saveData = currentGame.getSavedGame();
        JSONObject playerProperties = (JSONObject) saveData.get("player");
        int x = Integer.parseInt(playerProperties.get("x").toString());
        int y = Integer.parseInt(playerProperties.get("y").toString());
        int health = Integer.parseInt(playerProperties.get("health").toString());

        player.moveTo(x, y);
        player.setHealth(health);
        JSONArray itemArray = (JSONArray) playerProperties.get("items");
        for (int i = 0; i < itemArray.size(); i++) {
            if (!itemArray.get(i).toString().equals("-1")) {
                int amount = Integer.parseInt(itemArray.get(i).toString());
                player.addItem(i, amount);
            }
        }
        setLevel(saveData.get("level").toString());
    }

    public void saveGame() {
        currentGame.saveFile(player, currentLevel);
    }

    /**
     * Changes the current level to the level given
     * @param level the name of the .json file inside Assets/Levels/ to load
     */
    public void setLevel(String level) {
        currentLevel = level;
        map = new Map(level);

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

        removeItemsToIgnore(level);

        // only the arcade map wants the arcade machines
        if (level.equals("arcade.json"))
            extractArcadeMachines();


        // let the player know about the collision blocks
        player.setBlocks(blocks);
    }

    /**
     * Gets all the items that we have already collected in the level and removes them so we don't always load up the
     * item even after it was picked up
     */
    protected void removeItemsToIgnore(String level) {
        itemSpawners.removeIf(item -> {
            // currentGame keeps track of items we have picked up, we know if we picked up an item if the location
            // from currentGame matches the item's location since only one item can take up a certain location at a time
            for (Point point : currentGame.getItemsToIgnore(level)) {
                if (point.x == item.x & point.y == item.y) {
                    // do delete
                    return true;
                }
            }
            // do not delete
            return false;
        });
    }

    /**
     * Some maps have a special song they want to play in the background, here is where we check if a map does, if so,
     * we return the song it wants to play
     * @return the path of the song desired to play
     */
    protected String getMusic() {
        String file = null;
        try {
            JSONObject properties = (JSONObject) map.get("properties");
            file = properties.get("music").toString();
        } catch (Exception e) {}
        return file;
    }

    /**
     * All of these "extract[x]" do relatively the same thing, they go through the map object and check if the map has
     * the property [x], if so, it loads it into the game as its respective object so it now becomes meaningful.
     */
    protected void extractItems() {
        JSONObject itemObject = map.getObject("items");
        // not all maps have items, if they do not, then stop here
        if (itemObject == null)
            return;
        // get all the objects as an array
        JSONArray itemArray = (JSONArray) itemObject.get("objects");

        // go through each one and convert it into an ItemSpawner
        for (Object item: itemArray) {
            JSONObject itemAsJSON = (JSONObject) item;
            int id = Integer.parseInt(itemAsJSON.get("name").toString());
            Rectangle bounds = convertJSONToRectangle(itemAsJSON);
            itemSpawners.add(new ItemSpawner(id, bounds));
        }
    }
    protected void extractWarps() {
        JSONObject warpObject = map.getObject("warps");
        if (warpObject == null)
            return;
        JSONArray warpArray = (JSONArray) warpObject.get("objects");
        for (Object warp : warpArray) {
            JSONObject warpAsJSON = (JSONObject) warp;
            eventStates.add(new Warp(warpAsJSON.get("name").toString(), convertJSONToRectangle(warpAsJSON)));
        }
    }
    protected void extractBlocks() {
        // I named the objects I want as walls "walls" in the JSON files.
        JSONObject wallObjects = map.getObject("walls");

        // this map has no walls, no need to continue
        if (wallObjects == null)
            return;

        // objects contain an array of objects defined under the "objects" property.
        JSONArray wallArray = (JSONArray) wallObjects.get("objects");

        for (Object wall : wallArray) {
            // we convert the object to a JSONObject to retrieve the properties we want.
            JSONObject wallAsJson = (JSONObject) wall;

            blocks.add(new Block(convertJSONToRectangle(wallAsJson)));
        }
    }
    protected void extractArcadeMachines() {
        JSONObject arcadeObject = map.getObject("arcades");
        if (arcadeObject == null)
            return;
        JSONArray arcadeArray = (JSONArray) arcadeObject.get("objects");
        for (Object arcade : arcadeArray) {
            JSONObject arcadeAsJSON = (JSONObject) arcade;

            int gameId = Integer.parseInt(arcadeAsJSON.get("name").toString());
            char direction = arcadeAsJSON.get("type").toString().charAt(0);

            blocks.add(new Block(convertJSONToRectangle(arcadeAsJSON)));
            eventStates.add(new ArcadeMachine(convertJSONToRectangle(arcadeAsJSON), gameId, direction));
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
    protected Rectangle convertJSONToRectangle(JSONObject toRectangle) {
        // JSONObject's get() method returns the value in a generic Object-type. We expect the
        // property "x" to contain a numerical value, so to convert it to a numerical value we must
        // first convert it to a string with Object's toString() method and then we can convert it back
        // to an integer with Integer.parseInt(). We do this for all properties we expect a numerical value.
        int x = Integer.parseInt(toRectangle.get("x").toString());
        int y = Integer.parseInt(toRectangle.get("y").toString());
        int width = Integer.parseInt(toRectangle.get("width").toString());
        int height = Integer.parseInt(toRectangle.get("height").toString());
        return new Rectangle(x, y, width, height);
    }
    protected void extractSpeech(){
        JSONObject speechObject = map.getObject("speech");
        if (speechObject == null)
            return;

        JSONArray speechArray = (JSONArray) speechObject.get("objects");
        for (Object speech : speechArray) {
            JSONObject speechAsJSON = (JSONObject) speech;
            String conversation = speechAsJSON.get("name").toString();
            eventStates.add(new ConversationEvent(conversation, keyInput, convertJSONToRectangle(speechAsJSON)));
        }
    }
    protected void extractNPC() {
        JSONObject npcObject = map.getObject("npc");
        if (npcObject == null) return;
        JSONArray npcArray = (JSONArray) npcObject.get("objects");
        for (Object npc : npcArray) {
            JSONObject npcAsJSON = (JSONObject) npc;
            String conversation = npcAsJSON.get("name").toString();
            Rectangle bounds = convertJSONToRectangle(npcAsJSON);
            blocks.add(new Block(bounds));
            eventStates.add(new NPC(bounds.getLocation(), conversation));
        }
    }
    protected void extractEnemies() {
        JSONObject enemyObject = map.getObject("enemies");
        if (enemyObject == null)
            return;
        JSONArray enemyArray = (JSONArray) enemyObject.get("objects");
        for (Object enemy : enemyArray) {
            JSONObject enemyAsJSON = (JSONObject) enemy;
            int enemyID = Integer.parseInt(enemyAsJSON.get("name").toString());
            Rectangle bounds = convertJSONToRectangle(enemyAsJSON);

            // enemyID tells us which enemy we should add to the game
            switch (enemyID) {
                case Map.SHOOTER_ID:
                    enemies.add(new Shooter(bounds.getLocation()));
                    break;
                case Map.GHOST_ID:
                    enemies.add(new Ghost(bounds.getLocation()));
                    break;
                case Map.SKELETON_ID:
                    enemies.add(new Skeleton(bounds.getLocation()));
                    break;
                case Map.SLIDER_ID:
                    enemies.add(new Slider(bounds.getLocation()));
                    break;
                case Map.MINION_ID:
                    // type holds the id of the minion
                    String id = enemyAsJSON.get("type").toString();

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
                    if (!found)
                        enemies.add(new Minion(bounds.getLocation(), id));

                    break;
            }
        }
    }
}