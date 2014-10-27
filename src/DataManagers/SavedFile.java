package DataManagers;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/*
    Takes care of saved files, retrieving them and saving them.
*/
public class SavedFile {
    
    protected final String rootPath = "Assets/SavedGames/";
    protected String relativeFilePath;
    protected JSONObject fileAsJson;

    protected ArrayList<int[][]> layers;
    protected ArrayList<JSONObject> objects;
    
    protected SavedFile(String relativePath) {
        fileAsJson = new JSONObject();
        relativeFilePath = relativePath;
    }

    
    public void saveFile() {
        
    }
    
    
}
