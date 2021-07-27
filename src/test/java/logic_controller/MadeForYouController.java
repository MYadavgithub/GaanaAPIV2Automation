package logic_controller;
import common.Helper;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import io.qameta.allure.Step;
import org.slf4j.LoggerFactory;
import org.testng.asserts.SoftAssert;

/**
 * @author umesh shukla
 */

public class MadeForYouController {
    
    Helper helper = new Helper();
    private static Logger log = LoggerFactory.getLogger(MadeForYouController.class);

    public int resetCounter(int counter) {
        if(counter == 3){
            counter = 0;
        }else{
            counter++;
        }
        return counter;
    }

    public boolean genricVplValidation(int flag, String user_type, JSONArray response, String device_id){
        boolean result = false;
        Iterator<Object> vplItr = response.iterator();
        while(vplItr.hasNext()){
            boolean isCommonValid = false;
            JSONObject vplMix = (JSONObject) vplItr.next();
            String vplType = vplMix.optString("vplType").toString().trim();

            switch (vplType) {
                case "GENERIC-MIX":
                    if(flag == 0){
                        isCommonValid = validateCommonDetails(user_type, vplMix, vplType, device_id);
                        result = (isCommonValid) ? true : false;
                        if(!isCommonValid){
                            result = false;
                            break;
                        }
                    }else if(flag == 1){
                        isCommonValid = validateTrackDetails(user_type, vplMix, vplType, device_id);
                        result = (isCommonValid) ? true : false;
                        if(!isCommonValid){
                            result = false;
                            break;
                        }
                    }else if(flag == 2){
                        result = true;
                    }
                break;

                case "ARTIST":
                    if(flag == 0){
                        isCommonValid = validateCommonDetails(user_type, vplMix, vplType, device_id);
                        result = (isCommonValid) ? true : false;
                        if(!isCommonValid){
                            result = false;
                            break;
                        }
                    }else if(flag == 1){
                        isCommonValid = validateTrackDetails(user_type, vplMix, vplType, device_id);
                        result = (isCommonValid) ? true : false;
                        if(!isCommonValid){
                            result = false;
                            break;
                        }
                    }else if(flag == 2){
                        isCommonValid = validateSourceIdDetails(user_type, vplMix, vplType, device_id);
                        result = (isCommonValid) ? true : false;
                        if(!isCommonValid){
                            result = false;
                            break;
                        }
                    }
                break;

                case "TAG":
                    if(flag == 0){
                        isCommonValid = validateCommonDetails(user_type, vplMix, vplType, device_id);
                        result = (isCommonValid) ? true : false;
                        if(!isCommonValid){
                            result = false;
                            break;
                        }
                    }else if(flag == 1){
                        isCommonValid = validateTrackDetails(user_type, vplMix, vplType, device_id);
                        result = (isCommonValid) ? true : false;
                        if(!isCommonValid){
                            result = false;
                            break;
                        }
                    }else if(flag == 2){
                        isCommonValid = validateSourceIdDetails(user_type, vplMix, vplType, device_id);
                        result = (isCommonValid) ? true : false;
                        if(!isCommonValid){
                            result = false;
                            break;
                        }
                    }
                break;
            }
        }
        return result;
    }

    @Step("Validating mixType, title, textColorCode, and artworkTemplateId")
    private boolean validateCommonDetails(String user_type, JSONObject vplMix, String vplType, String device_id) {
        boolean isCommonValid = false;
        SoftAssert softAssert = new SoftAssert();
        ArrayList<String> artwork = new ArrayList<>();
        String mixType = vplMix.optString("mixType").toString().trim();
        softAssert.assertEquals(mixType.length() > 0, true, "mixType validation got failed for device_id : "+device_id);

        String title = vplMix.optString("title").toString().trim();
        softAssert.assertEquals(title.length() > 0, true, "title validation got failed for device_id : "+device_id);

        String textColorCode = vplMix.optString("textColorCode").toString().trim();
        softAssert.assertEquals(textColorCode.contains("#"), true, "textColorCode validation got failed for device_id : "+device_id);

        int artworkTemplateId = Integer.parseInt(vplMix.optString("artworkTemplateId").toString().trim());
        softAssert.assertEquals(artworkTemplateId == 3 , true, "artworkTemplateId validation got failed for device_id : "+device_id);

        /* 
        * accroding to jira comment code disabled (https://timesgroup.jira.com/browse/GAANA-43501) comment date 4th june
        if(user_type.equalsIgnoreCase(MoodMixTd.expectedUserType[0])){
            if(vplType.equals("TAG")){
                softAssert.assertEquals(artworkTemplateId == 0, true, "artworkTemplateId validation got failed for device_id : "+device_id);
            }else{
                softAssert.assertEquals(artworkTemplateId == 3, true, "artworkTemplateId validation got failed for device_id : "+device_id);
            }
        }else if(user_type.equalsIgnoreCase(MoodMixTd.expectedUserType[1])){
            if(vplType.equals("ARTIST")){
                softAssert.assertEquals(artworkTemplateId == 3 , true, "artworkTemplateId validation got failed for device_id : "+device_id);
            }else{
                softAssert.assertEquals(artworkTemplateId == 0, true, "artworkTemplateId validation got failed for device_id : "+device_id);
            }
        }*/

        String backgroundArtworkUrl = vplMix.optString("backgroundArtworkUrl").toString().trim();
        artwork.add(backgroundArtworkUrl);

        softAssert.assertAll();

        isCommonValid = true;

        if(isCommonValid && artwork.size() > 0){
            helper.validateActiveLinks(artwork);
            artwork.clear();
        }

        return isCommonValid;
    }

    @Step("Validating trackIds and trackType for user type {0}")
    private boolean validateTrackDetails(String user_type, JSONObject vplMix, String vplType, String device_id) {
        boolean isTrackDetailsValid = true;
        int trackType = Integer.parseInt(vplMix.getString("trackType").toString().trim());
        JSONArray madeForYouTracks = vplMix.getJSONArray("trackIds");
        for(int i = 0; i<madeForYouTracks.length(); i++){
            if(Double.parseDouble(madeForYouTracks.optString(i).toString().trim()) <= 0){
                isTrackDetailsValid = false;
                log.error("For device_id : "+device_id+" and user_type : "+user_type+ " tracks id not correct, where vplType : "+vplType);
                break;
            }
        }
        if(isTrackDetailsValid && trackType > 0){
            return isTrackDetailsValid;
        }
        return false;
    }

    @Step("Validating SourceId in artist and tag type vallues.")
    private boolean validateSourceIdDetails(String user_type, JSONObject vplMix, String vplType, String device_id) {
        if(vplType.equalsIgnoreCase("ARTIST") || vplType.equalsIgnoreCase("TAG")){
            int sourceId = Integer.parseInt(vplMix.optString("sourceId").toString().trim());
            if(sourceId > 0){
                return true;
            }else{
                log.error("user_type : "+user_type+" vpl_type : "+vplType+" and device_id : "+device_id+" source_id is not correct!");
            }
        }
        return false;
    }
}