package recomendation.v2;
import config.BaseUrls;
import config.Endpoints;
import java.util.List;
import java.util.Map;
import common.Headers;
import common.Helper;
import org.slf4j.Logger;
import org.testng.Assert;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import utils.CommonUtils;
import org.json.JSONObject;
import java.util.Map.Entry;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import io.restassured.response.Response;
import test_data.MoodMixTd;
import org.testng.annotations.BeforeClass;

public class MadeForYou extends BaseUrls {

    String BASEURL = "";
    Helper helper = new Helper();
    RequestHandler handler = new RequestHandler();
    Map<Integer, Response> responses = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(MadeForYou.class);

    @BeforeClass
    protected void prepeareEnv() {
        // System.setProperty("env", "prod");
        // System.setProperty("type", "reco");
        // System.setProperty("device_type", "android");
        baseurl();
        BASEURL = prop.getProperty("prec_baseurl").toString().trim();
    }

    @Test(priority = 1)
    public void createCallToMadeForYou() {
        String url = BASEURL + Endpoints.madeForYou;
        Response response = handler.createGetRequest(url);
        if (response != null) {
            responses.put(0, response);
            response = null;
            Map<String, String> headers = Headers.getHeaders(0);
            headers.replace("deviceId", CommonUtils.generateRandomDeviceId());
            response = handler.createGetRequestWithCustomHeaders(url, headers);
            if (response != null)
                responses.put(1, response);
        }

        if (responses.size() != 2 || responses == null) {
            log.error("Two api call was expected but there is some error Manual check required!");
            Assert.assertEquals(2, responses.size());
        }
    }

    @Test(priority = 2)
    public void validateMadeForYouResponseBody(){
        int counter = 0;
        boolean isMadeForYouValidated = false;
        SoftAssert softAssert = new SoftAssert();
        List<String> expectedUsers = MoodMixTd.expectedUserType();
        for(Entry<Integer, Response> response : responses.entrySet()){
            JSONObject responseObject = new JSONObject(response.getValue().asString());
            String user_type = responseObject.optString("userType").toString().trim();
            if(!expectedUsers.contains(user_type)){
                log.error("User Type value unexpected in response body! Value got for user type is : "+user_type);
                softAssert.assertEquals(true, expectedUsers.contains(user_type));
            }
            JSONArray MadeForYouArr = responseObject.getJSONArray("vplMix");
            isMadeForYouValidated =  validateMadeForYouDetails(counter, MadeForYouArr);
            counter++;
        }

        if(!isMadeForYouValidated){
            log.error("Response body validation broken for Made For You api!");
            Assert.assertEquals(true, isMadeForYouValidated);
        }
    }

    private boolean validateMadeForYouDetails(int counter, JSONArray madeForYouArr) {
        boolean isDatavalidated = false;
        ArrayList<String> artworks = new ArrayList<>();
        if(madeForYouArr.length() > 0){
            Iterator<Object> itr = madeForYouArr.iterator();
            while(itr.hasNext()){
                JSONObject madeForYou = (JSONObject) itr.next();
                List<Object> keys = helper.keys(madeForYou);
                List<String> expectedKeys = MoodMixTd.expectedMadeForYouKeys();
                boolean isKeysValidated = validatekeys(expectedKeys, keys);
                Assert.assertEquals(true, isKeysValidated);
                
                artworks.add(madeForYou.optString("backgroundArtworkUrl").toString().trim());
                String mixType = madeForYou.optString("mixType").toString().trim();
                String title = madeForYou.optString("title").toString().trim();
                String textColorCode = madeForYou.optString("textColorCode").toString().trim();
                int track_type = Integer.parseInt(madeForYou.getString("trackType").toString().trim());
                int artworkTemplateId = Integer.parseInt(madeForYou.optString("artworkTemplateId").toString().trim());

                if(mixType.length() <= 0 && title.length() <= 0 && textColorCode.length() <= 0 && track_type < 0 && artworkTemplateId < 0){
                    log.error("In response Mixtype, title, textColorCode, trackType or artworkTemplateId is present with worng value, manual check required!");
                    Assert.assertEquals(true, false);
                }

                boolean trackIdValidated = true;
                JSONArray madeForYouTracks = madeForYou.getJSONArray("trackIds");
                for(int i = 0; i<madeForYouTracks.length(); i++){
                    if(Double.parseDouble(madeForYouTracks.optString(i).toString().trim()) <= 0){
                        trackIdValidated = false;
                    }
                }
                Assert.assertEquals(true, trackIdValidated);
                isDatavalidated = true;
            }
        
            if(artworks.size() > 0 && isDatavalidated){
                isDatavalidated = helper.validateActiveLinks(artworks);
            }
        }
        return isDatavalidated;
    }

    public boolean validatekeys(List<String> expectedKeys, List<Object> keys) {
        boolean isKeyValidated = false;
        if(expectedKeys.size() == keys.size()){
            for(Object key : keys) {
                if(!expectedKeys.contains(key.toString().trim())){
                    isKeyValidated = false;
                    log.error("Key value is unexpected : "+key);
                    break;
                }else{
                    isKeyValidated = true;
                }
            }
        }
        return isKeyValidated;
    }
}