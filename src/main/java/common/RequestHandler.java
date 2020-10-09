package common;
import config.Constants;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class RequestHandler {
   
    private static Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    public Response createGetRequestCall(Properties prop, String url) {
        Response response = RestAssured.given()
            //.log().all()
            .header("gaanaAppVersion", prop.getProperty("gaanaAppVersionAndroid").toString().trim())
            .header("deviceType", prop.getProperty("deviceTypeAndroid").toString().trim())
            .header("deviceId", prop.getProperty("deviceId").toString().trim())
            .header("COUNTRY", prop.getProperty("COUNTRY").toString().trim())
            .header("appVersion", prop.getProperty("appVersion").toString().trim())
            .when().get(url);
        
        if(response != null){
            if(validateStatusCodeAndResponseTime(response)){
                log.info(url);
                log.info(response.asString());
                return response;
            }else{
                log.error("The get api call was taking more time than expected api was \n"+url);
            }
        }
        return null;
    }
    
    /**
     * validate status code and response time
     * @param response
     * @return
     */
    private boolean validateStatusCodeAndResponseTime(Response response){
        if(response.getStatusCode() == 200 && response.getTimeIn(TimeUnit.SECONDS) <= Constants.RESPONSE_TIME){
            return true;
        }
        return false;
    }
}