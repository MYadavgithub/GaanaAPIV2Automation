package common;
import config.Constants;
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

    public Response createGetRequestWithoutHeader(String url){
        Response response = RestAssured.given()
            .urlEncodingEnabled(false)
            // .log().all()
            .when().get(url);

        // response.prettyPrint();
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
}