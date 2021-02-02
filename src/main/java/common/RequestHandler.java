package common;
import java.util.Map;
import org.slf4j.Logger;
import config.Constants;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import java.util.concurrent.TimeUnit;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RequestHandler {

    private static Logger log = LoggerFactory.getLogger(RequestHandler.class);

    /**
     * Create Get Request with RequestSpecification Or Http Method
     * @param url
     * @return
     */
    public Response createGetRequestHttp(String url) {
        RestAssured.baseURI = url;
        Map<String, String> headers = Headers.getHeaders(0);
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest
            .urlEncodingEnabled(false)
            .headers(headers)
            // .log().all()
            .when().get(url);

        response.prettyPrint();
        if(validateStatusCodeAndResponseTime(response, url)){
            return response;
        }

        return response;
    }

    /**
     * Rest-Assured Get Call Without Specification
     * @param url
     * @return
     */
    @Step("Creating get request to api : {0} ")
    public Response createGetRequest(String url){
        Map<String, String> headers = Headers.getHeaders(0);
        Response response = RestAssured.given()
            .urlEncodingEnabled(true)
            .headers(headers)
            // .log().all()
            .when().get(url);

        // response.prettyPrint();
        if(validateStatusCodeAndResponseTime(response, url)){
            return response;
        }

        return response;
    }

    @Step("Creating get request with url : {0} \n Custom header values are : {1}")
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