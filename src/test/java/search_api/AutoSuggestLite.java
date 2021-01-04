package search_api;
import java.util.Map;
import config.BaseUrls;
import config.Constants;
import utils.CsvReader;
import utils.Mailer;
import common.Helper;
import utils.WriteCsv;
import config.Endpoints;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.testng.Assert;
import common.FileActions;
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
        // System.setProperty("env", "prod");
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
            JSONObject prod_response_object = new JSONObject(prod_responses.get(EX_COUNT).asString());

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
        StringBuilder STAGE_VALUE = new StringBuilder();
        StringBuilder PROD_VALUE = new StringBuilder();
        StringBuilder DIFF_VALUE = new StringBuilder();
        StringBuilder DIFF_KEY_VALUE = new StringBuilder();

        if(EX_COUNT != 0){

            JSONObject stage_response_object = new JSONObject(stage_responses.get(EX_COUNT).asString());
            JSONArray stage_gr = stage_response_object.getJSONArray("gr");

            JSONObject prod_response_object = new JSONObject(prod_responses.get(EX_COUNT).asString());
            JSONArray prod_gr = prod_response_object.getJSONArray("gr");

            ArrayList<Integer> validated_gr_data = new ArrayList<>();

            for(int i = 0; i<prod_gr.length(); i++){
                JSONObject prod_gr_object = prod_gr.getJSONObject(i);
                String prod_gr_type_title = prod_gr_object.optString("ty").toString().trim();

                for(int j = 0; j<stage_gr.length(); j++){
                    JSONObject stage_gr_object = stage_gr.getJSONObject(j);
                    String stage_gr_type_title = stage_gr_object.optString("ty").toString().trim();
                    if(stage_gr_type_title.equals(prod_gr_type_title)){
                        validated_gr_data.add(i); // for ext index validations
                        JSONArray prod_gd_array = prod_gr_object.getJSONArray("gd");
                        JSONArray stage_gd_array = stage_gr_object.getJSONArray("gd");
                        // System.out.println("Prod GD = "+prod_gd_array);
                        // System.out.println("Stage GD = "+stage_gd_array);

                        ArrayList<String> comparison_result = validateGdData(prod_gr_type_title, prod_gd_array, stage_gd_array);
                        STAGE_VALUE.append(comparison_result.get(0).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                        PROD_VALUE.append(comparison_result.get(1).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                        if(comparison_result.get(2).length() > 0){
                            DIFF_VALUE.append(comparison_result.get(2).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                            DIFF_KEY_VALUE.append(prod_gr_type_title+", ");
                        }
                        break;
                    }
                }
            }

            // Prod having more data
            if(prod_gr.length() != validated_gr_data.size()){
                ArrayList<Integer> extra_gr_indexes = getMissingIndexId(prod_gr.length(), validated_gr_data);
                for(int index : extra_gr_indexes){
                    JSONObject ext_prod_gr_object = prod_gr.getJSONObject(index);
                    String type_title = ext_prod_gr_object.optString("ty").toString().trim();
                    JSONArray prod_gd_array = ext_prod_gr_object.getJSONArray("gd");
                    ArrayList<String> comparison_result = validateGdData(type_title, prod_gd_array, null);
                    STAGE_VALUE.append(comparison_result.get(0).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                    PROD_VALUE.append(comparison_result.get(1).toString().replaceAll("[\\[\\]\\(\\)]", ", "));
                    if(comparison_result.get(2).length() > 0){
                        DIFF_VALUE.append(comparison_result.get(2).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                        DIFF_KEY_VALUE.append(type_title+", ");
                    }
                }
            }
            // System.out.println("Stage : "+STAGE_VALUE.toString().trim());
            // System.out.println("Prod : "+PROD_VALUE.toString().trim());
            // System.out.println("Diff : "+DIFF_VALUE.toString().trim());
            // System.out.println(DIFF_KEY_VALUE.toString().trim());

            String solr_val = "N/A";
            String keyword = testdatainputs.get(EX_COUNT).toString().trim();

            Response solr_res = solr_responses.get(EX_COUNT);
            if(solr_res != null){
                solr_val = solr_responses.get(EX_COUNT).asString();
            }

            String stage = STAGE_VALUE.toString().trim();
            String prod = PROD_VALUE.toString().trim();

            String diff_found = "N/A";
            if(DIFF_VALUE.toString().trim().length() > 0){
                diff_found = DIFF_VALUE.toString().trim();
            }

            String diff_keys = DIFF_KEY_VALUE.toString();

            String result_val [] = {keyword, solr_val, stage, prod, diff_found, diff_keys};
            result.put(EX_COUNT, result_val);
        }
        setAndRestCounter();
        if(EX_COUNT == 0 && result.size() > 0)
            processCsvWrite(result);
    }

    @Test(priority = 4)
    public void sendEmail(){
        if(Constants.EMAILER_ENABLED == 1){
            String file_name = "AutoSuggestLite.csv";
            String scope = "Scope : This suite compares stage response with production response.";
            Mailer mail = new Mailer();
            mail.sendEmail("AutoSuggestLite", file_name, scope);
        }
    }

    private void processCsvWrite(Map<Integer, String[]> result) {
        String file_name = "AutoSuggestLite.csv";
        String head[] = { "Keyword", "ErSolr", "Staging Response", "Live Response", "Difference", "Title(Algo)" };
        WriteCsv.writeCsvWithHeader(file_name, head, result);
    }

    private ArrayList<String> validateGdData(String type_title, JSONArray prod_gds, JSONArray stage_gds) {
        boolean isMix = false;
        String stageUniqueId = "";
        String prodUniqueId = "";
        ArrayList<String> prod_res_obj = new ArrayList<>();
        ArrayList<String> stage_res_obj = new ArrayList<>();
        ArrayList<String> diff_key = new ArrayList<>();

        if(prod_gds == null){
            Assert.assertEquals(prod_gds != null, true, "Prod Gd can't be null!");
        }

        if(type_title.equalsIgnoreCase("Mix")){
            isMix = true;
        }

        if(stage_gds == null){
            if(prod_gds.length() > 0){
                for(int i = 0; i <prod_gds.length(); i++){
                    JSONObject prod_gd_object = prod_gds.getJSONObject(i);
                    String object_type_title = prod_gd_object.optString("ty").toString().trim();
                    if(object_type_title.equals(type_title) || isMix == true){
                        List<Object> keys = helper.keys(prod_gd_object);
                        if(!keys.contains("innerGdList")){
                            prodUniqueId = prod_gd_object.getString("iid").toString().trim()+"_"+object_type_title;
                            String prod_gd_object_title = prod_gd_object.optString("ti").toString().trim();
                            if(prodUniqueId.length() > 0){
                                prod_res_obj.add(prod_gd_object_title);
                                diff_key.add(prodUniqueId+"__"+prod_gd_object_title);
                            }
                        }else{
                            JSONArray prodInnerGdList = prod_gd_object.getJSONArray("innerGdList");
                            if(prodInnerGdList.length() > 0){
                                Iterator<Object> itr = prodInnerGdList.iterator();
                                while(itr.hasNext()){
                                    JSONObject prodInnerGdObject = (JSONObject) itr.next();
                                    if(prodInnerGdObject.length() > 0){
                                        for(int g = 0; g<prodInnerGdObject.length(); g++){
                                            String o_type_title = prodInnerGdObject.optString("ty").toString().trim();
                                            prodUniqueId = prodInnerGdObject.getString("iid").toString().trim()+"_"+o_type_title;
                                            String prod_gd_object_title = prodInnerGdObject.optString("ti").toString().trim();
                                            if(prodUniqueId.length() > 0){
                                                prod_res_obj.add(prod_gd_object_title);
                                                diff_key.add(prodUniqueId+"__"+prod_gd_object_title);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        Assert.assertEquals(object_type_title, type_title, "Gr title should match with gr objects title but its not matching!");
                    }
                }
            }
        }else if(prod_gds.length() > 0 && stage_gds.length() > 0){
            for(int i = 0; i<prod_gds.length(); i++){
                JSONObject prod_gd_object = prod_gds.getJSONObject(i);
                List<Object> keys = helper.keys(prod_gd_object);

                for(int j = 0; j<stage_gds.length(); j++){
                    // if prod & stage both present
                    String stage_gd_type = "";
                    JSONObject stage_gd_object = null;

                    try{
                        stage_gd_object = stage_gds.getJSONObject(i);
                    }catch(Exception e){
                        log.error("No stage object found!");
                    }

                    String prod_gd_type = getOptionalJSONObject(prod_gd_object, "ty");

                    if(stage_gd_object != null){
                        stage_gd_type = getOptionalJSONObject(stage_gd_object, "ty");
                    }

                    if(prod_gd_type.equals(stage_gd_type) && (prod_gd_type.equals(type_title) || isMix == true)){
                        if(!keys.contains("innerGdList")){
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
                            try{
                                JSONArray stageInnerGdList = new JSONArray();
                                JSONArray prodInnerGdList = prod_gd_object.getJSONArray("innerGdList");
                                try{
                                    stageInnerGdList = stage_gd_object.getJSONArray("innerGdList");
                                }catch(Exception e){
                                    log.error("Stage doesn't having inner gd list.");
                                }

                                if(prodInnerGdList.length() > 0){
                                    Iterator<Object> itr = prodInnerGdList.iterator();
                                    while(itr.hasNext()){
                                        JSONObject prodInnerGdObject = (JSONObject) itr.next();
                                        if(stageInnerGdList.length() > 0){
                                            for(int g = 0; g<stageInnerGdList.length(); g++){
                                                JSONObject stageInnerGdObject = stageInnerGdList.getJSONObject(g);
                                                prodUniqueId = prodInnerGdObject.getString("iid").toString().trim()+"_"+prod_gd_type;
                                                stageUniqueId = stageInnerGdObject.getString("iid").toString().trim()+"_"+stage_gd_type;

                                                if(prodUniqueId.equals(stageUniqueId)){
                                                    String stage_title = getOptionalJSONObject(stageInnerGdObject, "ti").trim();
                                                    String prod_title = getOptionalJSONObject(prodInnerGdObject, "ti").trim();

                                                    if(!stageUniqueId.equals(prodUniqueId)){
                                                        diff_key.add(prodUniqueId+"__"+prod_title);
                                                    }

                                                    stage_res_obj.add(stage_title);
                                                    prod_res_obj.add(prod_title);
                                                    break;
                                                }
                                            }
                                        }else{
                                            String object_type_title = prodInnerGdObject.optString("ty").toString().trim();
                                            prodUniqueId = prodInnerGdObject.getString("iid").toString().trim()+"_"+object_type_title;
                                            String prod_gd_object_title = prodInnerGdObject.optString("ti").toString().trim();
                                            if(prodUniqueId.length() > 0){
                                                prod_res_obj.add(prod_gd_object_title);
                                                diff_key.add(prodUniqueId+"__"+prod_gd_object_title);
                                            }
                                        }
                                    }
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                        break;
                    }else if(stage_gd_type.length() <= 0){
                        prodUniqueId = prod_gd_object.getString("iid").toString().trim()+"_"+prod_gd_type;
                        String prod_title = getOptionalJSONObject(prod_gd_object, "ti").trim();
                        diff_key.add(prodUniqueId+"__"+prod_title);
                        prod_res_obj.add(prod_title);
                        break;
                    }
                }
            }
        }

        ArrayList<String> result_set = new ArrayList<>();
        result_set.add(stage_res_obj.toString());
        result_set.add(prod_res_obj.toString());
        result_set.add(diff_key.toString());
        return result_set;
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
        STAGE_URL = stage_base_url+Endpoints.autoSuggestStageEndpoint(params);
        PROD_URL = live_url+Endpoints.autoSuggestProdEndpoint(params);
        SOLR_URL = stage_base_url+Endpoints.autoSuggestSolrEndpoint(params);
    }

    private void getTestData() {
        String file_name_prev_data = API_NAME + ".csv";
        String file_path = "./src/test/resources/data/Search_Data/";
        boolean isFilePresent = FileActions.fileOperation(1, file_path, file_name_prev_data);
        if(isFilePresent)
            testdatainputs = CsvReader.readCsv(file_path + file_name_prev_data);
    }

    private ArrayList<Integer> getMissingIndexId(int length, ArrayList<Integer> visited_indexes) {
        ArrayList<Integer> ex_index_values = new ArrayList<>();
        ArrayList<Integer> not_visited_indexes = new ArrayList<>();

        for(int i = 0; i<length; i++){
            ex_index_values.add(i);
        }

        for(int index : ex_index_values){
            if(!visited_indexes.contains(index)){
                not_visited_indexes.add(index);
            }
        }

        return not_visited_indexes;
    }

    private void setAndRestCounter(){
        EX_COUNT++;
        if(EX_COUNT == Constants.AS_INVOCATION_COUNT){
            EX_COUNT = 0;
        }
    }
}
