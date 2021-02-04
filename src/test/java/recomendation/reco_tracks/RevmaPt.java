package recomendation.reco_tracks;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.Test;
import config.Constants;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.Mailer;
import utils.WriteCsv;

public class RevmaPt {

    int API_CALL = 1;
    final int INVOCATION = 200;
    String file_name = "RevMa.csv";
    String url = "http://stream-as-dev.rcs.revma.com/test";

    @Test(priority = 1, enabled = true, invocationCount = INVOCATION)
    public void validateRevMa(){
        Map<Integer, String[]> result = new HashMap<>();
        Response response = RestAssured.given().when()
            .urlEncodingEnabled(true)
            // .log().all()
            .get(url);

        String call_count = String.valueOf(API_CALL);
        String status_code = String.valueOf(response.getStatusCode());
        String response_time = String.valueOf(response.getTimeIn(TimeUnit.MILLISECONDS));
        String result_set[] = {call_count, url,  status_code, response_time };
        result.put(API_CALL, result_set);
        processCsvWrite(result);
        if(API_CALL == INVOCATION){
            sendEmail();
        }
        API_CALL++;
    }

    public void processCsvWrite(Map<Integer, String[]> result) {
        String head[] = { "Sr. No", "url", "status code", "Response Time (Ms)" };
        if(API_CALL == 1){
            WriteCsv.writeCsvWithHeader(file_name, head, result, true);
        }else{
            WriteCsv.writeCsvWithHeader(file_name, null, result, true);
        }
    }

    public void sendEmail(){
        if(Constants.EMAILER_ENABLED == 1){
            String scope = "Scope : This is REVMA Multiple API Hit Reponse time result, server to server call.";
            Mailer mail = new Mailer();
            mail.sendEmail("RevMa", file_name, scope);
        }
    }
}