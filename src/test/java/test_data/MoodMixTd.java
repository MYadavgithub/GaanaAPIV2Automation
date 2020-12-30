package test_data;
import java.util.List;
import java.util.Arrays;

public class MoodMixTd {

    public final static int INVOCATION_COUNT = 8;
    public static int [] entity_ids = {1, 2, 3, 4, 5, 7, 8, 30};

    /**Made for You Test Data  */
    public static List<String> expectedUserType() {
        String str [] = {"EXISTING USER", "NEW USER"};
        return Arrays.asList(str);
    }

    /**Made for You Test Data  */
    public static List<String> expectedMadeForYouKeys(){
        String str [] = {"backgroundArtworkUrl","mixType","trackIds","trackType","title","artworkTemplateId","textColorCode"};
        return Arrays.asList(str);
    }

    /**DeviceConsumedLanguage Test Data */
    public static List<String> exKeysDeviceConsumedLanguage(){
        String str [] = {"id", "weight", "language"};
        return Arrays.asList(str);
    }
}
