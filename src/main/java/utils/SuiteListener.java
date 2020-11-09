package utils;
import org.testng.IExecutionListener;


public class SuiteListener implements IExecutionListener {

    public void onExecutionFinish() {
        if(System.getProperty("env").equals("stable")){
            Mailer mail = new Mailer();
            mail.sendEmail(null, null);
        }
    }
}