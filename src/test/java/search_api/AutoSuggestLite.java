package search_api;
import java.util.Map;
import config.BaseUrls;
import config.Constants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import java.util.HashMap;
import org.testng.Assert;
import common.GlobalConfigHandler;
import java.util.ArrayList;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
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
import logic_controller.AutoSuggestLiteController;
import utils.Mailer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

public class AutoSuggestLite extends BaseUrls {

    int API_CALL = 0;
    int MAX_COUNT = 0;
    String API_NAME = "Auto_Suggest_Lite";
    ArrayList<String> KEY_INPUTS = null;
    SoftAssert softAssert = new SoftAssert();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    Map<String, ArrayList<String>> URLS = new HashMap<>();
    RequestHandler request = new RequestHandler();
    Map<Integer, Response> STAGE_RESPONSES = new HashMap<>();
    Map<Integer, Response> PROD_RESPONSES = new HashMap<>();
    Map<Integer, Response> SOLR_RESPONSES = new HashMap<>();
    ArrayList<String> PROD_ALGO_LIST = new ArrayList<>();
    ArrayList<String> STAGE_ALGO_LIST = new ArrayList<>();
    Map<Integer, String[]> FINAL_DATA = new HashMap<>();
    AutoSuggestLiteController controller = new AutoSuggestLiteController();
    private static Logger log = LoggerFactory.getLogger(AutoSuggestLite.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-40938";
    final static String REPROTING_FEATURE = "Auto-suggest api comparision with production response to stage response.";

    @BeforeClass
    public void prepareTestEnv(){
        // System.setProperty("env", "prod");
        // System.setProperty("type", "Search");
        // System.setProperty("device_type", "android");
        baseurl();
        KEY_INPUTS = AutoSuggestLiteController.getTestData(API_NAME+".csv");
        if(KEY_INPUTS.size() > 0)
            MAX_COUNT = Constants.AS_INVOCATION_COUNT+1;
    }

    @DataProvider(name = "key_provider")
    public Object [][] url(){
        if(API_CALL == 0)
            API_CALL++;

        return new Object[][]
        {
            {
                KEY_INPUTS.get(API_CALL).toString().trim()
            }
        };
    }

    @Test(priority = 1,  dataProvider = "key_provider", invocationCount = Constants.AS_INVOCATION_COUNT)
    @Link(name = "Jira Id", url = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Story("Validate response time, status code.")
    @Description("Genrate url and call api using get method to get complete response for further validations.")
    @Step("We are saving each call response in the map.")
    @Severity(SeverityLevel.BLOCKER)
    public void createGetRequestAutoSuggestCall(String keyword){
        ArrayList<String> urls = AutoSuggestLiteController.prepareUrls(prop, keyword);

        if(urls.size() != 3)
            Assert.assertEquals(urls.size(), 3, "Staging, Production and Solr Url not generated successfully!");

        URLS.put(keyword, urls);
        // log.info("Execution url generated successfully. \nStaging url : "+urls.get(0)+"\nProd url : "+urls.get(1)+"\nSolr url : "+urls.get(2));
        Response stage_response = request.createGetRequest(urls.get(0));
        STAGE_RESPONSES.put(API_CALL, stage_response);
        Response prod_response = request.createGetRequest(urls.get(1));
        PROD_RESPONSES.put(API_CALL, prod_response);
        Response solr_response = request.createGetRequest(urls.get(2));
        SOLR_RESPONSES.put(API_CALL, solr_response);

        if(API_CALL == Constants.AS_INVOCATION_COUNT){
            if(STAGE_RESPONSES.size() != PROD_RESPONSES.size() && PROD_RESPONSES.size() != SOLR_RESPONSES.size()){
                Assert.assertEquals(STAGE_RESPONSES.size() == PROD_RESPONSES.size(), true, "Staging, Production and Solr Url responses not captured correctly!");
            }
        }

        API_CALL = handler.invocationCounter(API_CALL, MAX_COUNT);
    }

    @Test(priority = 2, dataProvider = "key_provider", invocationCount = Constants.AS_INVOCATION_COUNT)
    @Link(name = "Jira Id", url = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Story("Validate common nodes present in both staging and production api response.")
    @Severity(SeverityLevel.MINOR)
    public void validateCommonFieldsInResponseBody(String keyword){
        JSONObject prod_response = new JSONObject(PROD_RESPONSES.get(API_CALL).asString());
        JSONObject stage_response = new JSONObject(STAGE_RESPONSES.get(API_CALL).asString());

        String prod_srId = controller.getOptionalJSONObject(prod_response, "srId");
        String stage_srId = controller.getOptionalJSONObject(stage_response, "srId");

        if(prod_srId.length() <= 0 || stage_srId.length() <= 0){
            Assert.assertEquals(prod_srId.length() <= 0, true, "Sr Id is empty for search key "+keyword);
        }

        String prod_q = controller.getOptionalJSONObject(prod_response, "q");
        String stage_q = controller.getOptionalJSONObject(stage_response, "q");

        if(prod_q.length() <= 0 || stage_q.length() <= 0 && prod_q.equalsIgnoreCase(keyword) || stage_q.equalsIgnoreCase(keyword)){
            Assert.assertEquals(stage_q.length() >= 0, true, "Queried key q is empty in response body!");
        }

        String prod_algo = controller.getOptionalJSONObject(prod_response, "algo");
        String stage_algo = controller.getOptionalJSONObject(stage_response, "algo");

        if(stage_algo.length() > 0 && prod_algo.length() > 0){
            PROD_ALGO_LIST.add(prod_algo);
            STAGE_ALGO_LIST.add(stage_algo);
        }else{
            PROD_ALGO_LIST.add("N/A");
            STAGE_ALGO_LIST.add("N/A");
        }

        API_CALL = handler.invocationCounter(API_CALL, MAX_COUNT);
    }

    @Test(priority = 3, dataProvider = "key_provider", invocationCount = Constants.AS_INVOCATION_COUNT)
    @Link(name = "Jira Id", url = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Story("Match both production and stage api data for difference validation.")
    @Description("Production response will be freezed and created a nested loop against stage response.")
    @Severity(SeverityLevel.CRITICAL)
    public void matchProdWithStage(String keyword){
        StringBuilder STAGING_DATA = new StringBuilder();
        StringBuilder PRODUCTION_DATA = new StringBuilder();
        StringBuilder ONLY_SATGING_DATA = new StringBuilder();
        StringBuilder DIFF_DATA = new StringBuilder();
        StringBuilder DIFF_KEY_VALUE = new StringBuilder();

        JSONObject prod_response = new JSONObject(PROD_RESPONSES.get(API_CALL).asString());
        JSONArray prod_gr = prod_response.getJSONArray("gr");
        // log.info("=>"+PROD_RESPONSES.get(API_CALL).asString().toString()+"\n\n");

        JSONObject stage_response = new JSONObject(STAGE_RESPONSES.get(API_CALL).asString());
        JSONArray stage_gr = stage_response.getJSONArray("gr");
        // log.info("=>"+STAGE_RESPONSES.get(API_CALL).asString());

        validateArrayLength(keyword, prod_gr, stage_gr);

        ArrayList<Integer> validated_prod_gr = new ArrayList<>();
        ArrayList<Integer> validated_stage_gr = new ArrayList<>();
        for(int i = 0; i< prod_gr.length(); i++){
            JSONObject prodGrObject = prod_gr.getJSONObject(i);
            String prod_gr_type = controller.getOptionalJSONObject(prodGrObject, "ty");

            for(int j = 0; j<stage_gr.length(); j++){
                JSONObject stageGrObject = stage_gr.getJSONObject(j);
                String stage_gr_type = controller.getOptionalJSONObject(stageGrObject, "ty");
                if(prod_gr_type.equalsIgnoreCase(stage_gr_type)){
                    validated_prod_gr.add(i);
                    validated_stage_gr.add(j);
                    JSONArray prodGdList = prodGrObject.getJSONArray("gd");
                    JSONArray stageGdList = stageGrObject.getJSONArray("gd");
                    validateArrayLength(keyword, prodGdList, stageGdList);

                    ArrayList<String> result_set = controller.validateGdList(prod_gr_type, keyword, prodGdList, stageGdList);
                    PRODUCTION_DATA.append(result_set.get(0).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                    STAGING_DATA.append(result_set.get(1).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                    if(result_set.get(2).length() > 0){
                        DIFF_DATA.append(result_set.get(2).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                        DIFF_KEY_VALUE.append(prod_gr_type+", ");
                    }
                    ONLY_SATGING_DATA.append(result_set.get(3).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                    break;
                }
            }
        }

        // if gr not present in stage but present on prod
        if(prod_gr.length() != validated_prod_gr.size()){
            for(int i = 0; i<prod_gr.length(); i++){
                if(!validated_prod_gr.contains(i)) {
                    JSONObject prodGrObject = prod_gr.getJSONObject(i);
                    JSONArray prodGdList = prodGrObject.getJSONArray("gd");
                    String prod_gr_type = controller.getOptionalJSONObject(prodGrObject, "ty");
                    ArrayList<String> result_set = controller.validateGdList(prod_gr_type, keyword, prodGdList, null);
                    PRODUCTION_DATA.append(result_set.get(0).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                    STAGING_DATA.append(result_set.get(1).toString().replaceAll("[\\[\\]\\(\\)]", " "));

                    if(result_set.get(2).length() > 0){
                        DIFF_DATA.append(result_set.get(2).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                        DIFF_KEY_VALUE.append(prod_gr_type+", ");
                    }
                    ONLY_SATGING_DATA.append(result_set.get(3).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                }
            }
        }

        // if stage having extra Gr Node
        if(stage_gr.length() != validated_stage_gr.size()){
            for(int i = 0; i<stage_gr.length(); i++){
                if(!validated_stage_gr.contains(i)) {
                    JSONObject stageGrObject = stage_gr.getJSONObject(i);
                    JSONArray stageGdList = stageGrObject.getJSONArray("gd");
                    String stage_gr_type = controller.getOptionalJSONObject(stageGrObject, "ty");
                    ArrayList<String> result_set = controller.validateGdList(stage_gr_type, keyword, null, stageGdList);
                    PRODUCTION_DATA.append(result_set.get(0).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                    STAGING_DATA.append(result_set.get(1).toString().replaceAll("[\\[\\]\\(\\)]", " "));

                    if(result_set.get(2).length() > 0){
                        DIFF_DATA.append(result_set.get(2).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                        DIFF_KEY_VALUE.append(stage_gr_type+", ");
                    }
                    ONLY_SATGING_DATA.append(result_set.get(3).toString().replaceAll("[\\[\\]\\(\\)]", " "));
                }
            }
        }

        String solr_val = "N/A";
        Response solr_res = SOLR_RESPONSES.get(API_CALL);
        if(solr_res != null){
            solr_val = SOLR_RESPONSES.get(API_CALL).asString();
        }

        // String keyword = testdatainputs.get(EX_COUNT).toString().trim();

        String prod = PRODUCTION_DATA.toString().trim();
        String stage = STAGING_DATA.toString().trim();

        String onlyStage = "N/A";
        if(ONLY_SATGING_DATA.toString().trim().length() > 0){
            onlyStage = ONLY_SATGING_DATA.toString().trim();
        }

        String diff_found = "N/A";
        if(DIFF_DATA.toString().trim().length() > 0){
            diff_found = DIFF_DATA.toString().trim();
        }

        String diff_keys = DIFF_KEY_VALUE.toString();

        String algo = "N/A";
        if(PROD_ALGO_LIST.size() == STAGE_ALGO_LIST.size()){
            String prodAlgoName = PROD_ALGO_LIST.get(API_CALL-1).trim();
            String stageAlgoName = STAGE_ALGO_LIST.get(API_CALL-1).trim();
            if(!prodAlgoName.equals("N/A")){
                algo = stageAlgoName +" | "+ prodAlgoName;
            }
        }

        String result_val [] = {keyword, solr_val, onlyStage, stage, prod, diff_found, diff_keys, algo};
        FINAL_DATA.put(API_CALL, result_val);

        if(API_CALL == Constants.AS_INVOCATION_COUNT && FINAL_DATA.size() > 0)
            controller.processCsvWrite(FINAL_DATA);
        API_CALL = handler.invocationCounter(API_CALL, MAX_COUNT);
    }

    @Test(priority = 4)
    @Link(name = "Jira Id", url = "https://timesgroup.jira.com/browse/GAANA-40938")
    @Feature(REPROTING_FEATURE)
    @Story("This test will push custom mailer to the defined recipients.")
    @Severity(SeverityLevel.TRIVIAL)
    public void sendEmail(){
        if(Constants.EMAILER_ENABLED == 1){
            String file_name = "AutoSuggestLite.csv";
            String scope = "Scope : This suite compares stage response with production response.";
            Mailer mail = new Mailer();
            mail.sendEmail("AutoSuggestLite", file_name, scope);
        }
    }

    // private ArrayList<Integer> getMissingIndexId(int length, ArrayList<Integer> visited_indexes) {
    //     ArrayList<Integer> ex_index_values = new ArrayList<>();
    //     ArrayList<Integer> not_visited_indexes = new ArrayList<>();

    //     for(int i = 0; i<length; i++){
    //         ex_index_values.add(i);
    //     }

    //     for(int index : ex_index_values){
    //         if(!visited_indexes.contains(index)){
    //             not_visited_indexes.add(index);
    //         }
    //     }

    //     return not_visited_indexes;
    // }

    /**
     * Array length should be greater than zero
     * @param keyword
     * @param prod
     * @param stage
     */
    public void validateArrayLength(String keyword, JSONArray prod, JSONArray stage){
        if(prod.length() <= 0){
            softAssert.assertEquals(prod.length() <= 0, true);
            log.info("Production array can't be less than zero for keyword : "+keyword);
            controller.printUrls(URLS.get(keyword));
        }else if(stage.length() <= 0){
            softAssert.assertEquals(stage.length() <= 0, true);
            log.info("Staging array can't be less than zero for keyword : "+keyword);
            controller.printUrls(URLS.get(keyword));
        }
        softAssert.assertAll();
    }
}
