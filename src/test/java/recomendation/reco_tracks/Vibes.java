package recomendation.reco_tracks;
import java.util.Map;
import utils.*;
import config.*;
import config.v1.GetProp;
import java.util.HashMap;
import org.testng.*;
import java.util.Iterator;
import common.*;
import org.json.JSONArray;
import java.util.ArrayList;
import org.json.JSONObject;
import org.slf4j.*;
import io.restassured.response.Response;
import org.testng.annotations.*;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;

/**
 * @author Umesh Shukla
 * @version 8.22.0 Support Disabled
 * @deprecated https://timesgroup.jira.com/browse/GAANA-43257
 */

public class Vibes {

    String id = "1";
    String baseurl = "";
    int EXEC_CONTEXT = 0;
    int DEVICE_ID_COUNT = 1;
    static int RES_OBJ = 10;
    String API_NAME = "Vibes";
    GetProp prop = null;
    final static int CASE_COUNT = 5;
    JSONArray entities_list = null;
    Helper helper = new Helper();
    ArrayList<String> device_ids = null;
    ArrayList<String> urls = new ArrayList<>();
    Map<Integer, Response> responses = new HashMap<>();
    Map<Integer, String[]> all_entitys_ids = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(Vibes.class);

    @BeforeClass
    public void prepareRequestData(ITestContext context) {
        // GlobalConfigHandler.setLocalProps();
        EXEC_CONTEXT = Integer.parseInt(context.getCurrentXmlTest().getParameter("id"));
        // baseurl();
        // baseurl = prop.getProperty("reco_baseurl").toString().trim();
        baseurl = GlobalConfigHandler.baseurl();
        prop = new GetProp();
        device_ids = CsvReader.readCsv("./src/test/resources/data/deviceid.csv");
    }

    @DataProvider(name = "device_id")
    public Object[][] DataProvider() {
        return new Object[][] {
            {
                device_ids.get(DEVICE_ID_COUNT)
            }
        };
    }

    @Test(priority = 1, dataProvider = "device_id", invocationCount = CASE_COUNT)
    public void validateStatusAndDataType(String device_id) {
        String url = baseurl + Endpoints.vibes;
        prop.setDeviceId(device_id);
        urls.add(url);
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        // Response response = rq.createGetRequest(url);
        Assert.assertEquals(response != null, true, "Response time or code is not valid!");
        responses.put(DEVICE_ID_COUNT, response);
        deviceIdCounter();
    }

    @Test(priority = 2, dataProvider = "device_id", invocationCount = CASE_COUNT)
    public void getEntityIds(String device_id) {
        if(DEVICE_ID_COUNT == 1) {
            Assert.assertEquals(!responses.isEmpty(), true, "Response is empty can't process further!");
            Assert.assertEquals(responses.size() == CASE_COUNT, true,"Responses list is not equal to number of cases.");
        }

        JSONObject response_object = new JSONObject(responses.get(DEVICE_ID_COUNT).asString());
        int res_obj_count = Integer.parseInt(response_object.getString("count").trim());
        Assert.assertEquals((res_obj_count == RES_OBJ), true, "Response Object must be 10!");

        String[] unique_ids = new String[RES_OBJ];
        JSONArray entities = response_object.getJSONArray("entities");
        Iterator<Object> itr = entities.iterator();
        
        int count = 0;
        while(itr.hasNext()){
            JSONObject entity = (JSONObject) itr.next();
            String entity_id = entity.getString("entity_id").trim();
            String entity_type = entity.optString("entity_type").trim().toString();
            String created_id = entity_id+"_"+entity_type;
            unique_ids[count] = created_id;
            count++;
        }

        all_entitys_ids.put(DEVICE_ID_COUNT, unique_ids);
        deviceIdCounter();
    }

    @Test(priority = 3)
    public void processCsvWrite() {
        if(!all_entitys_ids.isEmpty() && EXEC_CONTEXT == 0){
            String file_name = API_NAME+".csv";
            String head[] = { "Entities" };
            WriteCsv.writeCsvWithHeader(file_name, head, all_entitys_ids, false);
        }else if(EXEC_CONTEXT == 1){
            Map<Integer, ArrayList<String>> previous_entities = readPrevData();
            Assert.assertEquals((previous_entities.size() == all_entitys_ids.size()), true, "Previous and new response size should be same!");
            boolean result = validatedata(previous_entities, all_entitys_ids);
            Assert.assertEquals(result, true, "Previous and new response matching!");
        }
    }

    @Test(priority = 4)
    public void validateEntityCount(){
        JSONObject response = new JSONObject(responses.get(DEVICE_ID_COUNT).asString());
        int response_entity_count = Integer.parseInt(response.getString("count").toString().trim());
        int user_access_token = Integer.parseInt(response.optString("user_token_status").toString().trim());
        Assert.assertEquals(RES_OBJ, response_entity_count, "Response entity count is not matching!");
        Assert.assertEquals(1, user_access_token, "User access token should be 1!");

        entities_list = response.getJSONArray("entities");
        Assert.assertEquals(entities_list.length(), RES_OBJ, "entities count is not matching!");
    }

    @Test(priority = 5, dependsOnMethods = {"validateEntityCount"})
    public void validateEachEntityArtWork(){
        ArrayList<String> artworks = new ArrayList<>();
        Iterator<Object> entities_itr = entities_list.iterator();
        while(entities_itr.hasNext()){
            JSONObject entity = (JSONObject) entities_itr.next();
            String atw = entity.optString("atw").toString().trim();
            String artwork = entity.optString("artwork").toString().trim();
            if(atw.equals(artwork)){
                artworks.add(artwork);
            }else{
                Reporter.log("********* ERROR *********");
                log.error("atw not matching with artwork, which must be matching please check in response of url given below : \n"+urls.get(DEVICE_ID_COUNT-1));
                Reporter.log("********* ERROR *********");
            }
        }

        boolean isArtworkValidated = helper.validateActiveLinks(artworks);
        Assert.assertEquals(isArtworkValidated, true, "Artwork not validated succesfully!");
    }

    @Test(priority = 6, dependsOnMethods = {"validateEachEntityArtWork"})
    public void validateVertVDtoken(){
        Iterator<Object> entities_itr = entities_list.iterator();
        while(entities_itr.hasNext()){
            JSONObject entity = (JSONObject) entities_itr.next();
            JSONObject entity_map = entity.getJSONObject("entity_map");
            String vert_vd = entity_map.opt("vert_vd").toString().trim();
            Assert.assertEquals(vert_vd.length() > 0, true, "vert_vd token is not valdated for url : "+urls.get(DEVICE_ID_COUNT-1));
        }
    }

    @Test(priority = 7, dependsOnMethods = {"validateVertVDtoken"})
    public void validateShortTagsJSONArray(){
        Iterator<Object> entities_itr = entities_list.iterator();
        while(entities_itr.hasNext()){
            JSONObject entity = (JSONObject) entities_itr.next();
            JSONObject entity_map = entity.getJSONObject("entity_map");
            JSONArray short_tracks = entity_map.optJSONArray("short_track");

            for(int i = 0; i<short_tracks.length(); i++){
                JSONObject short_track = short_tracks.getJSONObject(i);
                String seokey = short_track.optString("seokey").toString().trim();
                String track_id = short_track.getString("track_id").toString().trim();

                ArrayList<String> artworks = new ArrayList<>();
                if(seokey.length() > 0 && track_id.length() > 0){
                    artworks.add(short_track.optString("atw").toString().trim());
                    artworks.add(short_track.optString("artwork").toString().trim());
                    boolean isArtworkValidated = helper.validateActiveLinks(artworks);
                    Assert.assertEquals(isArtworkValidated, true, "Short Track artwork not validated succesfully!");
                }else{
                    log.error("SEO Key : "+seokey+ " Track id : "+track_id);
                }
            }
        }
    }

    @Test(priority = 8, dependsOnMethods = {"validateShortTagsJSONArray"})
    public void validateHashtagsJSONArray(){
        Iterator<Object> entities_itr = entities_list.iterator();
        while(entities_itr.hasNext()){
            JSONObject entity = (JSONObject) entities_itr.next();
            JSONObject entity_map = entity.getJSONObject("entity_map");
            JSONArray hashtags = entity_map.optJSONArray("hashtags");

            for(int i = 0; i<hashtags.length(); i++){
                JSONObject hashtag = hashtags.getJSONObject(i);
                String seokey = hashtag.optString("seokey").toString().trim();
                String hashtag_id = hashtag.getString("hashtag_id").toString().trim();
                if(seokey.length() <= 0 && hashtag_id.length() <= 0){
                    Assert.assertEquals(hashtag_id.length() > 0, true, "Hashtag not validated succesfully!");
                }
            }
        }
    }

    @Test(priority = 9, dependsOnMethods = {"validateHashtagsJSONArray"})
    public void validateArtistJSONArray(){
        Iterator<Object> entities_itr = entities_list.iterator();
        while(entities_itr.hasNext()){
            JSONObject entity = (JSONObject) entities_itr.next();
            JSONObject entity_map = entity.getJSONObject("entity_map");
            JSONArray artists = entity_map.optJSONArray("artist");
            Assert.assertEquals(artists.length() > 0, true, "Artist details missing for url : "+urls.get(DEVICE_ID_COUNT-1));

            for(int i = 0; i<artists.length(); i++){
                JSONObject artist = artists.getJSONObject(i);
                String seokey = artist.optString("seokey").toString().trim();
                String name = artist.optString("name").toString().trim();
                String artist_id = artist.getString("artist_id").toString().trim();

                ArrayList<String> artworks = new ArrayList<>();
                if(seokey.length() > 0 && name.length() > 0 && artist_id.length() > 0){
                    artworks.add(artist.optString("atw").toString().trim());
                    artworks.add(artist.optString("artwork").toString().trim());
                    boolean isArtworkValidated = helper.validateActiveLinks(artworks);
                    Assert.assertEquals(isArtworkValidated, true, "Artist artwork not validated succesfully!");
                }else{
                    log.error("SEO Key : "+seokey+" Artist Name : "+name+ " Artist id : "+artist_id);
                }
            }
        }
    }

    private boolean validatedata(Map<Integer, ArrayList<String>> previous_entities, Map<Integer, String[]> new_entities) {
        boolean result = false;
        for(int i = 1; i<=previous_entities.size(); i++){
            ArrayList<String> pre_response_entities = previous_entities.get(i);
            String[] new_response_entities = new_entities.get(i);
            // log.info(pre_response_entities.toString());
            for(String val : new_response_entities){
                if(!pre_response_entities.contains(val)){
                    // log.info(val);
                    result = true;
                }else{
                    result = false;
                    log.error(val+" is present in previous response as well as new response for device id : "+device_ids.get(i));
                    break;
                }
            }
        }
        return result;
    }

    private Map<Integer, ArrayList<String>> readPrevData() {
        String path = "."+Constants.CUSTOM_REPORT_FOLDER+"/Runtime/";
        String file_name = API_NAME+".csv";
        boolean isFilePresent = FileActions.fileOperation(1, path, file_name);
        if(isFilePresent)
            return CsvReader.readCsvLineWise(path + file_name);
        return null;
    }

    private void deviceIdCounter() {
        if(DEVICE_ID_COUNT == CASE_COUNT){
            DEVICE_ID_COUNT = 1;
        }else{
            DEVICE_ID_COUNT++;
        }
    }
}