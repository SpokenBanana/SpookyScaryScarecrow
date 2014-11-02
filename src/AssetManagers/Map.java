package AssetManagers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
    This will take the data from the various JSON files that represent a tiled map and be able
    to turn into a map object. This will allows us to get the objects specified in the JSON files as well
    as be able to render the map it describes.
 */
public class Map {
    private ArrayList<int[][]> layers;
    private JSONObject mapData;
    private final int TILE_SIZE = 32, SOURCE_TILE_SIZE = 32, TILE_COLUMNS = 21;


    // to simply things, we will use only one tilesheet (21x23)
    private BufferedImage tileSheet;

    // the maps have an object group names "enemies" that are named by a number, the number represents an enemy and each
    // number tells the map which monster to spawn there.
    public static final short SHOOTER_ID = 0, GHOST_ID = 1, SKELETON_ID = 2, SLIDER_ID = 3, MINION_ID = 4;

    public Map(String filename) {
        layers = new ArrayList<int[][]>();
        String notFound = "";
        try {
            notFound = filename;
            FileReader reader = new FileReader("Assets/Levels/" + filename);
            mapData = (JSONObject) new JSONParser().parse(reader);
            notFound = "terrain.png";
            tileSheet = ImageIO.read(new File("Assets/terrain.png"));
        } catch (Exception e) {
            System.out.println("File was not found: " + notFound);
        }
        extractLayers();
    }
    /**
        This will draw a layer of the map which the user will specify. The layers start at 0
     */
    public void draw(Graphics2D g, int layer) {
        if (layer < layers.size())
            drawLayer(g, layers.get(layer));
    }
    private void drawLayer(Graphics2D g, int[][] layer) {
        for (int i = 0; i < layer.length; i++) {
            for (int j = 0; j < layer[0].length; j++) {
                if (layer[i][j] == -1) continue;

                // j represents how far along we are in a row, which is our x
                int x = j * TILE_SIZE;

                // i represents how far along we are in a columns, which is our y
                int y = i * TILE_SIZE;

                // we only want to draw one part of the tile sheet (the specified tile) so that means
                // we have to find a way to interpret the data. The tile id corresponds to where the tile is placed
                // on the tile sheet (top left corner = 0, to right of that tile = 1, and so on; left to right,
                // top to bottom. So that means if we take the width of each tile (which are squares) multiply it by
                // the mod of the the id by the amount of tiles in a rows is how far along it is on the x-axis,
                // and the id divided by the amount of rows is how far along it is on the y-axis.
                int sourceX = SOURCE_TILE_SIZE * (layer[i][j] % TILE_COLUMNS);
                int sourceY = SOURCE_TILE_SIZE * (layer[i][j] / TILE_COLUMNS);
                g.drawImage(tileSheet, x, y, x + TILE_SIZE, y + TILE_SIZE,
                            sourceX, sourceY,sourceX + SOURCE_TILE_SIZE, sourceY + SOURCE_TILE_SIZE, null);
            }
        }
    }
    /**
        retrieves an object from the JSON file. Objects in the JSON are meant to represent many things and
        helps make levels easier to lay out.
     */
    public JSONObject getObject(String key) {
        JSONArray objectArray = (JSONArray) mapData.get("layers");
        for (Object object : objectArray) {
            JSONObject objectAsJSON = (JSONObject) object;
            if (objectAsJSON.get("name").equals(key))
                return objectAsJSON;
        }
        return null;
    }

    /**
     * returns the object at the defined key
     * @param key they key to the object
     * @return the object that corresponds to key
     */
    public Object get(String key) {
        return mapData.get(key);
    }
    /**
        This will read the Map object and take all the data from each tile layer and
        parse it so that we get the information we need (the tile id's) in the format we want (ArrayList<int[][]>).
     */
    private void extractLayers() {
        JSONArray objects = (JSONArray) mapData.get("layers");

        // an iterator is sort of a helper that helps us iterate through a collection
        for (Object object1 : objects) {
            JSONObject object = (JSONObject) object1;

            // from the attribute "type" we check if it is a tile layer, if so we add it to layers
            if (object.get("type").equals("tilelayer")) {
                // data contains the tile numbers of the map as they are placed
                JSONArray tileData = (JSONArray) object.get("data");

                // the attributes height and width contain the amount of tiles on each dimension
                int mapHeight = Integer.parseInt(mapData.get("height").toString());
                int mapWidth = Integer.parseInt(mapData.get("width").toString());

                // we will save the data in a 2 dimensional array to simplify rendering
                // because now we can say tiles[0][1] and understand the indexes as coordinates
                int[][] tiles = new int[mapWidth][mapHeight];
                for (int i = 0; i < mapHeight; i++)
                    for (int j = 0; j < mapWidth; j++)
                        tiles[i][j] = Integer.parseInt(tileData.get((i * mapWidth) + j).toString()) - 1;
                layers.add(tiles);
            }
        }
    }
}
