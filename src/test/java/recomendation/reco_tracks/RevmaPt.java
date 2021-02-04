package recomendation.reco_tracks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import config.Constants;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.Mailer;
import utils.WriteCsv;

public class RevmaPt {

    int API_CALL = 1;
    final int INVOCATION = 20;
    ArrayList<String> streamUrls = new ArrayList<>();

    @Test(priority = 1, enabled = true, invocationCount = INVOCATION)
    public void validateResponseTimeGetStreamUrlForRevMa(){
        String file_name = "RevMaStreamUrl.csv";
        Map<Integer, String[]> result = new HashMap<>();
        String url = "https://www.revma.com/api/stations/mk3t27qygeruv/private_stream_token?minutes=600";
        String header_auth_key = "1600091866bU5eLR9PP4NX7dahKtM5";
        Response response = RestAssured.given().when()
            .urlEncodingEnabled(true)
            .header("x-auth-token", header_auth_key)
            // .log().all()
            .get(url);

        String call_count = String.valueOf(API_CALL);
        String status_code = String.valueOf(response.getStatusCode());
        String response_time = String.valueOf(response.getTimeIn(TimeUnit.MILLISECONDS));
        String stream_url = response.asString().trim().replaceAll("\"", "");
        streamUrls.add(stream_url);
        String head[] = { "Sr. No", "Url", "Status Code", "Response Time (Ms)", "Stream Url" };
        String result_set[] = {call_count, url,  status_code, response_time, stream_url };
        result.put(API_CALL, result_set);
        processCsvWrite(file_name, head, result);
        API_CALL++;
        if((API_CALL-1) == INVOCATION){
            sendEmail(file_name);
            API_CALL = 1;
        }
    }

    @Test(priority = 2, dependsOnMethods = {"validateResponseTimeGetStreamUrlForRevMa"}, enabled = true, dataProvider = "url", invocationCount = INVOCATION)
    public void validateRevMa(String url){
        String file_name = "RevMa.csv";
        // String url = "http://stream-as-dev.rcs.revma.com/test";
        Map<Integer, String[]> result = new HashMap<>();
        Response response = RestAssured.given().when()
            .urlEncodingEnabled(true)
            // .log().all()
            .get(url);

        String call_count = String.valueOf(API_CALL);
        String status_code = String.valueOf(response.getStatusCode());
        String response_time = String.valueOf(response.getTimeIn(TimeUnit.MILLISECONDS));
        String result_set[] = {call_count, url,  status_code, response_time };
        String head[] = { "Sr. No", "Url", "Status Code", "Response Time (Ms)" };
        result.put(API_CALL, result_set);
        processCsvWrite(file_name, head, result);

        API_CALL++;
        if((API_CALL-1) == INVOCATION){
            sendEmail(file_name);
            API_CALL = 1;
        }
    }

    @DataProvider(name = "url")
    public Object[][] DataProvider() {
        return new Object[][] {
            {
                streamUrls.get(1)
            }
        };
    }

    private void processCsvWrite(String file_name, String[] head, Map<Integer, String[]> result) {
        if(API_CALL == 1){
            WriteCsv.writeCsvWithHeader(file_name, head, result, true);
        }else{
            WriteCsv.writeCsvWithHeader(file_name, null, result, true);
        }
    }

    private void sendEmail(String file_name){
        if(Constants.EMAILER_ENABLED == 1){
            String scope = "Scope : This is REVMA Multiple API Hit Reponse time result, server to server call.";
            Mailer mail = new Mailer();
            mail.sendEmail("RevMa", file_name, scope);
        }
    }
}