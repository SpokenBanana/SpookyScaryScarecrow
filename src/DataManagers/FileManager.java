package DataManagers;

import java.io.File;

/**
 * This class is in charge of keeping track of files that need to be read and saved during the game.
 */
public class FileManager {

    public FileManager() {

    }

    /**
     * Creates a saved file with the given name, if the directory already exists, we assume the player wants to override
     * the file
     * @param name name of the saved file directory
     */
    public void createSaveDirectory(String name) {
        File newDir = new File("Assets/SavedGames/" + name + "/");

        if (newDir.isDirectory()){
            File[] files = newDir.listFiles();

            // delete all files
            if (files != null)
                for (File file : files)
                    file.delete();

            newDir.delete();
        }

        try {
            newDir.mkdir();
        } catch (Exception e) {}
    }

    public File[] getSavedFiles() {
        return new File("Assets/SavedGames/").listFiles();
    }

}
