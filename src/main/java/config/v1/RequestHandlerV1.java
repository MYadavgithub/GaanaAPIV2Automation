package config.v1;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.*;
import config.Constants;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * @author umesh-shukla
 * @version v1
 */
public class RequestHandlerV1 {

    GetProp prop = new GetProp();
    private static Logger log = LoggerFactory.getLogger(RequestHandlerV1.class);

    private Response exeRequestAndGetResponse(String url, ApiRequestTypes requestType, RequestSpecification reqspec){
        Response response = null;
        System.setProperty("https.protocols", prop.getHttpProtocol());
        try {
			switch (requestType)
			{
			case GET:
                response = reqspec.when().get(url).then().extract().response();
				break;
			case POST:
				response = reqspec.when().post(url).then().extract().response();
				break;
			case DELETE:
				response = reqspec.when().delete(url).then().extract().response();
				break;
			case PUT:
				response = reqspec.when().put(url).then().extract().response();
				break;
			case PATCH:
				response = reqspec.when().patch(url).then().extract().response();
				break;
			}
		}
		catch (Exception e){
            log.error("Api Execution failed, manual check required...", e);
        }

        if(validateStatusCodeAndResponseTime(response, url)){
            return response;
        }

        return response;
    }

    private RequestSpecification requestSpecBuilder(String url, ContentTypes contentType, Map<String, String> headers, Map<String, String> formData, String body){
        RequestSpecification reqspec = RestAssured.given();
        switch (contentType) {
            case ANY:
                reqspec.accept(ContentType.ANY);
            break;

            case BINARY:
                reqspec.accept(ContentType.BINARY);
            break;

            case HTML:
                reqspec.accept(ContentType.HTML);
            break;

            case JSON:
                reqspec.accept(ContentType.JSON);
            break;

            case TEXT:
                reqspec.accept(ContentType.TEXT);
            break;

            case URLENC:
                reqspec.accept(ContentType.URLENC);
            break;

            case XML:
                reqspec.accept(ContentType.XML);
            break;
        }

        reqspec.baseUri(url);
        reqspec.headers(headers);
        reqspec.urlEncodingEnabled(true);
        // reqspec.log().all();
        EncoderConfig encoderconfig = new EncoderConfig();
        reqspec.config(RestAssured.config().encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));

        if(body != null){
            reqspec.body(body);
        }else if(formData != null){
			reqspec.formParams(formData);
		}
        return reqspec;
    }

    public Response executeRequestAndGetResponse(String url, ApiRequestTypes requestType, ContentTypes contentType, Map<String, 
        String> custom_headers, Map<String, String> formData, String body){
    
        Response response = null;
        Map<String, String> headers;
        if(custom_headers != null){
            headers = custom_headers;
        }else{
            headers = RequestHelper.getHeader(0);
        }

        if(body != null && body.length() > 0){
            headers.put("Accept", "*/*");
            headers.put("Connection", "keep-alive");
            headers.put("Content-Type", "application/json");
        }
        RequestSpecification reqspec = requestSpecBuilder(url, contentType, headers, formData, body);
        response = exeRequestAndGetResponse(url, requestType, reqspec);
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
                log.info("Url : "+url);
                return validateBasics;
            }else{
                validateBasics = false;
                log.info(url);
                log.info(response.asString());
                log.error("Api taking much time from expected time , total time taken : "+response.getTimeIn(TimeUnit.SECONDS));
            }
        }
        return validateBasics;
    }
}
