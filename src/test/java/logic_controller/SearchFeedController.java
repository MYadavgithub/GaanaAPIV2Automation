package logic_controller;
import java.util.*;
import org.json.*;
import org.slf4j.*;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import common.*;
import config.Endpoints;
import io.qameta.allure.Step;
import test_data.SearchFeedTd;

/**
 * @author Umesh Shukla
 */

public class SearchFeedController {

    Helper helper = new Helper();
    private static Logger log = LoggerFactory.getLogger(SearchFeedController.class);

    @Step("Creating Endpoints for api, api call : {0} and tab_id : {1}")
    public static String generateSearchFeedUrl(int api_call, String tab_id) {
        String endpoint = "";
        if (api_call == 0) {
            endpoint = Endpoints.searchFeed + SearchFeedTd.prepEndpointParams();
        } else {
            endpoint = Endpoints.searchFeed + SearchFeedTd.prepEndpointParams() + "&tabSelected=" + tab_id;
        }
        return endpoint;
    }

    @Step("Validating Expected tabs:  ids : {0}")
    public boolean validateExTabs(JSONArray tabs) {
        boolean isExTabValidated = false;
        for (int i = 0; i < tabs.length(); i++) {
            JSONObject tab = tabs.getJSONObject(i);
            String tab_id = tab.optString("tabId").toString().trim();
            String tab_name = tab.optString("tabName").toString().trim();
            String ex_tab_name = SearchFeedTd.tabsName(tab_id);
            if (tab_name.equals(ex_tab_name)) {
                for (String tabName : SearchFeedTd.tabs) {
                    if (!tabName.equals(SearchFeedTd.tabs[0])) {
                        if (tabName.equals(tab_id)) {
                            isExTabValidated = true;
                            break;
                        }
                    }
                }
            }
        }
        return isExTabValidated;
    }

    @Step("Validating Selected tab id : {0} and data : {1}")
    public boolean validateTabSelected(String tab_id, JSONObject tabSelected) {
        String tabId = "";
        String tabName = "";
        String ex_tab_name = SearchFeedTd.tabsName(tab_id);

        if (tab_id.equals(SearchFeedTd.tabs[0])) {
            tabId = "default";
        } else {
            tabId = tabSelected.optString("tabId").toString().trim();
        }

        if (tab_id.equals("-4")) {
            tabName = ex_tab_name;
        } else {
            tabName = tabSelected.optString("tabName").toString().trim();
        }

        if(GlobalConfigHandler.getDeviceType() == 0){
            if(tabId.equals("default")){
                ex_tab_name = SearchFeedTd.tabsName(SearchFeedTd.podcast);
            }
        }

        if (tabId.equals(tab_id) && tabName.equals(ex_tab_name)) {
            return true;
        }
        return false;
    }

    @Step("Validate response body for tab_id : {0} and response object : {1}")
    public boolean validateSearchFeedRecommendedResponse(String tab_id, JSONArray responseBody) {
        boolean isResBodyValid = false;
        SoftAssert softAssert = new SoftAssert();
        if (responseBody.length() <= 0) {
            log.error("Response body for validations can't be empty or null please validate for tab id " + tab_id);
            return false;
        }

        Iterator<Object> itr = responseBody.iterator();
        while (itr.hasNext()) {
            JSONObject responseOb = (JSONObject) itr.next();
            
            List<Object> keys = helper.keys(responseOb);
            boolean keyValidated = validateKeys(keys);
            softAssert.assertEquals(keyValidated, true, "Keys not validated for tab id "+tab_id+" response body object : \n"+responseOb);

            isResBodyValid = validateKeyValueRequired(tab_id, keys, responseOb);
            softAssert.assertEquals(isResBodyValid, true, "tab id"+tab_id+"\nResponse object validation failed object value : "+responseOb);
        }
        return isResBodyValid;
	}

    private boolean validateKeys(List<Object> keys) {
        boolean isKeyValidated = false;
        if(keys.size() > 0){
            for(Object key : keys){
                String key_name = key.toString().trim();
                for(String exKeyName : SearchFeedTd.expectedResponseKeys){
                    if(exKeyName.equals(key_name)){
                        isKeyValidated = true;
                        break;
                    }else{
                        isKeyValidated = false;
                        continue;
                    }
                }
            }
        }
        return isKeyValidated;
    }

    private boolean validateKeyValueRequired(String tab_id, List<Object> keys, JSONObject responseOb) {
        boolean isObValidated = false;
        ArrayList<String> artworks = new ArrayList<>();
        ArrayList<String> artistAtw = new ArrayList<>();
        if(keys.size() > 0){
            for(Object key : keys){
                String key_name = key.toString();
                String value = responseOb.optString(key_name).toString().trim();
                
                switch (key_name) {
                    case "iid":
                        if(value.length() > 0){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "ti":
                        if(value.length() > 0){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "ty":
                        if(value.length() > 0){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "aw":
                        if(value.length() > 0){
                            isObValidated = true;
                            artworks.add(value);
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "play_ct":
                        if(value.length() > 0){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "sti":
                        if(!tab_id.equals("-5") && !tab_id.equals("-6")){
                            if(value.length() > 0){
                                isObValidated = true;
                                // log.info(key_name+ " is : "+value);
                            }else{
                                isObValidated = false;
                                log.error(key_name+" can't be null, manual check required!");
                                return isObValidated;
                            }
                        }
                    break;

                    case "fty":
                        if(!tab_id.equals("-5")){
                            if(value.length() > 0){
                                isObValidated = true;
                                // log.info(key_name+ " is : "+value);
                            }else{
                                isObValidated = false;
                                log.error(key_name+" can't be null, manual check required!");
                                return isObValidated;
                            }
                        }
                    break;

                    case "vty":
                        if(value.length() > 0){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "lang":
                        JSONArray lang = null;
                        try{
                            lang = responseOb.getJSONArray("lang");
                        }catch(Exception e){
                            log.info("Language is not as array!");
                        }
                        
                        if(lang.length() > 0){
                            Iterator<Object> lang_itr = lang.iterator();
                            while(lang_itr.hasNext()){
                                String language = lang_itr.next().toString().trim();
                                if(SearchFeedTd.requestedLang().contains(language)){
                                    // log.info("Umesh : "+responseOb.optString("iid").toString().trim()+" : "+SearchFeedTd.requestedLang() + " : "+language);
                                    isObValidated = true;
                                    break;
                                }
                            }
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "language":
                        if(value.length() > 0){
                            if(value.length() > 0 && SearchFeedTd.requestedLang().toString().replaceAll("[\\[\\]\\(\\)]", "").contains(value)){
                                isObValidated = true;
                                // log.info(key_name+ " is : "+value);
                            }
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "tags":
                        JSONArray tags = null;
                        try{
                            tags = responseOb.getJSONArray("tags");
                        }catch(Exception e){
                            log.info("Tags are not as array!");
                        }
                        if(tags.length() > 0 || tags.length() == 0){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "oty":
                        if(value.length() > 0){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "psl":
                        if(value.length() > 0){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "clip_url":
                        if(value.length() > 100){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null or less than 100 character, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "vurl":
                        if(value.length() > 100){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null or less than 100 character, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "scoreF":
                        double scoref = Double.parseDouble(value);
                        if(!tab_id.equals("-6")){
                            if(tab_id.equals("default") || tab_id.equals("-1") || tab_id.equals("-2") || tab_id.equals("100") || tab_id.equals("103") || tab_id.equals("104") || tab_id.equals("1389")){
                                isObValidated = true;
                            }else if(scoref > 0.00){
                                isObValidated = true;
                                // log.info(key_name+ " is : "+value);
                            }else{
                                System.out.println("Tab id : "+tab_id);
                                isObValidated = false;
                                log.error(key_name+" can't be null or less than 0, manual check required!");
                                return isObValidated;
                            }
                        }
                    break;

                    case "seo":
                        if(value.length() > 0){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "artistATW":
                        if(value.length() > 0){
                            isObValidated = true;
                            artistAtw.add(value);
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "artistTitle":
                        if(value.length() > 0){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "subtitle":
                        if(!tab_id.equals("-5") && !tab_id.equals("-6")){
                            if(value.length() > 0){
                                isObValidated = true;
                                // log.info(key_name+ " is : "+value);
                            }else{
                                isObValidated = false;
                                log.error(key_name+" can't be null, manual check required!");
                                return isObValidated;
                            }
                        }
                    break;

                    case "tile_type":
                        if(value.length() > 0 && value.equals("radio_sq")){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    case "sty":
                        if(value.length() > 0 && (value.equals("RM") || value.equals("RL"))){
                            isObValidated = true;
                            // log.info(key_name+ " is : "+value);
                        }else{
                            isObValidated = false;
                            log.error(key_name+" can't be null, manual check required!");
                            return isObValidated;
                        }
                    break;

                    default:
                        log.info("Key : "+key_name+" ==> "+responseOb.optString(key_name) +" not in validation list.");
                    break;
                }
            }
        }

        if(artworks.size() > 0){
            isObValidated = helper.validateActiveLinks(artworks);
        }else if(artistAtw.size() > 0){
            isObValidated = helper.validateActiveLinks(artistAtw);
        }
        return isObValidated;
    }

    /**
     * Validate Subtitles in response objects
     * @param tab_name
     * @param response
     */
    public void validateSubTitle(String tab_name, JSONArray response){
        String specialSubtitleTabRecommended = SearchFeedTd.tabsName("-1");
        String specialSubtitleTabPopular = SearchFeedTd.tabsName("-2");
        String specialSubtitleTabPodcast = SearchFeedTd.tabsName("-4"); //podcast
        String specialSubtitleTabRadio = SearchFeedTd.tabsName("-5");
        String specialSubtitleTabTrending = SearchFeedTd.tabsName("-6");
        Iterator<Object> response_itr = response.iterator();
        while(response_itr.hasNext()){
            JSONObject resObj = (JSONObject) response_itr.next();
            String iid = resObj.getString("iid").toString().trim();
            String ti = resObj.optString("ti").toString().trim();
            String ty = resObj.optString("ty").toString().trim();
            String language = resObj.optString("language").toString().trim();
            String subtitle = resObj.optString("subtitle").toString().trim();
            String exSubTitle = "";
            if(tab_name.equals(specialSubtitleTabPodcast) && (language.length() > 0 && ty.equals("Show"))){
                exSubTitle = language;
            }else if(tab_name.equals(specialSubtitleTabRadio) && (language.length() > 0 && ty.equals("Radio"))){
                exSubTitle = "";
            }else if((tab_name.equals(specialSubtitleTabRecommended) || tab_name.equals(specialSubtitleTabPopular) || tab_name.equals(specialSubtitleTabTrending)) && (language.length() > 0 && ty.equals("Show"))){
                exSubTitle = ty+" | "+language;
            }else{
                exSubTitle = ty;
                if(subtitle.length() <= 0){
                    subtitle = ty;
                }
            }

            if(!subtitle.equals(exSubTitle)){
                log.error("\""+tab_name+"\""+ " and Id : "+iid+ " and title : " +ti+ " subTitle validation not working!");
            }
            Assert.assertEquals(subtitle, exSubTitle);
        }
    }
}