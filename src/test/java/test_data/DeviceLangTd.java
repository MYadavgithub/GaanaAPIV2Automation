package test_data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DeviceLangTd {
    
    // consumed in = ExistingDevice
    public final static int E_D_INVOCATION = 4;
    public static String DEVICE_IDS []  = {"M2007J17I_af67ba0ea6321cbe", "SM-G960F_05677aef52a816c3",
        "vivo 1906_70f061265840aac6", "ONEPLUS A6000_f264f15b7475ac77"};

    /**
     * DeviceTrackPlayouts
     * Last device intensionaly put as invalid you can put any where in this array.
     */
    public final static int DTP_INVOCATION = 4;
    public static String DTP_DEVICE_IDS []  = {"M2007J17I_af67ba0ea6321cbe", "SM-G960F_05677aef52a816c3",
        "vivo 1906_70f061265840aac6", "ONEPLUS A1234567hgt"};

    /**
     * As of now Haryanvi's associated lang not found. so added for negative test cases.
     */
    public final static int AL_INVOCATION = 5; // equals LanguagesList().size;
    public static String LANGUAGES [] = {"English","Hindi","Urdu","Punjabi","Tamil","Bhojpuri", "Telugu", "Haryanvi"};
    public static ArrayList<String>LanguagesList(){
        ArrayList<String> associatedLang = new ArrayList<>();
        associatedLang.add("");
        associatedLang.add("English");
        associatedLang.add("English,Hindi,Urdu,Punjabi,Tamil,Bhojpuri");
        associatedLang.add("Haryanvi");
        associatedLang.add("English,Hindi,Urdu,Punjabi,Tamil,Bhojpuri,Telugu,Haryanvi");
        return associatedLang;
    }

    public static String[] associatedLanguageDetails(String language){
        String[] associatedLanguages = {};
        Map<String, String[]> associatedLangs = new HashMap<>();
        associatedLangs.put("English", new String [] {"Hindi", "Punjabi"});
        associatedLangs.put("Hindi", new String [] {"Punjabi", "English"});
        associatedLangs.put("Bhojpuri", new String [] {"Hindi", "English"});
        associatedLangs.put("Punjabi", new String [] {"Hindi", "English"});
        associatedLangs.put("Tamil", new String [] {"Telugu", "Hindi", "English"});
        associatedLangs.put("Telugu", new String [] {"Hindi", "English", "Tamil"});

        for(Entry<String, String[]> aLang : associatedLangs.entrySet()){
            if(aLang.getKey().trim().equals(language.trim())){
                return aLang.getValue();
            }
        }
        return associatedLanguages;
    }
}
