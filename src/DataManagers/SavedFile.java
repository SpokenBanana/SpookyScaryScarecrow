package DataManagers;

import Entity.Items.Item;
import Entity.Player.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
    Takes care of saved files, retrieving them and saving them.
*/
public class SavedFile {
    
    private final String rootPath = "Assets/SavedGames/";
    private String saveFile;
    private JSONObject savedGame;

    public SavedFile(){
        // the default location of the directory we want to save these files to
        saveFile = "tmp";
    }
    public SavedFile(String relativePath) {
        saveFile = relativePath;
    }

    public void setSaveFile(String path) {
        saveFile = path;
    }

    /**
     * returns the "gameSave.json" as a JSONObject which holds the data from the previous save
     * @return the saved state of the game
     */
    public JSONObject getSavedGame() {
        try {
            return (JSONObject) new JSONParser().parse(new FileReader(rootPath + saveFile + "gameSave.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * When saving, we usually just care about the player's properties and the level he is currently in. So this
     * saves all that data in a file we can get later when the player decides to load the file
     * @param player player in the game we want to save
     * @param level the name of the level we want to save
     */
    public void saveFile(Player player, String level) {
        JSONObject gameToSave = new JSONObject();

        JSONArray itemArray = new JSONArray();
        for (Item item : player.getItems()) {
            if (item == null)
                itemArray.add("-1");
            else
                itemArray.add(Integer.toString(item.amount));
        }

        // will contain the properties we care about the player
        JSONObject playerObject = new JSONObject();
        playerObject.put("x", Integer.toString(player.getPosition().x));
        playerObject.put("y", Integer.toString(player.getPosition().y));
        playerObject.put("health", Integer.toString(player.getHealth()));
        playerObject.put("items", itemArray);

        gameToSave.put("player", playerObject);
        gameToSave.put("level", level);

        try{
            if (saveFile.equals("tmp")) {
                File savedGames = new File(rootPath);
                saveFile = "Saved_Game_" + savedGames.list().length + "/";
                savedGames = new File(rootPath + saveFile);
                savedGames.mkdir();
            }
            File file = new File(rootPath + saveFile + "gameSave.json");
            PrintWriter writer = new PrintWriter(file);
            writer.write(gameToSave.toJSONString());
            writer.close();
        } catch (Exception e) {
            System.out.println("failed to save");
        }

    }

    /**
     * Adds the item location of the item we want to ignore in the file. This is so when the player re-enters the level,
     * the items the player has already collected will not show up again
     * @param filename name of the level currently in
     * @param x x position of the item
     * @param y y position of the item
     */
    public void addToItemIgnore(String filename, int x, int y) {
        // do not want that .json, we want it as a .txt
        if (filename.endsWith(".json")){
            filename = filename.substring(0, filename.indexOf(".json"));
        }

        File save = new File(rootPath + saveFile + filename + ".txt");
        try{
            Writer write = new FileWriter(save);
            write.append(x + " " +  y);
            write.close();
        }catch (Exception e) {}
    }

    /**
     * This gets the points of the items we want to ignore
     * @param level the name of the level we are entering
     * @return the points of the items that we want to ignore
     */
    public ArrayList<Point> getItemsToIgnore(String level) {
        // do not want that .json, we want it as a .txt
        if (level.endsWith(".json")){
            level = level.substring(0, level.indexOf(".json"));
            level += ".txt";
        }
        ArrayList<Point> itemsToIgnore = new ArrayList<>();
        try{
            Scanner fileReader = new Scanner(new File("Assets/SavedGames/" + saveFile + level));
            while (fileReader.hasNext()){
                String[] points = fileReader.nextLine().split(" ");
                int x = Integer.parseInt(points[0]);
                int y = Integer.parseInt(points[1]);
                itemsToIgnore.add(new Point(x, y));
            }
            fileReader.close();
        } catch (Exception e) {
        }
        return itemsToIgnore;
    }
}
