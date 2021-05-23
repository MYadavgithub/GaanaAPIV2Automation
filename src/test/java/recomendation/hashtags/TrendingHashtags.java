package recomendation.hashtags;
import config.BaseUrls;
import config.Endpoints;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import test_data.HashTagTd;
import common.GlobalConfigHandler;
import common.Helper;
import common.RequestHandler;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class TrendingHashtags extends BaseUrls{
    
    String BASEURL = "";
    Response response = null;
    JSONArray hashTags = null;
    Helper helper = new Helper();
    RequestHandler request = new RequestHandler();
    private static Logger log = LoggerFactory.getLogger(TrendingHashtags.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-42866";
    final static String REPROTING_FEATURE = "Trending Hastag api content validations.";


    @BeforeClass
    public void prepEnv(){
        GlobalConfigHandler.setLocalProps();
        BASEURL = baseurl();
    }

    @Test(enabled = true, priority = 1)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("To validate trending hastag api on basis of response objects.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Url and validate status code response body type and save respponse.")
    @Severity(SeverityLevel.NORMAL)
    public void createTrendingHashTagCall(){
        String url = BASEURL+Endpoints.trendingHashTag;
        response = request.createGetRequest(url);
        Assert.assertEquals(response != null, true, "Response can't be null.");
    }

    @Test(enabled = true, priority = 2)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validate whether response data is null or having values.")
    @Severity(SeverityLevel.NORMAL)
    public void validateTrendingHashTagsResponseLength(){
        JSONObject response_obj = new JSONObject(response.asString());
        hashTags = response_obj.getJSONArray("hashtags");
        Assert.assertEquals(hashTags.length() > 0, true, "Hashtags can't be null!");
    }

    @Test(enabled = true, priority = 3)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Detailed validation of response objects in terms of id, seo key name and expected keys validation.")
    @Severity(SeverityLevel.NORMAL)
    public void validateTrendingHashTagsResponseData(){
        int counter = 0;
        boolean isKeysValid = false;
        SoftAssert softAssert = new SoftAssert();
        Iterator<Object> hashtagItr = hashTags.iterator();
        while(hashtagItr.hasNext()){
            JSONObject hashtag = (JSONObject) hashtagItr.next();
            if(counter == 0){
                isKeysValid = validateExpectedKeys(hashtag);
            }

            softAssert.assertEquals(isKeysValid, true, "keys not valid in response data!");

            int hashtagID = Integer.parseInt(hashtag.getString("hashtagId").toString().trim());
            softAssert.assertEquals(hashtagID > 0, true, "Hashtag ID is not valid!");

            String name = hashtag.optString("name").toString().trim();
            softAssert.assertEquals(name.length() > 0, true, "Hashtag name is not valid!");
            softAssert.assertEquals(name.contains("#"), true, "Hashtag name must contains # !");

            String seoKey = hashtag.optString("seoKey").toString().trim();
            softAssert.assertEquals(seoKey.length() > 0, true, "Hashtag seoKey is not valid!");

            float scoreVal = 0.00f;
            String score = hashtag.optString("score").toString().trim();
            if(score.length() > 0){
                scoreVal = Float.parseFloat(score);
                softAssert.assertEquals(scoreVal > 0, true, "Hashtag score is not valid!");
            }else{
                log.warn("Socre value for hashtag id "+hashtagID+ " and name "+name+ " is : "+scoreVal);
            }

            softAssert.assertAll();
            counter++;
        }
    }

    private boolean validateExpectedKeys(JSONObject hashtag){
        boolean isKeyValid = false;
        List<Object> responseObjKeys = helper.keys(hashtag);
        for(String key : HashTagTd.trendingHashTagReskeys){
            if(responseObjKeys.contains(key)){
                isKeyValid = true;
            }else{
                isKeyValid = false;
                log.error(key + " key not found in response object keys list!");
                break;
            }
        }
        return isKeyValid;
    }
}
