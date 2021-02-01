package logic_controller;
import common.Helper;
import org.slf4j.Logger;
import config.Constants;
import config.Endpoints;
import io.qameta.allure.Step;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Properties;
import org.slf4j.LoggerFactory;

public class AutoSuggestV2Controller {
    String UNIQUE_ID = "";
    String STAGE =  "Stage";
    private static Logger log = LoggerFactory.getLogger(SearchFeedController.class);

    /**
     * preparing urls
     * @param prop
     * @param keyword
     * @return
     */
    public static ArrayList<String> prepareUrls(Properties prop, String keyword) {
        ArrayList<String> urls = new ArrayList<>();
        String stage_base_url = prop.getProperty("auto_suggest_v2_stage_url").toString().trim();
        String live_url = prop.getProperty("auto_suggest_v2_live_url").toString().trim();
        urls.add(stage_base_url+Endpoints.autoSuggestMobileV2Endpoint(keyword));
        urls.add(live_url+Endpoints.autoSuggestMobileV2Endpoint(keyword));
        return urls;
    }

	public ArrayList<String> matchPredictionsWithProd(String keyword, JSONArray prod_predictions, JSONArray stage_predictions) {
        String prodUniqueId = "";
        String stageUniqueId = "";
        ArrayList<String> results = new ArrayList<>();
        if(prod_predictions.length() <= 0 || stage_predictions.length() <= 0){
            log.info("Either prod predictions or stage predictions array length is less than or equal to zero!");
        }

        ArrayList<String> prod_predictions_list = getAllPredictions(1, keyword, prod_predictions);
        if(UNIQUE_ID.trim().length() > 0 && !UNIQUE_ID.contains(STAGE)){
            prodUniqueId = UNIQUE_ID;
        }

        ArrayList<String> stage_predictions_list = getAllPredictions(0, keyword, stage_predictions);
        if(UNIQUE_ID.trim().length() > 0 && UNIQUE_ID.contains(STAGE)){
            stageUniqueId = UNIQUE_ID;
        }

        ArrayList<String> predictions_diff_list = findDiff(prod_predictions_list, stage_predictions_list);
        results.add(prod_predictions_list.toString());
        results.add(stage_predictions_list.toString());
        results.add(predictions_diff_list.toString());
        if(Constants.IS_STAGE_LIVE == 0 && stageUniqueId.length() > 0){
            results.add("True");
        }else if(Constants.IS_STAGE_LIVE == 1 && (prodUniqueId.length() > 0 && stageUniqueId.length() > 0)){
            results.add("True");
        }else{
            results.add("False");
        }
        return results;
    }

    @Step("Getting predictions prod and stage for keyword {1}")
    private ArrayList<String> getAllPredictions(int isProd, String keyword, JSONArray predcitions) {
        ArrayList<String> prediction_list = new ArrayList<>();
        for(int i = 0; i<predcitions.length(); i++){
            JSONObject prediction = null;
            String prediction_text = null;
            try{
                prediction = predcitions.getJSONObject(i);
                prediction_text = prediction.optString("text").toString().trim();
                if(prediction_text.trim().length() <= 0)
                    prediction_text = null;
            }catch(Exception e){
                e.printStackTrace();
            }
            if(prediction_text == null && i == 2 && Constants.IS_STAGE_LIVE == 0 && isProd == 0){
                ArrayList<String> aw_list = new ArrayList<>();
                Helper helper = new Helper();
                prediction = predcitions.getJSONObject(i);
                JSONObject docObj = prediction.getJSONObject("doc");
                String iid = docObj.getString("iid").toString().trim();
                String ty = docObj.optString("ty").toString().trim();
                String ti = docObj.optString("ti").toString().trim();
                if(isProd == 0){
                    UNIQUE_ID = STAGE+"__"+iid+"__"+ty+"__"+ti;
                }else{
                    UNIQUE_ID = iid+"__"+ty+"__"+ti;
                }
                String aw = docObj.optString("aw").toString().trim();
                aw_list.add(aw);
                if(aw_list.size() > 0){
                    boolean isArtworkValid = helper.validateActiveLinks(aw_list);
                    aw_list.clear();
                    if(!isArtworkValid){
                        log.error("Keyword => "+"\""+keyword+"\""+ " artworks not active please check manually.");
                    }
                }
            }else if(i == 2 && prediction_text != null){
                UNIQUE_ID = "";
                log.error("For keyword "+"\""+keyword+"\" "+ " Expected result not found.");
            }
            
            if(prediction_text != null && prediction_text.length() > 0){
                prediction_list.add(prediction_text);
            }else{
                log.error("For keyword "+"\""+keyword+"\" "+ "prediction text is missing for counter "+i+" please validate manually!");
            }
        }
        return prediction_list;
    }

    @Step("Find difference between prod predictions : {0} and stage predictions : {1}")
    private ArrayList<String> findDiff(ArrayList<String> prod_predictions_list, ArrayList<String> stage_predictions_list) {
        ArrayList<String> diffs = new ArrayList<>();
        if(prod_predictions_list.size() > 0 && stage_predictions_list.size() > 0){
            int count = 0;
            for(String prod_prediction_text : prod_predictions_list){
                for(int i = 0; i<stage_predictions_list.size(); i++){
                    String stage_prediction_text = stage_predictions_list.get(count);
                    if(prod_prediction_text.equalsIgnoreCase(stage_prediction_text)){
                        // log.info("=> "+prod_prediction_text);
                        count++;
                        break;
                    }else if(i == (stage_predictions_list.size()-1) && !prod_prediction_text.equalsIgnoreCase(stage_prediction_text)){
                        diffs.add(prod_prediction_text);
                    }
                }
            }
        }
        return diffs;
    }
}
