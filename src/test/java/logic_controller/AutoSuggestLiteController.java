package logic_controller;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import common.CommonPath;
import common.FileActions;
import common.Helper;
import config.Endpoints;
import io.qameta.allure.Step;
import search_api.AutoSuggestLite;
import utils.CsvReader;
import utils.WriteCsv;

public class AutoSuggestLiteController {

    String KEYWORD = "";
    String STAGE_IDENTIFIER = "STAGE";
    String STAGE_NOT_VALIDATED = "NOT_FOUND";
    Helper helper = new Helper();
    ArrayList<Integer> prod_gd_validated = new ArrayList<>();
    ArrayList<Integer> stage_gd_validated = new ArrayList<>();
    ArrayList<Integer> prod_InnerGd_validated = new ArrayList<>();
    ArrayList<Integer> stage_InnerGd_validated = new ArrayList<>();
    ArrayList<String> prod_res_data = new ArrayList<>();
    ArrayList<String> stage_res_data = new ArrayList<>();
    ArrayList<String> diff_data = new ArrayList<>();
    ArrayList<String> stage_ext_data = new ArrayList<>();
    ArrayList<String> artworks = new ArrayList<>();
    private static Logger log = LoggerFactory.getLogger(SearchFeedController.class);

    /**
     * Get Test Data from Specific Sheet
     * @return
     */
    public static ArrayList<String> getTestData(String fileName) {
        String file_path = CommonPath.AUTO_QUEUE_KEYWORD_PROVIDER_PATH;
        boolean isFilePresent = FileActions.fileOperation(1, file_path, fileName);
        if(isFilePresent)
           return CsvReader.readCsv(file_path + fileName);
        return null;
    }

    /**
     * preparing urls
     * @param prop
     * @param keyword
     * @return
     */
    public static ArrayList<String> prepareUrls(Properties prop, String keyword) {
        ArrayList<String> urls = new ArrayList<>();
        // keyword = keyword.replaceAll("\\s", "%20");
        String stage_base_url = prop.getProperty("search_baseurl").toString().trim();
        String live_url = prop.getProperty("search_live_url").toString().trim();
        urls.add(stage_base_url+Endpoints.autoSuggestStageEndpoint(keyword));
        urls.add(live_url+Endpoints.autoSuggestProdEndpoint(keyword));
        urls.add(stage_base_url+Endpoints.autoSuggestSolrEndpoint(keyword));
        return urls;
    }

    /**
     * Get Optional to String value
     * @param response
     * @param param
     * @return
     */
    public String getOptionalJSONObject(JSONObject response, String param) {
        try{
            return response.optString(param).toString().trim().replaceAll("%20", " ");
        }catch(Exception e){
            log.error("Expected Optional JSON Object not found key was : "+param);
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Print Inline urls
     * @param arrayList
     */
	public void printUrls(ArrayList<String> urllist) {
        log.info("Stage Url : "+urllist.get(0));
        log.info("Production Url : "+urllist.get(1));
        log.info("Solr Url : "+urllist.get(2));
    }
    /**
     * Flag 0 = Staging Data , Flag = 1 Production Data
     * @param flag
     * @param prodGdObj
     */
    private void getOnlyProdData(int flag, JSONObject gdObj) {
        String gdObject_type = getOptionalJSONObject(gdObj, "ty");
        String iid = gdObj.getString("iid").toString().trim();
        String uniqueId = iid+"_"+gdObject_type;
        String gdTitle = getOptionalJSONObject(gdObj, "ti").trim();
        if(flag == 1){
            diff_data.add(uniqueId+"__"+gdTitle);
            prod_res_data.add(iid+"__"+gdTitle);
        }else if(flag == 0){
            stage_ext_data.add(iid+"__"+gdTitle);
        }
        String prod_gd_aw = getOptionalJSONObject(gdObj, "aw").trim();
        artworks.add(prod_gd_aw);
    }
    
    /**
     * @param prod_gr_type
     * @param keyword
     * @param prodGdList
     * @param stageGdList
     * @return
     */
    public ArrayList<String> validateGdList(String gr_type, String keyword, JSONArray prodGdList, JSONArray stageGdList) {
        // log.info("Gr Type :"+gr_type);
        KEYWORD = keyword;
        boolean isMix = gr_type.equalsIgnoreCase("Mix");
        if(prodGdList != null){
            for(int i = 0; i<prodGdList.length(); i++){
                JSONObject prodGdObject = prodGdList.getJSONObject(i);
                
                if(stageGdList == null){
                    int gdCount = 0;
                    if(prodGdList.length() > 0){
                        Iterator<Object> prodItrObj = prodGdList.iterator();
                        while(prodItrObj.hasNext()){
                            JSONObject prodGdObj = (JSONObject) prodItrObj.next();
                            List<Object> prod_keys = helper.keys(prodGdObj);
                            String prodGrTy = getOptionalJSONObject(prodGdObj, "ty").trim();
                            boolean isTypeValidated = gr_type.equalsIgnoreCase(prodGrTy);
                            if(!prod_keys.contains("innerGdList") && (isTypeValidated || isMix)){
                                prod_gd_validated.add(gdCount);
                                getOnlyProdData(1, prodGdObj);
                            }else if (prod_keys.contains("innerGdList") && (isTypeValidated || isMix)){
                                // System.out.println(prodGdObj);
                                JSONArray prodInnerGdArr =  prodGdObj.getJSONArray("innerGdList");
                                if(prodInnerGdArr.length() > 0){
                                    Iterator<Object> innerGdItr = prodInnerGdArr.iterator();
                                    while(innerGdItr.hasNext()){
                                        JSONObject innerGdOb = (JSONObject) innerGdItr.next();
                                        String prodInnerGdTy = getOptionalJSONObject(innerGdOb, "ty").trim();

                                        if(gr_type.equals(prodInnerGdTy) || isMix){
                                            getOnlyProdData(1, innerGdOb);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else if(stageGdList.length() > 0){
                    for(int j = 0; j<stageGdList.length(); j++){
                        List<Object> prod_keys = helper.keys(prodGdObject);
                        JSONObject stageGdObject = stageGdList.getJSONObject(j);
                        String stageGrType = getOptionalJSONObject(stageGdObject, "ty").trim();
                        boolean isTypeValidated = gr_type.equalsIgnoreCase(stageGrType);
                        if(!prod_keys.contains("innerGdList") && (isTypeValidated || isMix) && stageGrType.length() > 0){
                            boolean isInnerGd = false;
                            String isDataValid = validateGdDetails(isInnerGd, i, j, prodGdObject, stageGdObject);
                            if(!isDataValid.equals(STAGE_NOT_VALIDATED) && isDataValid.contains(STAGE_IDENTIFIER)){
                                // System.out.println("Stage : "+stage_res_data.toString());
                                // System.out.println("Prod : "+prod_res_data.toString());
                                // System.out.println("Diff : "+diff_data.toString());
                                break;
                            }
                        }else if(prod_keys.contains("innerGdList") && (isTypeValidated || isMix)){
                            String prodGdTi =  getOptionalJSONObject(prodGdObject, "ti");
                            String stageGdTi =  getOptionalJSONObject(stageGdObject, "ti");
                            List<Object> stage_keys = helper.keys(stageGdObject);
                            if(prodGdTi.equalsIgnoreCase(stageGdTi) && stage_keys.contains("innerGdList")){
                                validateInnerGdValues(gr_type, isMix, prodGdObject, stageGdObject);
                                break;
                            }
                        }
                    }
                }
            }
        }else {
            if(stageGdList.length() > 0){
                Iterator<Object> stageItrObj = stageGdList.iterator();
                int gdCount = 0;
                while(stageItrObj.hasNext()){
                    JSONObject stageGdObj = (JSONObject) stageItrObj.next();
                    List<Object> stage_keys = helper.keys(stageGdObj);
                    String stageGrTy = getOptionalJSONObject(stageGdObj, "ty").trim();
                    boolean isTypeValidated = gr_type.equalsIgnoreCase(stageGrTy);
                    if(!stage_keys.contains("innerGdList") && (isTypeValidated || isMix)){
                        stage_gd_validated.add(gdCount);
                        getOnlyProdData(0, stageGdObj);
                    }else if (stage_keys.contains("innerGdList") && (isTypeValidated || isMix)){
                        // System.out.println(stageGdObj);
                        JSONArray stageInnerGdArr =  stageGdObj.getJSONArray("innerGdList");
                        if(stageInnerGdArr.length() > 0){
                            Iterator<Object> innerGdItr = stageInnerGdArr.iterator();
                            while(innerGdItr.hasNext()){
                                JSONObject innerGdOb = (JSONObject) innerGdItr.next();
                                String stageInnerGdTy = getOptionalJSONObject(innerGdOb, "ty").trim();

                                if(gr_type.equals(stageInnerGdTy) || isMix){
                                    getOnlyProdData(0, innerGdOb);
                                }
                            }
                        }
                    }
                }
            }
        }

        if(prodGdList != null){
            addMissedDataWhichPresentOnProd(prodGdList, prod_gd_validated);
        }
        
        if(stageGdList != null){
            addExtraDataFoundOnStageInList(stageGdList, stage_gd_validated);
        } 

        if(artworks.size() > 0){
            boolean isArtworkValid = helper.validateActiveLinks(artworks);
            artworks.clear();
            if(!isArtworkValid){
                log.error("Keyword => "+"\""+KEYWORD+"\""+ " artworks not active please check manually.");
            }
        }
            // System.out.println("Stage : "+stage_res_data.toString());
            // System.out.println("Prod : "+prod_res_data.toString());
            // System.out.println("Diff : "+diff_data.toString());
            // System.out.println("Only stage : "+stage_ext_data.toString());

        ArrayList<String> result_set = new ArrayList<>();
        result_set.add(prod_res_data.toString());
        prod_res_data.clear();
        result_set.add(stage_res_data.toString());
        stage_res_data.clear();
        result_set.add(diff_data.toString());
        diff_data.clear();
        result_set.add(stage_ext_data.toString());
        stage_ext_data.clear();

        prod_gd_validated.clear();
        stage_gd_validated.clear();
        return result_set;
    }

    /**
     * Validate InnerGd
     * @param gr_type
     * @param isMix
     * @param prodGdObject
     * @param stageGdObject
     */
    private void validateInnerGdValues(String prodGrTy, boolean isMix, JSONObject prodGdObject, JSONObject stageGdObject) {
        boolean isInnerGd = true;
        int prodCounter = 0;
        int stageCounter = 0;
        AutoSuggestLite lite = new AutoSuggestLite();

        JSONArray prodGdList =  prodGdObject.getJSONArray("innerGdList");
        JSONArray stageGdList =  stageGdObject.getJSONArray("innerGdList");

        lite.validateArrayLength("innerGdList", prodGdList, stageGdList);


        Iterator<Object> prodGdItr = prodGdList.iterator();
        while(prodGdItr.hasNext()){
            JSONObject prodGd = (JSONObject) prodGdItr.next();

            Iterator<Object> stageGdItr = stageGdList.iterator();
            while(stageGdItr.hasNext()){
                JSONObject stageGd = (JSONObject) stageGdItr.next();
                String stageGrTy = getOptionalJSONObject(stageGd, "ty").trim();

                if(stageGrTy.equals(prodGrTy) || isMix){
                    String isDataValid = validateGdDetails(isInnerGd, prodCounter, stageCounter, prodGd, stageGd);
                    if(!isDataValid.equals(STAGE_NOT_VALIDATED) && isDataValid.contains(STAGE_IDENTIFIER)){
                        // System.out.println("Stage : "+stage_res_data.toString());
                        // System.out.println("Prod : "+prod_res_data.toString());
                        // System.out.println("Diff : "+diff_data.toString());
                        // System.out.println("Only stage : "+diff_data.toString());
                        stageCounter = 0;
                        break;
                    }
                }
                stageCounter++;
            }
            prodCounter++;
        }

        addMissedDataWhichPresentOnProd(prodGdList, prod_InnerGd_validated);
        addExtraDataFoundOnStageInList(stageGdList, stage_InnerGd_validated);

        // System.out.println("Stage : "+stage_res_data.toString());
        // System.out.println("Prod : "+prod_res_data.toString());
        // System.out.println("Diff : "+diff_data.toString());
        // System.out.println("Only stage : "+stage_ext_data.toString());
    }

    /**
     * Prod data missed in stage
     * @param prodGdList
     */
    private void addMissedDataWhichPresentOnProd(JSONArray prodGdList, ArrayList<Integer> visited_list) {
        if(prodGdList.length() != visited_list.size()){
            String prodUniqueId = "";
            for(int i = 0; i<prodGdList.length(); i++){
                if(!visited_list.contains(i)){
                    JSONObject prodGdObject = prodGdList.getJSONObject(i);
                    String prod_gdObject_type = getOptionalJSONObject(prodGdObject, "ty");
                    if(prod_gdObject_type.length() > 0){
                        String prod_iid = prodGdObject.getString("iid").toString().trim();
                        prodUniqueId = prod_iid+"_"+prod_gdObject_type;
                        String prod_gd_title = getOptionalJSONObject(prodGdObject, "ti").trim();
                        diff_data.add(prodUniqueId+"__"+prod_gd_title);
                        prod_res_data.add(prod_iid+"__"+prod_gd_title);
                        String prod_gd_aw = getOptionalJSONObject(prodGdObject, "aw").trim();
                        artworks.add(prod_gd_aw);
                    }
                }

                if(i == (prodGdList.length()-1)){
                    visited_list.clear();
                }
            }
        }
    }

    /**
     * Extra Data in stage we got
     * @param stageGdList
     */
    private void addExtraDataFoundOnStageInList(JSONArray stageGdList, ArrayList<Integer> visited_list) {
        if(stageGdList.length() != visited_list.size()){
            for(int k = 0; k<stageGdList.length(); k++){
                if(!visited_list.contains(k)){
                    JSONObject stageGdObject = stageGdList.getJSONObject(k);
                    String stage_gdObject_type = getOptionalJSONObject(stageGdObject, "ty");
                    if(stage_gdObject_type.length() > 0){
                        String stage_iid = stageGdObject.getString("iid").toString().trim();
                        String stageUniqueId = stage_iid+"_"+stage_gdObject_type;
                        String stage_gd_title = getOptionalJSONObject(stageGdObject, "ti").trim();
                        stage_ext_data.add(stageUniqueId+"__"+stage_gd_title);
                        String stage_gd_aw = getOptionalJSONObject(stageGdObject, "aw").trim();
                        artworks.add(stage_gd_aw);
                    }
                }

                if(k == (stageGdList.length()-1)){
                    visited_list.clear();
                }
            }
        }
    }

    /**
     * Pass Single GD Objcets
     * @param pGdObject
     * @param sGdObject
     */
    private String validateGdDetails(boolean isInnerGd, int prodGdCount, int stageGdCount, JSONObject pGdObject, JSONObject sGdObject){
        String prodUniqueId = "";
        String stageUniqueId = "";
        String prod_iid = pGdObject.getString("iid").toString().trim();
        String stage_iid = sGdObject.getString("iid").toString().trim();
        String pGdObType = getOptionalJSONObject(pGdObject, "ty");
        String sGdObType = getOptionalJSONObject(sGdObject, "ty");

        boolean isIidMatched = prod_iid.equals(stage_iid);
        boolean isTypeValidated = pGdObType.equals(sGdObType);
        if(isIidMatched && isTypeValidated){
            if(isInnerGd){
                prod_InnerGd_validated.add(prodGdCount);
                stage_InnerGd_validated.add(stageGdCount);
            }else{
                prod_gd_validated.add(prodGdCount);
                stage_gd_validated.add(stageGdCount);
            }
            
            prodUniqueId = prod_iid+"_"+pGdObType;
            stageUniqueId = stage_iid+"_"+sGdObType;

            String prod_gd_title = getOptionalJSONObject(pGdObject, "ti").trim();
            String stage_gd_title = getOptionalJSONObject(sGdObject, "ti").trim();

            if(!stageUniqueId.equals(prodUniqueId)){
                diff_data.add(prodUniqueId+"__"+prod_gd_title);
            }

            prod_res_data.add(prod_iid+"__"+prod_gd_title);
            stage_res_data.add(stage_iid+"__"+stage_gd_title);

            // if stage matched
            String stageMatched = STAGE_IDENTIFIER+"_"+stage_iid+"_"+stage_gd_title;

            String prod_gd_aw = getOptionalJSONObject(pGdObject, "aw").trim();
            String stage_gd_aw = getOptionalJSONObject(sGdObject, "aw").trim();

            if(prod_gd_aw.equals(stage_gd_aw)){
                artworks.add(prod_gd_aw);
            }else{
                artworks.add(prod_gd_aw);
                log.error("For keyword : "+"\""+KEYWORD+"\""+" & uniqueId "+prodUniqueId+" gd type "+pGdObType+ " => stage artwork not matching with production artwork!");
            }

            return stageMatched;
        }
        return STAGE_NOT_VALIDATED;
    }

    @Step("Perform csv write on basis of got results.")
    public void processCsvWrite(Map<Integer, String[]> result) {
        String file_name = "AutoSuggestLite.csv";
        String head[] = { "Keyword", "ErSolr", "Staging Extra Response", "Staging Response", "Live Response", "Difference(Prod vs Stage)", "Gr-Title", "Algo (Stage | Production)" };
        WriteCsv.writeCsvWithHeader(file_name, head, result);
    }
}