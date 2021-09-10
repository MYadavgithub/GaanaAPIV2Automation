package logic_controller;
import java.util.*;
import org.slf4j.*;
import org.testng.Assert;
import org.json.*;
import pojo.*;
import test_data.DeviceLangTd;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author Umesh.Shukla
 */

public class DeviceLanguageController {

    private static Logger LOG = LoggerFactory.getLogger(DeviceLanguageController.class);

    public boolean validateAssociatedLanguage(String language, List<AssociatedLanguageEntity> associated_languages) {
        boolean isAssociatedLanguageValid = false;
        List<String> ex_associated_lang = Arrays.asList(DeviceLangTd.associatedLanguageDetails(language));
        if(!CollectionUtils.isEmpty(ex_associated_lang)){
            if(ex_associated_lang.size() == associated_languages.size()){
                for(AssociatedLanguageEntity lang : associated_languages){
                    boolean isLangPresent = ex_associated_lang.contains(lang.getLanguage());
                    boolean isWeightValid = lang.getWeight() <= -1000;
                    if(isLangPresent && isWeightValid){
                        isAssociatedLanguageValid = true;
                    }
                }
            }else{
                isAssociatedLanguageValid = false;
                LOG.error(this.getClass()+ "In response object language entities must be eqaul to ex_associated_lang...!");
                Assert.assertTrue(ex_associated_lang.size() == associated_languages.size());
            }
        }else{
            isAssociatedLanguageValid = true;
            LOG.error(this.getClass()+ " for language "+language+ " no associated language found...!");
        }
        return isAssociatedLanguageValid;
    }

    public boolean validateSearchedLangPresentInResponse(String languges, AssociatedLanguages asLanguage) {
        String [] searchLang = languges.split(",");
        for(String languge : searchLang){
            if(asLanguage.getLanguage().equalsIgnoreCase(languge))
                return true;
        }
        return false;
    }

    public List<AssociatedLanguages> responseArrayToList(JSONArray responseArray) {
        List<AssociatedLanguages> associatedLanguages = new ArrayList<>();
        for(int i = 0; i<responseArray.length(); i++){
            JSONObject languageObject = responseArray.getJSONObject(i);
            
            String language = languageObject.optString("language").toString().trim();
            JSONArray languageEntity = languageObject.getJSONArray("associatedLanguages");
            
            List<AssociatedLanguageEntity> associatedLanguageEntities = new ArrayList<>();
            for(int j = 0; j<languageEntity.length(); j++){
                JSONObject languageObj = languageEntity.getJSONObject(j);
                int id = Integer.parseInt(languageObj.optString("id").toString().trim());
                String _language = languageObj.optString("language").toString().trim();
                double weight = Double.parseDouble(languageObj.optString("weight").toString().trim());
                associatedLanguageEntities.add(new AssociatedLanguageEntity(id, _language, weight));
            }

            associatedLanguages.add(new AssociatedLanguages(language, associatedLanguageEntities));
        }
        return associatedLanguages;
    }

    /**--------------- DeviceLanguage ---------------- */
    /**
     * Convert JSONArray to List
     * @param languageArray
     */
    public List<LanguageEntity> convertJSONArrayToList(JSONArray languageArray){
        List<LanguageEntity> langEntity = new ArrayList<>();
        if (languageArray != null) { 
            for (int i = 0; i<languageArray.length(); i++){ 
                JSONObject languageObj = languageArray.getJSONObject(i);
                int id = 0;
                String language = "";
                double weight = 0.00;
                try{
                    id = Integer.parseInt(languageObj.optString("id").toString().trim());
                    language = languageObj.optString("language").toString().trim();
                    weight = Double.parseDouble(languageObj.optString("weight").toString().trim());
                }catch(Exception e){
                    e.printStackTrace();
                    LOG.error(this.getClass()+" Invalid language object...!");
                }
                langEntity.add(new LanguageEntity(id, language, weight));
            } 
        }
        return langEntity;
    }

    /**--------------- DeviceTrackPlayouts ---------------- */
    /**
     * Convert from array to list
     * @param responseArray 
     * @return
     */
    public List<DeviceTrackPlayout> convertToList(JSONArray responseArray) {
        List<DeviceTrackPlayout> deviceTrackPlayouts = new ArrayList<>();  
        for(int i = 0; i<responseArray.length(); i++){
            JSONObject trackObject = responseArray.getJSONObject(i);
            int trackId = Integer.parseInt(trackObject.optString("trackId").trim().toString());
            int playout = Integer.parseInt(trackObject.optString("playout").trim().toString());
            double deviceDuration = Double.parseDouble(trackObject.optString("deviceDuration").trim().toString());
            double songDuration = Double.parseDouble(trackObject.optString("songDuration").trim().toString());
            DeviceTrackPlayout deviceTrackPlayout = new DeviceTrackPlayout(trackId, playout, deviceDuration, songDuration);
            deviceTrackPlayouts.add(deviceTrackPlayout);
        }
        return deviceTrackPlayouts;
    }
}
