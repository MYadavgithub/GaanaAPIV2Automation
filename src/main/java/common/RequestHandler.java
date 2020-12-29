package common;
import config.Constants;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RequestHandler {

    private static Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public Response createGetRequestCall(Properties prop, String url) {
        Map<String, String> headers = GlobalConfigHandler.headers(prop);
        Response response = RestAssured.given()
            // .log().all()
            .headers(headers)
            .when().get(url);

        if (validateStatusCodeAndResponseTime(response, url)) {
            return response;
        }

        return response;
    }

    public Response createGetRequest(Properties prop, String url) {
        Map<String, String> headers = GlobalConfigHandler.headers(prop);
        RestAssured.baseURI = url;
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest
            .headers(headers)
            // .log().all()
            .when().get(url);

        // response.prettyPrint();
        if(validateStatusCodeAndResponseTime(response, url)){
            return response;
        }

        return response;
    }

    public Response createGetRequest(String url){
        Map<String, String> headers = Headers.getHeaders(0);
        Response response = RestAssured.given()
            .urlEncodingEnabled(false)
            // .log().all()
            .headers(headers)
            .when().get(url);

        // response.prettyPrint();
        if(validateStatusCodeAndResponseTime(response, url)){
            return response;
        }

        return response;
    }

    public Response createGetRequestWithCustomHeaders(String url, Map<String, String> headers){
        Response response = RestAssured.given()
            .urlEncodingEnabled(false)
            .headers(headers)
            // .log().all()
            .when().get(url);

        if(validateStatusCodeAndResponseTime(response, url)){
            return response;
        }

        return response;
    }
    
    /**
     * validate status code and response time
     * @param response
     * @return
     */
    private boolean validateStatusCodeAndResponseTime(Response response, String url){
        boolean validateBasics = false;
        if((response.getStatusCode() == 200 || response.getStatusCode() == 201) && response.getTimeIn(TimeUnit.SECONDS) <= Constants.RESPONSE_TIME){
            validateBasics = true;
        }

        if(response != null){
            if(validateBasics){
                log.info(url);
                return validateBasics;
            }else{
                validateBasics = false;
                log.info(url);
                log.info(response.asString());
                log.error("The get api call was taking more time than expected, total time taken : "+response.getTimeIn(TimeUnit.SECONDS));
            }
        }
        return validateBasics;
    }


    /**
     * Validate Link is active or not
     * @param urls
     * @return
     */
    public static boolean validateGetUrlStatusCode(ArrayList<String> urls){
        boolean linkActive = false;
        ArrayList<String> inactiveUrls = new ArrayList<>();

        if(urls.size() <= 0){
            return linkActive;
        }

        int count = 1;
		for(String url : urls) {
			if(url.contains("http")) {
               Response response = RestAssured.given()
                    .urlEncodingEnabled(false)
                    .when().get(url);
                if(response.getStatusCode() == 200){
                    linkActive = true;
                }else{
                    linkActive = false;
                    inactiveUrls.add("Count : "+count+", URL :"+url);
                }
            }
            count++;
        }

        if(inactiveUrls.size() > 0){
            linkActive = false;
            log.error("Below given urls are inactive state, please manually validate the same : \n"+inactiveUrls);
        }
		return linkActive;
    }
}