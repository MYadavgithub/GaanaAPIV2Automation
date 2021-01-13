package config;
import java.util.ArrayList;

public class Constants{

    /**Commonly Used COnstants */

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
    public final static int AS_INVOCATION_COUNT = 25;

    /**Emailer Config */
    public static int EMAILER_ENABLED = 0; // 0 disabled 1 enabled
    public static String EMAILER_EMAIL = "umesh.shukla@gaana.com";
    public static String EMAILER_PWD = "pswagczcaoiybomw";
    public static String EMAIL_TO_LIST = "rohit.ranjan@gaana.com";
    public static String EMAIL_CC_LIST = "manish.pandey@gaana.com, c-abhishek.gupta@gaana.com, umesh.shukla@gaana.com";
    public static String EMAIL_BCC_LIST = "";

    /** Autoqueue New Seed Song Logic Constants */
    public static int SEED_ID = 11;
    public static String ALL_SEED_TRACKS_CSV = "AllSeeds.csv";
    public static String FIVE_SEED_TRACKS_CSV = "FiveSeeds.csv";
    public static final int AUTO_LOGIC_INVOCATION_COUNT = 1;

    public static String[] experiments() {
        String ex[] = { "Experiment_1", "Experiment_2", "Experiment_3", "Default" };
        return ex;
    }

    public static ArrayList<String> testDevicesAutoQueue() {
        ArrayList<String> setOfDevice = new ArrayList<>();
        setOfDevice.add("GM1901_d8f4420a8ba7849e75");
        setOfDevice.add("GM1901_d8f4420a8ba7849e76");
        setOfDevice.add("GM1901_d8f4420a8ba7849e96");
        setOfDevice.add("GM1901_d8f4420a8ba7849e80"); // default logic
        return setOfDevice;
    }
}
