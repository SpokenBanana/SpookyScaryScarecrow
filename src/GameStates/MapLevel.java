package GameStates;

import AssetManagers.Map;
import AssetManagers.SoundManager;
import Entity.ArcadeMachine;
import Entity.Block;
import Entity.Enemies.*;
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

    protected Map map;
    protected Player player;
    protected PlayerHUD playerHUD;
    protected ArrayList<Enemy> enemies;
    protected ArrayList<Block> blocks;
    protected ArrayList<EventState> eventStates;
    protected SoundManager sounds;

    public MapLevel(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        sounds = new SoundManager();
        blocks = new ArrayList<>();
        sounds.addSound("confirm", "confirm.wav");
        player = new Player(keys);

        setLevel("arcade.json");
        playerHUD = new PlayerHUD(player);
    }

    @Override
    public void update() {
        for (EventState eventState : eventStates) {
            if (keyInput.isPressed(KeyEvent.VK_F) && player.getFacingBlock().intersects(eventState.eventArea)) {
                eventState.activate(parentManager, this, player);
            }
            else if (eventState instanceof Warp && player.getPosition().intersects(eventState.eventArea))
                eventState.activate(parentManager, this, player);
        }
        player.update();

        enemies.forEach(enemy -> enemy.update(player));
    }

    @Override
    public void draw(Graphics2D g) {
        map.draw(g, 0);
        map.draw(g, 1);

        enemies.forEach(enemy -> enemy.draw(g));
        eventStates.forEach(event -> event.draw(g));

        player.draw(g);

        map.draw(g, 2);

        playerHUD.draw(g, player);
    }

    public void setLevel(String level) {
        map = new Map(level);
        eventStates = new ArrayList<EventState>();
        blocks.clear();
        extractBlocks();
        extractEnemies();
        extractNPC();
        extractSpeech();
        extractWarps();
        if (level.equals("arcade.json"))
            extractArcadeMachines();

        player.setBlocks(blocks);
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
        if (wallObjects == null)
            return;
        blocks = new ArrayList<Block>();

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
        JSONObject npcObject = (JSONObject) map.getObject("npc");
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
        enemies = new ArrayList<Enemy>();
        JSONObject enemyObject = (JSONObject) map.getObject("enemies");
        if (enemyObject == null)
            return;
        JSONArray enemyArray = (JSONArray) enemyObject.get("objects");
        for (Object enemy : enemyArray) {
            JSONObject enemyAsJSON = (JSONObject) enemy;
            int enemyID = Integer.parseInt(enemyAsJSON.get("name").toString());
            Rectangle bounds = convertJSONToRectangle(enemyAsJSON);
            switch (enemyID) {
                case Map.SHOOTER_ID: // shooter ID
                    enemies.add(new Shooter(bounds.getLocation()));
                    break;
                case Map.GHOST_ID: // Ghost ID
                    enemies.add(new Ghost(bounds.getLocation()));
                    break;
                case Map.SKELETON_ID: // Skeleton ID
                    enemies.add(new Skeleton(bounds.getLocation()));
                    break;
                case Map.SLIDER_ID:
                    enemies.add(new Slider(bounds.getLocation()));
                    break;
            }
        }
    }
}