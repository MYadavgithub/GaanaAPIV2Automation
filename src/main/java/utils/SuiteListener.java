package utils;
import org.slf4j.*;
import org.testng.IExecutionListener;

import common.GlobalConfigHandler;

public class SuiteListener implements IExecutionListener {

    private static Logger log = LoggerFactory.getLogger(SuiteListener.class);

    public void onExecutionStart(){
        if(GlobalConfigHandler.getDeviceType() == 0){
            log.info("This suite will get executed using ANDROID headers.");
        }else if(GlobalConfigHandler.getDeviceType() == 1){
            log.info("This suite will get executed using IOS headers.");
        }else{
            log.info("Execution context not defined, please manually check for which device type suite getting executed.");
        }
    }

    public void onExecutionFinish() {
        if(System.getProperty("env").equals("stable")){
            Mailer mail = new Mailer();
            mail.sendEmail(null, null, null);
        }
    }
}