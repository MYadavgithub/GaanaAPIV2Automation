package test_data;
import java.util.List;
import java.util.Arrays;

public class MixTd {
    public final static int DEVICE_CONSUMED_INVOCATION_COUNT = 2;
    public final static int INVOCATION_COUNT = 8;
    public static int [] entity_ids = {4, 5, 7, 8, 30};

    /**Made for You Test Data  */
    public final static int MADE_FOR_YOU_INVOCATION = 4;
    public static String device_ids [] = {"ONEPLUS A6000_f264f15b7475ac77", "vivo 1901_3042fbcce6aba834",
            "32496C8D-249A-4FC0-A509-7ECBC0AB8035", "Device not in use"};

    public static String expectedUserType [] = {"EXISTING USER", "NEW USER"};

    /**Made for You Test Data  */
    public static List<String> expectedMadeForYouKeys(){
        String str [] = {"mixType","title","trackIds","trackType", "backgroundArtworkUrl","textColorCode","artworkTemplateId","vplType","sourceId"};
        return Arrays.asList(str);
    }

    /**DeviceConsumedLanguage Test Data */
    public static List<String> exKeysDeviceConsumedLanguage(){
        String str [] = {"id", "weight", "language"};
        return Arrays.asList(str);
    }

    /** Daily Mix */
    public final static int DM_INVOCATION_COUNT = 4; // equal to DM_DEVICES length
    public static String DM_DEVICES [] = {"RMX1911_2198cdced8965f03", "SM-M215F_bb3e79046e027a91",
        "Redmi Note 5 Pro_69d7f6cd6e277eba", "ASUS_X01BDA_0f9fff2615cf7d49"};
}
