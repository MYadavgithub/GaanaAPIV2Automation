package common;
import java.util.*;
import java.util.Map.Entry;
import config.Constants;
import config.v1.GetProp;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

public class GlobalConfigHandler {

    /**
     * Only for Local debugging
     */
    public static void setLocalProps(){
          System.setProperty("env", "prod");
       System.setProperty("type", "Reco");
        // System.setProperty("type", "Search");
       System.setProperty("device_type", "android");
    }

    /**
     * V1 global config handler and baseurl provider
     * @param prop
     * @return
     */

    public static String baseurl(){
        setLocalProps();
        GetProp prop = new GetProp();
        return prop.baseurl();
    }

    /**
     * Get Reco Url
     * @param prop
     * @return
     */
    public static String getRecoExecUrl(Properties prop) {
        String baseurl = "";
        if(prop != null){
            if(Constants.RECO_BASEURL == 0){
                baseurl = prop.getProperty("reco_baseurl").toString().trim();
            }else if(Constants.RECO_BASEURL == 1){
                baseurl = prop.getProperty("prec_baseurl").toString().trim();
            }else if(Constants.RECO_BASEURL == 2){
                baseurl = prop.getProperty("svd_rec_baseurl").toString().trim();
            }
        }
        return baseurl;
    }

    /**
     * Get Execution Environment
     * @return
     */
    public static String getEnv() {
        String environment = "";
        String env = System.getProperty("env");
        if (env != null) {
            if (env.equalsIgnoreCase("local")) {
                environment = Constants.STAGE_ENV;
            } else if (env.equalsIgnoreCase("preprod")) {
                environment = Constants.PRE_PROD_ENV;
            }else if(env.equalsIgnoreCase("prod")){
                environment = Constants.PROD_ENV;
            }
        } else {
            environment = Constants.STAGE_ENV;
        }
        return environment;
    }

    /**
     * Get API type
     */
    public static String getType() {
        String type = "";
        String env = System.getProperty("type");
        if (env != null) {
            if(env.equalsIgnoreCase("Search")) {
                type = Constants.API_TYPE_SEARCH;
            }else if (env.equalsIgnoreCase("Search_Live")) {
                type = Constants.API_TYPE_LIVE_SEARCH;
            }else if (env.equalsIgnoreCase("Reco")) {
                type = Constants.API_TYPE_RECO;
            }
        } else {
            type = null;
        }
        return type;
    }

    /**
     * Get Device Type default value android.
     */
    public static int getDeviceType() {
        String env = System.getProperty("device_type");
        if (env != null) {
            if (env.equalsIgnoreCase(Constants.ANDROID)) {
                return 0;
            } else if (env.equalsIgnoreCase(Constants.IOS)) {
                return 1;
            } else if (env.equalsIgnoreCase(Constants.Bosch)) {
                return 2;
            } else if (env.equalsIgnoreCase(Constants.GreatWall)) {
                return 3;
            } else if (env.equalsIgnoreCase(Constants.SamsungFridge)) {
                return 4;
            } else if (env.equalsIgnoreCase(Constants.GaanaWapApp)) {
                return 5;
            } else if (env.equalsIgnoreCase(Constants.GaanaWebsiteApp)) {
                return 6;
            }
        }
        return 0;
    }

    public static String todaysDate() {
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(today);
    }

    public int invocationCounter(int counter, int Max) {
        counter++;
        if (counter == Max) {
            counter = 0;
        }
        return counter;
    }

    public static String getAppName(int id) {
        Map<Integer, String> appName = new HashMap<>();
        appName.put(0, Constants.ANDROID);
        appName.put(1, Constants.IOS);
        appName.put(2, Constants.Bosch);
        appName.put(3, Constants.GreatWall);
        appName.put(4, Constants.SamsungFridge);
        appName.put(5, Constants.GaanaWapApp);
        appName.put(6, Constants.GaanaWebsiteApp);

        for (Entry<Integer, String> app : appName.entrySet()) {
            if(app.getKey().equals(id)){
                return appName.get(id);
            }
        }
        return "";
    }

    public static String encodeUrl(String url_to_encode){
        String url = "";
        try {
        	url= URLDecoder.decode(url_to_encode, "UTF-8");
        	url= url.replace(" ", "+");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}
