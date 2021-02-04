package recomendation.reco_tracks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import config.Constants;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.Mailer;
import utils.WriteCsv;

public class RevmaPt {

    int API_CALL = 1;
    int EXEC_CONTEXT = 0;
    final int INVOCATION = 20;
    String URL = "https://www.revma.com/api/stations/mk3t27qygeruv/private_stream_token?minutes=600";
    String header_auth_key = "1600091866bU5eLR9PP4NX7dahKtM5";
    ArrayList<String> streamUrls = new ArrayList<>();

    @BeforeClass
    public void beforeClass(ITestContext context){
        EXEC_CONTEXT = Integer.parseInt(context.getCurrentXmlTest().getParameter("id"));
    }

    private Response getResponse(boolean isAuth, String url){
        Response response = null;
        if(isAuth){
            response = RestAssured.given().when()
            .urlEncodingEnabled(true)
            .header("x-auth-token", header_auth_key)
            // .log().all()
            .get(url);
        }else{
            response = RestAssured.given().when()
            .urlEncodingEnabled(true)
            // .log().all()
            .get(url);
        }
        return response;
    }

    @Test(priority = 1, enabled = false, invocationCount = INVOCATION)
    public void validateResponseTimeGetStreamUrlForRevMa(){
        String file_name = "RevMaStreamUrl.csv";
        Map<Integer, String[]> result = new HashMap<>();
        Response response = getResponse(true, URL);
        String call_count = String.valueOf(API_CALL);
        String status_code = String.valueOf(response.getStatusCode());
        String response_time = String.valueOf(response.getTimeIn(TimeUnit.MILLISECONDS));
        String stream_url = response.asString().trim().replaceAll("\"", "");
        streamUrls.add(stream_url);
        String head[] = { "Sr. No", "Url", "Status Code", "Response Time (Ms)", "Stream Url" };
        String result_set[] = {call_count, URL,  status_code, response_time, stream_url };
        result.put(API_CALL, result_set);
        processCsvWrite(file_name, head, result);
        API_CALL++;
        if((API_CALL-1) == INVOCATION){
            sendEmail(file_name);
            API_CALL = 1;
        }
    }

    @Test(priority = 2, enabled = false, dependsOnMethods = {"validateResponseTimeGetStreamUrlForRevMa"}, dataProvider = "url", invocationCount = INVOCATION)
    public void validateRevMa(String url){
        String file_name = "RevMa.csv";
        // String url = "http://stream-as-dev.rcs.revma.com/test";
        Map<Integer, String[]> result = new HashMap<>();
        Response response = getResponse(false, URL);
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

    @Test(priority = 3, enabled = true, dataProvider = "devurl", invocationCount = INVOCATION)
    public void validateRevMaDevUrls(String url){
        String file_name = "RevMa.csv";
        Map<Integer, String[]> result = new HashMap<>();
        Response response = null;
        if(EXEC_CONTEXT == 0){
            response = getResponse(true, url);
        }else{
            response = getResponse(false, url);
        }
        String call_count = String.valueOf(API_CALL);
        String status_code = String.valueOf(response.getStatusCode());
        String response_time = String.valueOf(response.getTimeIn(TimeUnit.MILLISECONDS));
        String isAuthenticated = "false";
        if(EXEC_CONTEXT == 0){
            isAuthenticated = "true";
        }
        String result_set[] = {call_count, url,  status_code, response_time, isAuthenticated };
        String head[] = { "Sr. No", "Url", "Status Code", "Response Time (Ms)", "IsAuthUrl" };
        result.put(API_CALL, result_set);
        processCsvWrite(file_name, head, result);

        API_CALL++;
        if((API_CALL-1) == INVOCATION){
            sendEmail(file_name);
            API_CALL = 1;
        }
    }

    @DataProvider(name = "devurl")
    public Object[][] devDataProvider() {
        preUrls();
        return new Object[][] {
            {
                streamUrls.get(EXEC_CONTEXT)
            }
        };
    }

    @DataProvider(name = "url")
    public Object[][] DataProvider() {
        return new Object[][] {
            {
                streamUrls.get(0)
            }
        };
    }

    private void preUrls(){
        String withTokenUrl = "https://stream-as-dev.rcs.revma.com/mk3t27qygeruv?rj-auth=AAABdJnI2Vh1c2VyAG1rM3QyN3F5Z2VydXYAooZTLXHB-2gyMU0CUIfhy2xwMgnkIYgK8xK5UUBDtJg%3D";
        String withoutTokenUrl = "https://stream-as-dev.rcs.revma.com/test";
        streamUrls.add(withTokenUrl);
        streamUrls.add(withoutTokenUrl);
    }

    private void processCsvWrite(String file_name, String[] head, Map<Integer, String[]> result) {
        if((API_CALL == 1 && EXEC_CONTEXT == 2) || (API_CALL == 1 && EXEC_CONTEXT == 0)){
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