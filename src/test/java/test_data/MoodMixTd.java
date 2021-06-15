package test_data;
import java.util.List;
import java.util.Arrays;

public class MoodMixTd {
    public final static int DEVICE_CONSUMED_INVOCATION_COUNT = 2;
    public final static int INVOCATION_COUNT = 8;
    public static int [] entity_ids = {1, 2, 3, 4, 5, 7, 8, 30};

    /**Made for You Test Data  */
    public final static int MADE_FOR_YOU_INVOCATION = 4;
    public static String device_ids [] = {"ONEPLUS A6000_f264f15b7475ac77", "ONEPLUS A6000_d9f2469c6920860b",
        "Redmi Note 5 Pro_69d7f6cd6e277eba ", "Device not in use"};

    public static String expectedUserType [] = {"EXISTING USER", "NEW USER"};

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
