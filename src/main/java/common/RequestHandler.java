package common;
import java.util.Map;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import config.Constants;
import org.slf4j.LoggerFactory;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import java.util.concurrent.TimeUnit;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * @author umesh-shukla
 * @version v1
 */

public class RequestHandler {

    private static Logger log = LoggerFactory.getLogger(RequestHandler.class);

    /**
     * Create Get Request with RequestSpecification Or Http Method
     * @param url
     * @return
     */
    public Response createGetRequestHttp(String url) {
        RestAssured.baseURI = url;
        Map<String, String> headers = Headers.getHeaders(0, null);
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
        Map<String, String> headers = Headers.getHeaders(0, null);
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
     * Post Request with Json Object
     * @param url
     * @param post_data
     * @return
     */
    public Response postDataInRequest(String url, JSONObject post_data) {
        Map<String, String> headers = Headers.getHeaders(0, null);
        headers.put("Accept", "*/*");
        headers.put("Connection", "keep-alive");
        headers.put("Content-Type", "application/json");
        EncoderConfig encoderconfig = new EncoderConfig();
        Response response = RestAssured.given()
            .config(RestAssured.config().encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)))
            .accept(ContentType.JSON)
            .headers(headers)
            .body(post_data.toString())//.log().all()
            .post(url).then()
            .extract().response();

        if(validateStatusCodeAndResponseTime(response, url)){
            return response;
        }
        return null;
    }
}