package DataManagers;

import java.io.File;

/**
 * This class is in charge of keeping track of files that need to be read and saved during the game.
 */
public class FileManager {
    /**
     * Creates a saved file with the given name, if the directory already exists, we assume the player wants to override
     * the file
     * @param name name of the saved file directory
     */
    public void createSaveDirectory(String name) {
        File newDir = new File("Assets/SavedGames/" + name + "/");

        // if there is already a file with the same name, just overwrite it.
        if (newDir.isDirectory()){
            File[] files = newDir.listFiles();

            // delete all files and it is good as new!
            if (files != null)
                for (File file : files)
                    file.delete();
        }
        else // directory doesn't exists, so create it!
            newDir.mkdir();
    }

    /**
     * Gives a list of all the files in the SavedGames directory which holds all the saved game data
     * @return List of all saved game files
     */
    public File[] getSavedFiles() {
        File file = new File("Assets/SavedGames/");

        // if the directory doesn't exist, create it
        if (!file.exists())
            file.mkdir();

        return file.listFiles();
    }

}
