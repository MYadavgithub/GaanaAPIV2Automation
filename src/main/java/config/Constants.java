package config;
import java.util.Arrays;
import java.util.List;

public class Constants{

    /**Commonly Used Constants */
    public static int RESPONSE_TIME = 2;
    public static String CUSTOM_REPORT_FOLDER = "/Reports";

    /**Device Types (0 to 6)*/
    public static String ANDROID = "Android";
    public static String IOS = "Ios";
    public static String Bosch = "Bosch";
    public static String GreatWall = "GreatWall";
    public static String SamsungFridge = "SamsungFridge";
    public static String GaanaWapApp = "WapApp";
    public static String GaanaWebsiteApp = "WebApp";

    /**Api Type */
    public static String API_TYPE_SEARCH = "Search";
    public static String API_TYPE_RECO = "Reco";

    /** Env Type */
    public static String STAGE_ENV = "local";
    public static String PRE_PROD_ENV = "preprod";
    public static String PROD_ENV = "prod";

    /** Test Data read write locations */
    public static String READ_TD_CSV_FROM = "./src/test/resources/data/";
    public static String WRITE_TD_CSV_FROM = "./src/test/resources/savedResponse/";

    /**DB Constants */

    public static int MUSIC_DB = 0;
    public static int MUSIC_X1_DB = 1;
    public static int MUSIC_LOGS_DB = 2;
    public static int MUSIC_GAANA_SVD_DB = 3;
    public static int MUSIC_GAANA_USERS_DB = 4;
    public static int MUSIC_GAANA_FAVOURITE_DB = 5;
    public static int MUSIC_GAANA_RECOMMENDATION_DB = 5;

    /* Recomended Track Constants */
    public final static int REC_INVOCATION_COUNT = 1;
    public final static int AS_INVOCATION_COUNT = 4000;
    public static int IS_STAGE_LIVE = 0;
    public final static int ASV2_INVOCATION_COUNT = 18;

    /**Emailer Config */
    public static int EMAILER_ENABLED = 0; // 0 disabled 1 enabled
    public static String EMAILER_EMAIL = "umesh.shukla@gaana.com";
    public static String EMAILER_PWD = "pswagczcaoiybomw";
    // public static String EMAIL_TO_LIST = "rohit.ranjan@gaana.com";
    // public static String EMAIL_CC_LIST = "manish.pandey@gaana.com, c-abhishek.gupta@gaana.com, umesh.shukla@gaana.com";
    public static String EMAIL_TO_LIST = "rohit.ranjan@gaana.com, ritvik.sharma@gaana.com";
    public static String EMAIL_CC_LIST = "manish.pandey@gaana.com";
    public static String EMAIL_BCC_LIST = "";

    /**
     * Solr Cloud Constants
     */
    public static String COLLECTION = "solrcollection";
    public static final int ZOOKEEPER_INVOCATION_COUNT = 6000;
    public static List<String> zookeeperServers(){
        // {"172.26.11.216:2181", "172.26.11.217:2181", "172.26.11.218:2181"} old servers
        // String arr [] = {"172.26.69.113:2181", "172.26.69.114:2181", "172.26.69.226:2181"}; //stage
        String arr [] = {"172.26.11.136:2181", "172.26.11.141:2181", "172.26.11.237:2181"};
        return Arrays.asList(arr);
    }
}
