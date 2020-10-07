package common;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class FileActions {

    public static Properties prop;
    /**
     * Traverse data folder and get all list of items.
     * @return
     */
    public static File[] dataFolderTraverse() {
        String path = System.getProperty("user.dir") + "/resources/data/";
        File dir = new File(path);
        File[] dirList = dir.listFiles();
        return dirList;
    }

    /**
     * based on flag delete or validate folder exists or not.
     * @param flag
     * @param filename
     * @return
     */
    public boolean fileOperation(int flag, String filename) {
        File[] filelist = dataFolderTraverse();
        for (File file : filelist) {
            if (file.getName().trim().equals(filename) && flag == 0) {
                file.delete();
                return true;
            } else if (flag == 1) {
                if (file.getName().trim().equals(filename)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Read Prop file
     */
    public static Properties readProp(String filename) {
        String filepath = System.getProperty("user.dir") + "/resources/properties/" + filename;
        try {
            FileInputStream file = new FileInputStream(filepath);
            prop = new Properties();
            prop.load(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}