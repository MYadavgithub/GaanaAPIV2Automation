package search_api;
import java.util.Map;
import config.BaseUrls;
import config.Constants;
import utils.CsvReader;
import utils.Mailer;
import utils.WriteCsv;
import config.Endpoints;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.List;
import org.testng.Assert;
import common.FileActions;
import common.Helper;
import java.util.ArrayList;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

public class AutoSuggestLite extends BaseUrls {

    int EX_EXE_COUNT = 0;
    int EX_COUNT = 0;
    String STAGE_URL = "";
    String PROD_URL = "";
    String SOLR_URL = "";
    String API_NAME = "Auto_Suggest_Lite";
    ArrayList<String> testdatainputs = null;
    Helper helper = new Helper();
    RequestHandler req = new RequestHandler();
    private static Logger log = LoggerFactory.getLogger(AutoSuggestLite.class);
    Map<Integer, Response> stage_responses = new HashMap<>();
    Map<Integer, Response> solr_responses = new HashMap<>();
    Map<Integer, Response> prod_responses = new HashMap<>();
    Map<Integer, ArrayList<String>> urls = new HashMap<>();
    Map<Integer, String[]> result = new HashMap<>();

    @BeforeClass
    public void prepareTestEnv(){
        // System.setProperty("env", "local");
        // System.setProperty("type", "Search");
        // System.setProperty("device_type", "android");
        getTestData();
        if(testdatainputs.size() > 0)
            EX_EXE_COUNT = testdatainputs.size();
    }

    @DataProvider(name = "key_provider")
    public Object [][] url(){
        return new Object[][]
        {
            {
                testdatainputs.get(EX_COUNT).toString().trim()
            }
        };
    }

    @Test(priority = 1, dataProvider = "key_provider", invocationCount = Constants.AS_INVOCATION_COUNT)
    public void prepareUrl(String key) {
        ArrayList<String> url = new ArrayList<>();
        key = key.replaceAll("\\s", "%20");
        if(EX_COUNT != 0 && !key.equalsIgnoreCase("Keyword")){
            prepareStageLiveUrls(key);
            if(STAGE_URL.length() > 0 && PROD_URL.length() > 0){
                url.add(STAGE_URL);
                url.add(PROD_URL);
                url.add(SOLR_URL);
                urls.put(EX_COUNT, url);
                log.info("Execution url generated successfully. \nStaging url : "+STAGE_URL+"\nSolr url : "+SOLR_URL+"\nProd url : "+PROD_URL);
                Response stage_response = req.createGetRequest(STAGE_URL);
                stage_responses.put(EX_COUNT, stage_response);
                Response solr_response = req.createGetRequest(SOLR_URL);
                solr_responses.put(EX_COUNT, solr_response);
                Response prod_response = req.createGetRequest(PROD_URL);
                prod_responses.put(EX_COUNT, prod_response);
            }else{
                Assert.assertEquals(STAGE_URL.length() > 0, true, "Stage url can't be null.");
                Assert.assertEquals(PROD_URL.length() > 0, true, "PROD url can't be null.");
            }
        }else{
            log.info("Expected key is column name in Data provider CSV key was : "+key);
        }
        setAndRestCounter();
    }

    @Test(priority = 2, invocationCount = Constants.AS_INVOCATION_COUNT)
    public void validateCommonResponseObjects(){

        if(EX_COUNT != 0){

            if(stage_responses.size() != prod_responses.size()){
                Assert.assertEquals(stage_responses.size() == prod_responses.size(), true, "Stage and Prod responses are not able to process further due to mismatch!");
            }

            JSONObject stage_response_object = new JSONObject(stage_responses.get(EX_COUNT).asString());
            JSONObject prod_response_object = new JSONObject(stage_responses.get(EX_COUNT).asString());

            String stage_srId = getOptionalJSONObject(stage_response_object, "srId");
            String prod_srId = getOptionalJSONObject(prod_response_object, "srId");
        
            String stage_q = getOptionalJSONObject(stage_response_object, "q");
            String prod_q = getOptionalJSONObject(prod_response_object, "q");

            if(stage_srId.length() <= 0 && prod_srId.length() <= 0){
                boolean srIdValid = false;
                srIdValid = stage_srId.length() > 0;
                srIdValid = prod_srId.length() > 0;
                Assert.assertEquals(srIdValid, true, "Sr Id is empty for search key "+testdatainputs.get(EX_COUNT));
            }

            if(!stage_q.equals(prod_q) && !prod_q.equalsIgnoreCase(testdatainputs.get(EX_COUNT))){
                Assert.assertEquals(stage_q.equals(prod_q), true, "Queried key q is empty in response body!");
            }
        }

        setAndRestCounter();
    }

    @Test(priority = 3, invocationCount = Constants.AS_INVOCATION_COUNT)
    public void matchStageAndProdResponses() {

        if(EX_COUNT != 0){

            JSONObject stage_response_object = new JSONObject(stage_responses.get(EX_COUNT).asString());
            JSONArray stage_gr = stage_response_object.getJSONArray("gr");

            JSONObject prod_response_object = new JSONObject(stage_responses.get(EX_COUNT).asString());
            JSONArray prod_gr = prod_response_object.getJSONArray("gr");

            if(stage_gr.length() != prod_gr.length()){
                Assert.assertEquals(stage_gr.length() == prod_gr.length(), true, "Gr length for stage and prod not matched comparison not possible!");
            }

            StringBuilder _stage_val = new StringBuilder();
            StringBuilder _prod_val = new StringBuilder();
            StringBuilder _diff_val = new StringBuilder();

            String type = "";
            for(int i = 0; i<stage_gr.length(); i++){
                JSONArray stage_gd_value = null;
                JSONArray prod_gd_value = null;

                JSONObject stage_gr_value = stage_gr.getJSONObject(i);
                JSONObject prod_gr_value = stage_gr.getJSONObject(i);
                type = validateType(stage_gr_value, prod_gr_value);

                if(type.length() > 0){
                    try{
                        stage_gd_value = stage_gr_value.getJSONArray("gd");
                        prod_gd_value = prod_gr_value.getJSONArray("gd");
                        Assert.assertEquals(stage_gd_value.length() == prod_gd_value.length(), true, "Gd length for stage and prod not matched comparison not possible!");
                    }catch(Exception e){
                        log.error("Gd Not found for requested key : "+testdatainputs.get(EX_COUNT));
                        e.printStackTrace();
                    }
                }

                ArrayList<String> values = validateGdValues(stage_gd_value, prod_gd_value);
                _stage_val.append(values.get(0));
                _prod_val.append(values.get(1));
                _diff_val.append(values.get(2)); 
            }

            String solr_val = "N/A";
            String keyword = testdatainputs.get(EX_COUNT).toString().trim();

            Response solr_res = solr_responses.get(EX_COUNT);
            if(solr_res != null){
                solr_val = solr_responses.get(EX_COUNT).asString();
            }

            String result_val [] = {keyword, solr_val, _stage_val.toString(), _prod_val.toString(), _diff_val.toString(), type};
            result.put(EX_COUNT, result_val);
        }
        setAndRestCounter();
        processCsvWrite(result);
    }

    @Test(priority = 4)
    public void sendEmail(){
        String file_name = "AutoSuggestLite.csv";
        Mailer mail = new Mailer();
        mail.sendEmail("AutoSuggestLite", file_name);
    }

    private void processCsvWrite(Map<Integer, String[]> result) {
        String file_name = "AutoSuggestLite.csv";
        String head[] = { "Keyword", "ErSolr", "Staging Response", "Live Response", "Difference", "Title(Algo)" };
        WriteCsv.writeCsvWithHeader(file_name, head, result);
    }

    private ArrayList<String> validateGdValues(JSONArray stage, JSONArray prod) {
        String stageUniqueId = "";
        String prodUniqueId = "";

        ArrayList<String> stage_res_obj = new ArrayList<>();
        ArrayList<String> prod_res_obj = new ArrayList<>();
        ArrayList<String> diff_key = new ArrayList<>();

        for(int i = 0; i<stage.length(); i++){
            JSONObject stage_gd_object = stage.getJSONObject(i);
            JSONObject prod_gd_object = prod.getJSONObject(i);
            List<Object> keys = helper.keys(prod_gd_object);

            if(!keys.contains("innerGdList")){
                String stage_gd_type = getOptionalJSONObject(stage_gd_object, "ty");
                String prod_gd_type = getOptionalJSONObject(prod_gd_object, "ty");

                stageUniqueId = stage_gd_object.getString("iid").toString().trim()+"_"+stage_gd_type;
                prodUniqueId = prod_gd_object.getString("iid").toString().trim()+"_"+prod_gd_type;

                String stage_title = getOptionalJSONObject(stage_gd_object, "ti").trim();
                String prod_title = getOptionalJSONObject(prod_gd_object, "ti").trim();

                if(!stageUniqueId.equals(prodUniqueId)){
                    diff_key.add(prodUniqueId+"__"+prod_title);
                }

                stage_res_obj.add(stage_title);
                prod_res_obj.add(prod_title);

            }else{
                System.out.println("sdhfhjsgfjsgfghjsgfj");
                //inner GD LIST
            }
        }

        ArrayList<String> result_set = new ArrayList<>();
        result_set.add(stage_res_obj.toString());
        result_set.add(prod_res_obj.toString());
        result_set.add(diff_key.toString());
        return result_set;
    }

    private String validateType(JSONObject stage_gr_value, JSONObject prod_gr_value) {
        String type = "";
        String stage_gr_type = getOptionalJSONObject(stage_gr_value, "ty");
        String prod_gr_type = getOptionalJSONObject(prod_gr_value, "ty");

        if(stage_gr_type.equalsIgnoreCase(prod_gr_type)){
            type = stage_gr_type;
        }else{
            Assert.assertEquals(stage_gr_type.equalsIgnoreCase(prod_gr_type), true, "Gr type not matched!");
        }
        return type;
    }

    protected String getOptionalJSONObject(JSONObject response, String param) {
        try{
            return response.optString(param).toString().trim().replaceAll("%20", " ");
        }catch(Exception e){
            log.error("Expected Optional JSON Object not found key was : "+param);
            e.printStackTrace();
        }
        return null;
    }

    protected JSONObject getJSONObject(String response, String param) {
        try{
            JSONObject response_object = new JSONObject(response);
            return response_object.getJSONObject(param);
        }catch(Exception e){
            log.error("Expected JSON Object not found : "+param);
            e.printStackTrace();
        }
        return null;
    }

    protected JSONArray getJSONArray(String response, String param) {
        try{
            JSONObject response_object = new JSONObject(response);
            return response_object.getJSONArray(param);
        }catch(Exception e){
            log.error("Expected JSON Array not found : "+param);
            e.printStackTrace();
        }
        return null;
    }

    protected void prepareStageLiveUrls(String params) {
        String stage_base_url = baseurl();
        String live_url = prop.getProperty("search_live_url").toString().trim();
        String end_point = genrateEndpoint(params);
        STAGE_URL = stage_base_url+end_point;
        PROD_URL = live_url+end_point;
        SOLR_URL = stage_base_url+"/gaanasearch-api/mobilesuggest/getErSolr?query=" + params + "&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English&testing=1";
    }

    protected String genrateEndpoint(String query) {
        String end_point = Endpoints.autoSuggestLite;
        StringBuilder query_params = new StringBuilder();
        query_params.append("query="+query+"&UserType=0&geoLocation=IN&content_filter=2");
        query_params.append("&include=allItems&isRegSrch=0&usrLang=Hindi,English&testing=1&autocomplete=1");
        return end_point+query_params.toString(); 
    }

    private void getTestData() {
        String file_name_prev_data = API_NAME + ".csv";
        String file_path = "./src/test/resources/data/Search_Data/";
        boolean isFilePresent = FileActions.fileOperation(1, file_path, file_name_prev_data);
        if(isFilePresent)
            testdatainputs = CsvReader.readCsv(file_path + file_name_prev_data);
    }

    private void setAndRestCounter(){
        EX_COUNT++;
        if(EX_COUNT == Constants.AS_INVOCATION_COUNT){
            EX_COUNT = 0;
        }
    }
}
