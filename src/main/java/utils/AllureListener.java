package utils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import common.GlobalConfigHandler;
import java.util.Properties;
import org.testng.IExecutionListener;

public class AllureListener implements IExecutionListener {
    
    public void onExecutionFinish() {
        Properties prop = new Properties();
        String FILE_PATH = System.getProperty("user.dir") + "/allure-results/environment.properties";
        try{
            OutputStream outputStream = new FileOutputStream(FILE_PATH);
            prop.setProperty("Organization Name", "GGL");
            prop.setProperty("QA Name", "Umesh Shukla");
            prop.setProperty("Os Name", System.getProperty("os.name"));
            prop.setProperty("User Name", System.getProperty("user.name"));
            prop.setProperty("Host Name", InetAddress.getLocalHost().getHostName());
            prop.setProperty("Java Version", System.getProperty("java.version"));
            prop.setProperty("Execution Context", getenv());
            prop.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getenv() {
        if(GlobalConfigHandler.getEnv().equalsIgnoreCase("prod")){
            return "Production";
        }
        return null;
    }
}