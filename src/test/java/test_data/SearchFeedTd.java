package test_data;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Umesh Shukla
 */
public class SearchFeedTd {

    public static String podcast = "-4"; // to segregate podcast call in TC-> 1
    public static String default_tab_id = "-1";
    public static String tabs[] = { "default", "-1", "-2", "-4", "-5", "-6", "100", "103", "104", "1389"}; // do not change order else manage TC-> 1
    public static final int INVOCATION_COUNT = 10; // increate is according to tabs length
    private static String query_params = "?geoLocation=IN&usrLang=";
    public static String SECTION_DEFAULT_ENTITY_DESCRIPTION = "Recommended";
    public static String SECTION_VIEW_TYPE = "feed_horz_scroll";
    public static String SECTION_URL_SEE_ALL = "https://rec.gaana.com/searchfeed/fetch?tabSelected=-1";

    /**
     * To get correct tab name call this function by passing tab_id
     * @param val
     * @return
     */
    public static String tabsName(String val) {
        Map<String, String> tabNames = new HashMap<>();
        tabNames.put("default", "Recommended");
        tabNames.put("-1", "Recommended");
        tabNames.put("-2", "Popular");
        // tabNames.put("-3", "HotShots");
        tabNames.put("-4", "Podcasts");
        tabNames.put("-5", "Radio");
        tabNames.put("-6", "Trending");
        tabNames.put("100", "Party");
        tabNames.put("103", "Romance");
        tabNames.put("104", "Sad");
        tabNames.put("1389", "90s");

        for (String name : tabNames.keySet()) {
            if (name.equals(val)) {
                return tabNames.get(val).toString().trim();
            }
        }
        return null;
    }

    /**
     * Expected all response keys in Search Feed api
     */
    public static String expectedResponseKeys[] = { "iid", "ti", "ty", "aw", "play_ct", "sti", "fty", "sty", "vty", "lang", "seo", "artistATW",
        "artistTitle", "tags", "oty", "clip_url", "vurl", "scoreF", "psl", "language", "subtitle", "tile_type"};

    /**
     * Add or change language in api url params from here.
     * @return
     */
    public static List<String> requestedLang() {
        String str [] = {"Hindi" ,"English", "Punjabi"};
        List<String> list = Arrays.asList(str);
        return list;
    }

    public static void main(String[] args) {
        System.out.println();
    }

    /**
     * Prep endpoint from this function.
     * @return
     */
    public static String prepEndpointParams(){
        String endpoint = requestedLang().toString().replaceAll("[\\[\\]\\(\\)]", "");
        return query_params+endpoint.replaceAll(" ", "").trim();
    }
}