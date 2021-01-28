package recomendation.v2;
import java.util.Map;
import common.Helper;
import config.BaseUrls;
import config.Endpoints;
import org.slf4j.Logger;
import org.testng.Assert;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import test_data.MoodMixTd;
import java.util.ArrayList;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
import common.GlobalConfigHandler;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

public class MoodMix extends BaseUrls{

    int max_call = 0;
    String BASEURL = "";
    int api_call_count = 0;
    String device_id = "ONEPLUS%20A5010_7445b8c92f959ba2";
    ArrayList<String> urls = new ArrayList<>();
    Map<Integer, Response> responses = new HashMap<>();
    Helper helper = new Helper();
    RequestHandler handler = new RequestHandler();
    GlobalConfigHandler gHandler = new GlobalConfigHandler();
    private static Logger log = LoggerFactory.getLogger(MoodMix.class);
    final static String REPROTING_FEATURE = "Mood Mix v2 api response validations";

    @BeforeClass
    public void prepareEnv() {
        // System.setProperty("env", "prod");
        // System.setProperty("type", "reco");
        // System.setProperty("device_type", "android");
        baseurl();
        BASEURL = prop.getProperty("prec_baseurl").toString().trim();
        prepareUrls();
    }

    @DataProvider(name = "urlProvider")
    public Object[][] DataProvider() {
        return new Object[][] {
            {
                urls.get(api_call_count)
            }
        };
    }


    @Test(priority = 1, dataProvider = "urlProvider", invocationCount = MoodMixTd.INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = "https://timesgroup.jira.com/browse/GAANA-40468")
    @Feature(REPROTING_FEATURE)
    @Story("Validate response time, status code, response body, title, entity type, and artworks.")
    @Description("Once response got we will store results in to map for further validations")
    @Step("If one or more than one keyword then put all value till last call in map, after that dump into csv file.")
    @Severity(SeverityLevel.BLOCKER)
    public void createAllRequests(String url){
        log.info("URL : "+url);
        Response response = handler.createGetRequest(url);
        responses.put(api_call_count, response);

        if(api_call_count == (MoodMixTd.INVOCATION_COUNT-1)){
            Assert.assertEquals(responses.size(), MoodMixTd.INVOCATION_COUNT, "All Responses not captured please check manually!");
        }
        api_call_count = gHandler.invocationCounter(api_call_count, max_call);
    }

    @Test(priority = 2, dataProvider = "urlProvider", invocationCount = MoodMixTd.INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = "https://timesgroup.jira.com/browse/GAANA-40468")
    @Feature(REPROTING_FEATURE)
    @Description("Validate response body w.r.t. Title, subtitle, userType and artworks.")
    @Step("For each response we will validate which data saved in local memory(map).")
    @Severity(SeverityLevel.CRITICAL)
    public void validateResponseData(String url){
        int flag = 0;
        boolean isEntitiesValid = false;
        JSONObject response = new JSONObject(responses.get(api_call_count).asString());

        String title = response.optString("title").trim();
        String subTitle = response.optString("subTitle").trim();
        String userType = response.optString("userType").trim();
        JSONArray entityMixObjects = response.getJSONArray("entityMixObjects");

        if(subTitle.length() > 0 && userType.length() > 0){
            log.info("SubTitle and userType available in response body.");
        }

        if(title.length() > 0 && entityMixObjects.getJSONObject(0).length() == 2){
            flag = 1; // to validate entity type & id
            if(entityMixObjects.length() > 0){
                isEntitiesValid = entityMixObjects(flag, entityMixObjects);
            }else{
                isEntitiesValid = true;
                log.info("Entity objects are empty for below given url : \n"+urls.get(api_call_count));
            }
        }else if(title.length() > 0 && entityMixObjects.getJSONObject(0).length() > 2){
            flag = 2; // others
            if(entityMixObjects.length() > 0){
                isEntitiesValid = entityMixObjects(flag, entityMixObjects);
            }else{
                isEntitiesValid = true;
                log.info("Entity objects are empty for below given url : \n"+urls.get(api_call_count));
            }    
        }

        String failed_url = "";
        if(!isEntitiesValid){
            failed_url = urls.get(api_call_count);
        }

        api_call_count = gHandler.invocationCounter(api_call_count, max_call);
        Assert.assertEquals(isEntitiesValid, true, "In below given url, validation got failed : \n "+failed_url);  
    }

    @Step("Validating each entityMixObject, got in api response")
    @Severity(SeverityLevel.BLOCKER)
    private boolean entityMixObjects(int flag, JSONArray entityMixObjects) {
        int counter = 0;
        int entity_type = 0;
        boolean result = false;
        ArrayList<String> artworks = new ArrayList<>();
        Iterator<Object> entities = entityMixObjects.iterator();
        while(entities.hasNext()){
            JSONObject entity = (JSONObject) entities.next();
            int entityType = Integer.parseInt(entity.optString("entityType").trim());
            String entity_id = entity.optString("entity").trim();
            if(counter == 0){
                entity_type = entityType;
            }
            
            if(entityType == entity_type && (entity_id.length() > 0 || !entity_id.isEmpty())){
                if(flag == 1){
                    result = true;
                }else if(flag == 2){
                    String entityTitle = entity.optString("entityTitle").trim();
                    String mixType = entity.optString("mixType").trim();
                    int showartwork = Integer.parseInt(entity.optString("showartwork").trim());
                    String backgroundArtworkUrl = entity.optString("backgroundArtworkUrl").trim();
                    String artworkTemplateId = entity.optString("artworkTemplateId").trim();

                    if(entityTitle.length() > 0 && mixType.length() > 0 && showartwork == 1){
                        result = true;
                        if(!artworkTemplateId.isEmpty()){
                            artworks.add(backgroundArtworkUrl);
                        }
                    }else{
                        result = false;
                        log.error("Entity title, mixType or showartwork validation got failed for entity :  "+entity_id+ "\nUrl was : "+urls.get(api_call_count));
                        break;
                    }
                }
            }else{
                SoftAssert softAssert = new SoftAssert();
                log.error("We got error in entity : "+entity);
                result = false;
                softAssert.assertEquals(true, result);
            }
            counter++;
        }

        if(artworks.size() > 0){
            result = helper.validateActiveLinks(artworks);
        }
        return result;
    }

    private void prepareUrls() {
        int[] entity_ids = MoodMixTd.entity_ids;
        max_call = entity_ids.length;
        // max_call = 1;
        // urls.add(baseurl+Endpoints.moodMix+"?deviceId="+device_id+"&entityId="+30);
        for(int entity_id : entity_ids){
            urls.add(BASEURL+Endpoints.moodMix+"?deviceId="+device_id+"&entityId="+entity_id);
        }
    }
}