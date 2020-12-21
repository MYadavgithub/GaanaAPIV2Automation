package config;

public class Constants{

    /**Commonly Used COnstants */

    public static int RESPONSE_TIME = 2;
    public static String CUSTOM_REPORT_FOLDER = "/Reports";

    public static String ANDROID = "Android"; // type = 0
    public static String IOS = "Ios"; // type = 1
    public static String WEB = "Web"; // type = 2

    public static String API_TYPE_SEARCH = "Search";
    public static String API_TYPE_RECO = "Reco";

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
    public final static int REC_INVOCATION_COUNT = 4;
    public final static int AS_INVOCATION_COUNT = 1000;

    /**Emailer Config */
    public static String EMAILER_EMAIL = "umesh.shukla@gaana.com";
    public static String EMAILER_PWD = "lcjzzcwgnzrqbcbiwd"; // pwd 2L
    public static String EMAIL_TO_LIST = "rohit.ranjan@gaana.com";
    public static String EMAIL_CC_LIST = "manish.pandey@gaana.com, radhika.garg@gaana.com";
    public static String EMAIL_BCC_LIST = "";
}
