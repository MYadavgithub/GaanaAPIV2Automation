package common;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileActions {

    public static Properties prop;
    private static Logger log = LoggerFactory.getLogger(Helper.class);

    /**
     * Traverse data folder and get all list of items.
     * @return
     */
    public static File[] dataFolderTraverse() {
        String path = System.getProperty("user.dir") + "/src/test/resources/data/";
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
        String filepath = System.getProperty("user.dir") + "/src/main/resources/properties/" + filename;
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

    /**
     * Create Directory
     */
     public static String checkFolderExists(String ex_path, String filename){
        File folder_name = new File(ex_path+filename);
        if(!folder_name.isDirectory()){
            log.info("Folder does not exists creating new folder with name : "+filename);
            folder_name.mkdir();
            folder_name.setReadable(true);
            folder_name.setWritable(true);
            folder_name.setExecutable(true);
        }else{
            log.info("Folder already exists with name : "+filename);
        }
        return ex_path+filename;
     }
}