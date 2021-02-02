package logic_controller;
import java.util.ArrayList;
import java.util.Iterator;
import common.Helper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.asserts.SoftAssert;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import utils.CommonUtils;

public class TrendingTrackController {

    Helper helper = new Helper();
    CommonUtils utils = new CommonUtils();
    private static Logger log = LoggerFactory.getLogger(TrendingTrackController.class);

    @Step("Getting possible search keys from default response which is {1}")
	public ArrayList<String> getSpecificSearchKeywords(ArrayList<String> types, Response response) {
        JSONObject responseObj = utils.converResponseToJSONObject(response);
        JSONArray data = responseObj.getJSONArray("data");
        if(data.length() > 0){
            Iterator<Object> dataItr = data.iterator();
            while(dataItr.hasNext()){
                JSONObject dataObj = (JSONObject) dataItr.next();
                String type = dataObj.optString("type").toString().trim();
                if(type.length() > 0){
                    types.add(type);
                }
            }
        }
        return types;
    }

    /**
     * Flag 0 = banner data, Flag 1 = playlist data and Flag 2 = Data
     * @param flag
     * @param type_key
     * @param dataList
     * @return
     */
    @Step("All detailed entity will be validated here. values are {0}, {1}, {2}")
	public boolean validateData(int flag, String type_key, JSONArray dataList) {
        boolean isDataAvl = dataList.length() > 0;
        if(!isDataAvl){
            log.info("Data not available for request type : "+type_key);
            return true;
        }

        Iterator<Object> dataItr = dataList.iterator();
        while(dataItr.hasNext()){
            JSONObject DataObj = (JSONObject) dataItr.next();
            int sequenceOfSection = Integer.parseInt(DataObj.optString("sequenceOfSection").toString().trim());
            String type = DataObj.optString("type").toString().trim();
            JSONArray entityList = null;
            if(flag == 0){
                entityList = DataObj.getJSONArray("bannerEntities");
            }else if(flag == 1){
                entityList = DataObj.getJSONArray("playlistEntities");
            }else{
                entityList = DataObj.getJSONArray("entities");
            }
            if(type.length() > 0 && sequenceOfSection > 0){
                if(entityList.length() > 0){
                    validateEntityList(flag, type_key, entityList);
                    return true;
                }else{
                    log.error("For "+type_key+ " no entityList found!");
                }
            }else{
                log.info("Error!");
            }
        }
        return false;
    }

    private void validateEntityList(int flag, String type_key, JSONArray entityList){
        int id = 0;
        String title = "";
        JSONArray languageArray = null;
        JSONArray artistList = null;
        SoftAssert softAssert = new SoftAssert();
        ArrayList<String> aw = new ArrayList<>();
        for(int i = 0; i<entityList.length(); i++){
            JSONObject entity = entityList.getJSONObject(i);
            if(flag == 0){
                id = Integer.parseInt(entity.optString("id").toString().trim());
                title = entity.optString("title").toString().trim();
                if(id > 1 && title.length() > 0){
                    aw.add(entity.optString("bannerAtw").toString().trim());
                }
                softAssert.assertEquals(id > 0, true);
            }else if (flag == 1){
                id = Integer.parseInt(entity.optString("playlistId").toString().trim());
                title = entity.optString("title").toString().trim();
                if(id > 1 && title.length() > 0){
                    aw.add(entity.optString("playlistAtw").toString().trim());
                }
                softAssert.assertEquals(id > 0, true);
            }else{
                String shortTrackId = entity.getString("shortTrackId").toString().trim();
                title = entity.optString("title").toString().trim();
                String trackId = entity.getString("trackId").toString().trim();
                int total_like = Integer.parseInt(entity.optString("total_like").toString().trim());
                int hotshotCount = Integer.parseInt(entity.optString("hotshotCount").toString().trim());

                if(shortTrackId.length() > 0 && title.length() > 0 && trackId.length() > 0 && total_like >= 0 && hotshotCount >= 0){
                    aw.add(entity.optString("artwork").toString().trim());
                }else{
                    softAssert.assertEquals(shortTrackId, shortTrackId.length() > 0, "shortTrackId is not valid!");
                    softAssert.assertEquals(title, title.length() > 0, "title is not valid!");
                    softAssert.assertEquals(trackId, trackId.length() > 0, "trackId is not valid!");
                    softAssert.assertEquals(total_like, total_like > 0, "total_like is not valid!");
                    softAssert.assertEquals(hotshotCount, hotshotCount > 0, "hotshotCount is not valid!");
                }
                try{
                    languageArray = entity.getJSONArray("language");
                    artistList = entity.getJSONArray("artistList");
                }catch(Exception e){
                    e.printStackTrace();
                    log.warn("Exception occurred in case of short track id "+shortTrackId);
                }
                 
                if(languageArray != null){
                    String lang = getValues(languageArray).toString().replaceAll("[\\[\\]\\(\\)]", "").trim();
                    softAssert.assertEquals(lang.length() > 0, true, "language list is not valid!");
                    // log.info("Languages for short track id "+shortTrackId+ " are : "+lang);
                }else if(artistList != null){
                    String artist = getValues(artistList).toString().replaceAll("[\\[\\]\\(\\)]", "").trim();
                    softAssert.assertEquals(artist.length() > 0, true, "artist list is not valid!");
                    // log.info("Artist for short track id "+shortTrackId+ " are : "+artist);
                }
            }
        }

        boolean isAwValidated = helper.validateActiveLinks(aw);
        softAssert.assertEquals(isAwValidated, true, "Artwork validation failed!");
        softAssert.assertAll();
    }

    private Object getValues(JSONArray dataArray) {
        ArrayList<String> arr = new ArrayList<>();
        if(dataArray.length() > 0){
            for(int i = 0; i < dataArray.length(); i++){
                arr.add(dataArray.get(i).toString().trim());
            }
        }
        return arr;
    }
}