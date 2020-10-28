package common;
import java.io.File;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.itextpdf.html2pdf.HtmlConverter;


public class FileActions {

    public static Properties prop;
    private static Logger log = LoggerFactory.getLogger(Helper.class);

    /**
     * based on flag delete or validate folder exists or not.
     * @param flag whether needs to delete or not for DELETE = 0, STATUS = 1
     * @param filename
     * @param path file-path
     * @return
     */
    public static boolean fileOperation(int flag, String path, String filename) {
        File directory = new File(path);
        if(directory.exists()){
            File[] directory_files = directory.listFiles();
            for (File file : directory_files) {
                if (file.getName().trim().equals(filename) && flag == 0) {
                    file.delete();
                    return true;
                } else if (flag == 1) {
                    if (file.getName().trim().equals(filename)) {
                        return true;
                    }
                }
            }
        }else{
            log.error("Requested folder not present, please check at path : "+path);
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
    public static String checkFolderExists(String ex_folder_path) {
        File folder_name = new File(ex_folder_path);
        if (!folder_name.isDirectory()) {
            log.info("Folder does not exists creating new folder with name : " + ex_folder_path);
            folder_name.mkdir();
            folder_name.setReadable(true);
            folder_name.setWritable(true);
            folder_name.setExecutable(true);
        } else {
            log.info("Folder already exists with name : " + ex_folder_path);
        }
        return ex_folder_path;
    }

    /**
     * Html to Pdf converter
     */
    public static void createPdf(File file) {
        String file_name_with_path = System.getProperty("user.dir") + "/Reports/EmailerReport.pdf";
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file_name_with_path));
            document.open();
            document.addTitle("GGM Noida Automation Report");
            document.addAuthor("GGM Noida");
            document.addCreator("Umesh Shukla");
            document.addSubject("Automation Reporting");
            document.addCreationDate();
            HtmlConverter.convertToPdf(new FileInputStream(file), new FileOutputStream(file_name_with_path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
    }
}