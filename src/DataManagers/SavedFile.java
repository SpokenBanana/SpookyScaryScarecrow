package DataManagers;

import Entity.Items.Item;
import Entity.Player.Player;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class contains all the saved data the game needs for a certain saved game. It keeps track of items that have
 * been collected in a HashMap to save temporarily so that all unsaved data remains unsaved. When the player decides to
 * save, it writes all the data in HashMap in it's respective file so we can later retrieve them.
*/
public class SavedFile {
    
    private final String rootPath = "Assets/SavedGames/";
    private String saveFile;

    // we store any info we plan to save here, and once the player decides to save, we write the contents in a text file
    // to restore when the player plays again
    private HashMap<String, ArrayList<Point>> temporaryFiles;


    public SavedFile(){
        // the default location of the directory we want to save these files to
        saveFile = "tmp";
        temporaryFiles = new HashMap<>();
    }
    public SavedFile(String relativePath) {
        saveFile = relativePath;
        temporaryFiles = new HashMap<>();
    }

    public void setSaveFile(String path) {
        saveFile = path;
    }

    /**
     * returns the "gameSave.json" as a JSONObject which holds the data from the previous save
     * @return the saved state of the game
     */
    public Element getSavedGame() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(new File(rootPath + saveFile + "gameSave.xml")).getDocumentElement();
        } catch (Exception e) {
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
        // tmp is used when the player starts a new file, once he decides to save, we place all data we want to save
        // inside another directory because the contents of tmp gets deleted every time we start a game.
        if (saveFile.equals("tmp")) {
            File savedGames = new File(rootPath);
            saveFile = "Saved_Game_" + savedGames.list().length + "/";
            savedGames = new File(rootPath + saveFile);
            savedGames.mkdir();
        }

        Document dom;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            dom = builder.newDocument();
        } catch (Exception e) {
            // save failed, stop here
            System.out.println("Save failed!");
            return;
        }

        Element itemXmlArray = dom.createElement("items");

        // save all the items the player
        for (Item item : player.getItems()) {
            Element itemXml = dom.createElement("item");

            if (item == null) {
                itemXml.setAttribute("amount", "-1");
            }
            else{
                itemXml.setAttribute("amount",Integer.toString(item.amount));
                itemXml.setAttribute("depreciation", Integer.toString(item.getDepreciation()));
            }
            itemXmlArray.appendChild(itemXml);
        }

        // will contain the properties we care about the player
        Element playerXml = dom.createElement("player");

        playerXml.appendChild(createElement("x", Integer.toString(player.getPosition().x), dom));
        playerXml.appendChild(createElement("y", Integer.toString(player.getPosition().y), dom));
        playerXml.appendChild(createElement("health", Integer.toString(player.getHealth()), dom));
        playerXml.appendChild(itemXmlArray);

        // add all the data to the file we want to save as

        playerXml.appendChild(createElement("level", level, dom));
        dom.appendChild(playerXml);

        // save all data from temporaryFiles
        for (String key : temporaryFiles.keySet()) {
            File file = new File(rootPath + saveFile + key + ".txt");
            try {
                Writer writer;

                // it exists, so we want to append it
                if (file.exists())
                    writer = new FileWriter(file, true);
                else // doesn't exists, create it
                    writer = new PrintWriter(file);

                for (Point point : temporaryFiles.get(key))
                    writer.append(point.x + " " + point.y + "\n");
                writer.close();
            } catch (Exception e) {
                System.out.println("Temporary files not saved");
            }
        }

        // they were saved, no longer temporary
        temporaryFiles.clear();

        // now to save the game data
        try{
            // get the xml document as a writable String
            Transformer converter = TransformerFactory.newInstance().newTransformer();
            StreamResult xmlString = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(dom);
            converter.setOutputProperty(OutputKeys.INDENT, "yes");
            converter.transform(source, xmlString);

            // save the data
            File file = new File(rootPath + saveFile + "gameSave.xml");
            PrintWriter writer = new PrintWriter(file);
            writer.write(xmlString.getWriter().toString());
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

        // add the point to the collection associated with the file name.
        if (temporaryFiles.containsKey(filename)) {
            temporaryFiles.get(filename).add(new Point(x, y));
        }
        else{
            ArrayList<Point> points = new ArrayList<>();
            points.add(new Point(x, y));
            temporaryFiles.put(filename, points);
        }
    }

    /**
     * This gets the points of the items we want to ignore
     * @param level the name of the level we are entering
     * @return the points of the items that we want to ignore
     */
    public ArrayList<Point> getItemsToIgnore(String level) {
        // do not want that .json, we want it as a .txt

        ArrayList<Point> itemsToIgnore = new ArrayList<>();

        // get all things from the temporary storage
        if (temporaryFiles.containsKey(level))
            for (Point point : temporaryFiles.get(level))
                itemsToIgnore.add(point);

        // if there is data saved from this level, load it up also
        try{
            Scanner fileReader = new Scanner(new File("Assets/SavedGames/" + saveFile + level + ".txt"));
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

    private Element createElement(String tagName, String content, Document doc) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(content));
        return element;
    }
}
