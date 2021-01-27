package search_api;
import config.BaseUrls;
import config.Constants;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.testng.Assert;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.RequestHandler;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import java.util.ArrayList;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import logic_controller.AutoSuggestLiteController;
import logic_controller.AutoSuggestV2Controller;
import utils.Mailer;

public class AutoCompleteV2 extends BaseUrls{
    
    int API_CALL = 0;
    int MAX_COUNT = 0;
    String API_NAME = "AutoCompleteV2";
    ArrayList<String> KEY_INPUTS = null;
    RequestHandler request = new RequestHandler();
    Map<String, ArrayList<String>> URLS = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    Map<Integer, Response> STAGE_RESPONSES = new HashMap<>();
    Map<Integer, Response> PROD_RESPONSES = new HashMap<>();
    Map<Integer, String[]> FINAL_DATA = new HashMap<>();
    AutoSuggestV2Controller controller = new AutoSuggestV2Controller();
    private static Logger log = LoggerFactory.getLogger(AutoCompleteV2.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-41390";
    final static String REPROTING_FEATURE = "Auto-Suggest v2 Mobile comparision with production response to stage response.";
    
    @BeforeClass
    public void prepareTestEnv(){
        // System.setProperty("env", "prod");
        // System.setProperty("type", "Search");
        // System.setProperty("device_type", "android");
        baseurl();

        KEY_INPUTS = AutoSuggestLiteController.getTestData(API_NAME+".csv");
        if(KEY_INPUTS.size() > 0)
            MAX_COUNT = Constants.ASV2_INVOCATION_COUNT+1;
    }

    @Test(priority = 1, dataProvider = "key_provider", invocationCount = Constants.ASV2_INVOCATION_COUNT)
    @Link(name = "Jira Id", url = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Story("Validate response time, status code.")
    @Description("Genrate url and call api using get method to get complete response for further validations.")
    @Step("We are saving each call response in the map.")
    @Severity(SeverityLevel.BLOCKER)
    public void createGetRequestAutoCompleteV2(String keyword){
        ArrayList<String> urls = AutoSuggestV2Controller.prepareUrls(prop, keyword);
        if(urls.size() != 2)
            log.info("Staging, Production and Solr Url not generated successfully!");
            Assert.assertEquals(urls.size(), 2);
        URLS.put(keyword, urls);

        Response stage_response = request.createGetRequest(urls.get(0));
        STAGE_RESPONSES.put(API_CALL, stage_response);
        Response prod_response = request.createGetRequest(urls.get(1));
        PROD_RESPONSES.put(API_CALL, prod_response);
        
        if(API_CALL == Constants.AS_INVOCATION_COUNT){
            if(STAGE_RESPONSES.size() != PROD_RESPONSES.size()){
                Assert.assertEquals(STAGE_RESPONSES.size() == PROD_RESPONSES.size(), true, "Staging and Production responses not captured correctly!");
            }
        }

        API_CALL = handler.invocationCounter(API_CALL, MAX_COUNT);
    }

    @Test(priority = 2, dataProvider = "key_provider", invocationCount = Constants.ASV2_INVOCATION_COUNT)
    @Link(name = "Jira Id", url = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Genrate url and call api using get method to get complete response for further validations.")
    @Step("We are saving each call response in the map.")
    @Severity(SeverityLevel.CRITICAL)
    public void detailedValidateAutoCompleteV2(String keyword){
        JSONArray prod_predictions = null;
        JSONArray stage_predictions = null;

        JSONObject prod_response_ob = new JSONObject(PROD_RESPONSES.get(API_CALL).asString());
        JSONObject stage_response_ob = new JSONObject(STAGE_RESPONSES.get(API_CALL).asString());

        String prod_input = prod_response_ob.optString("input").toString().trim();
        String stage_input = stage_response_ob.optString("input").toString().trim();

        if(!prod_input.equals(stage_input) && !prod_input.equals(keyword)){
            Assert.assertEquals(prod_input.equals(stage_input), true);
        }

        try{
            prod_predictions = prod_response_ob.getJSONArray("predictions");
            stage_predictions = stage_response_ob.getJSONArray("predictions");
        }catch(Exception e){
            e.printStackTrace();
            log.error("Not able  to get predictions JSONArray!");
        }

        String prod_prediction_count = String.valueOf(prod_predictions.length());
        String stage_prediction_count = String.valueOf(stage_predictions.length());

        ArrayList<String> resultSet = controller.matchPredictionsWithProd(keyword, prod_predictions, stage_predictions);

        StringBuilder prod_predictions_result = new StringBuilder();
        prod_predictions_result.append(resultSet.get(0).toString().replaceAll("[\\[\\]\\(\\)]", " ").trim());

        StringBuilder stage_predictions_result = new StringBuilder();
        stage_predictions_result.append(resultSet.get(1).toString().replaceAll("[\\[\\]\\(\\)]", " ").trim());

        StringBuilder diff_predictions_result = new StringBuilder();
        diff_predictions_result.append(resultSet.get(2).toString().replaceAll("[\\[\\]\\(\\)]", " ").trim());

        if(diff_predictions_result.toString().length() <= 0){
            diff_predictions_result.append("N/A").toString().trim();
        }

        StringBuilder third_response_valid = new StringBuilder();
        third_response_valid.append(resultSet.get(3).toString().replaceAll("[\\[\\]\\(\\)]", " "));

        String result_set [] = {keyword, prod_prediction_count, stage_prediction_count, stage_predictions_result.toString(),
            prod_predictions_result.toString(), diff_predictions_result.toString(), third_response_valid.toString()};
        FINAL_DATA.put(API_CALL, result_set);

        if(API_CALL == Constants.ASV2_INVOCATION_COUNT && FINAL_DATA.size() > 0){
            String file_name = "AutoCompleteV2.csv";
            String head[] = { "Keyword", "Prod Prediction Count", "Stage Prediction Count", "Staging Prediction Response", "Live Prediction Response", 
                "Difference(Prod vs Stage)", "Third Object Valid" };
            AutoSuggestLiteController auto_controller = new AutoSuggestLiteController();
            auto_controller.processCsvWrite(file_name, head, FINAL_DATA);
        }

        API_CALL = handler.invocationCounter(API_CALL, MAX_COUNT);
    }

    @Test(priority = 3)
    @Link(name = "Jira Id", url = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Story("This test will push custom mailer to the defined recipients.")
    @Severity(SeverityLevel.TRIVIAL)
    public void sendEmail(){
        if(Constants.EMAILER_ENABLED == 1){
            String file_name = API_NAME+".csv";
            String scope = "Scope : This test compares stage response with production response.";
            Mailer mail = new Mailer();
            mail.sendEmail("AutoCompleteV2", file_name, scope);
        }
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
}
