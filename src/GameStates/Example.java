package GameStates;

import AssetManagers.Map;
import Entity.Block;
import Entity.Enemies.Enemy;
import Entity.Enemies.Ghost;
import Entity.Enemies.Shooter;
import Entity.Enemies.Skeleton;
import Entity.Player.Player;
import Entity.Player.PlayerHUD;
import Handlers.KeyInput;
import Handlers.MouseInput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Example extends GameState {
    private Map map;
    private Player player;
    private PlayerHUD playerHUD;

    // we keep a generic list of all enemies in the level
    private ArrayList<Enemy> enemies;

    // blocks are sort of like the walls of the game, the player cannot pass through them
    private ArrayList<Block> blocks;

    // some objects can trigger a conversation, this will hold all the conversations that can be triggered in a level
    private ArrayList<ConversationEvent> conversationEvents;

    public Example(GameStateManager gameStateManager, KeyInput keyInput, MouseInput mouse) {
        super(gameStateManager, keyInput, mouse);
        map = new Map("start.json");
        player = new Player(this.keyInput);
        blocks = new ArrayList<Block>();
        conversationEvents = new ArrayList<ConversationEvent>();
        extractBlocks();
        extractSpeech();
        player.setBlocks(blocks);
        enemies = new ArrayList<Enemy>();
        enemies.add(new Skeleton(new Point(480,480)));
        enemies.add(new Ghost(new Point(320,320)));
        enemies.add(new Shooter(new Point(240,480)));
        playerHUD = new PlayerHUD(player);
    }
    protected void extractBlocks() {
        // I named the objects I want as walls "walls" in the JSON files.
        JSONObject wallObjects = map.getObject("walls");

        // objects contain an array of objects defined under the "objects" property.
        JSONArray wallArray = (JSONArray) wallObjects.get("objects");

        for (Object wall : wallArray) {
            // we convert the object to a JSONObject to retrieve the properties we want.
            JSONObject wallAsJson = (JSONObject) wall;

            blocks.add(new Block(convertJSONToRectangle(wallAsJson)));
        }
    }
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
        JSONObject speechObject = (JSONObject) map.getObject("speech");
        JSONArray speechArray = (JSONArray) speechObject.get("objects");
        for (Object speech : speechArray) {
            JSONObject speechAsJSON = (JSONObject) speech;
            String converstaion = speechAsJSON.get("name").toString();
            conversationEvents.add(new ConversationEvent(converstaion, keyInput, convertJSONToRectangle(speechAsJSON)));
        }
    }
    @Override
    public void update() {
        if (keyInput.isPressed(KeyEvent.VK_F)){
            Rectangle facingArea = player.getFacingBlock();
            for (ConversationEvent conversationEvent : conversationEvents) {
                if (conversationEvent.eventArea.intersects(facingArea))
                    conversationEvent.activate(parentManager, this, player);
            }
        }
        for (Enemy enemy : enemies)
            enemy.update(player);
        player.update();
    }

    @Override
    public void draw(Graphics2D g) {
        map.draw(g, 0);
        map.draw(g, 1);

        for (Enemy enemy : enemies)
            enemy.draw(g);
        player.draw(g);

        map.draw(g, 2);

        playerHUD.draw(g, player);
    }
}
