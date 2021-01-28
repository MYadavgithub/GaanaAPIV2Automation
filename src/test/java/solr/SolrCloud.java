package solr;
import java.util.List;
import java.util.Map;
import utils.CommonUtils;
import utils.CsvReader;
import utils.WriteCsv;
import org.slf4j.Logger;
import config.Constants;
import org.testng.Assert;
import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;

public class SolrCloud {

    int counter = 0;
    CloudSolrClient solrClient;
    String OUT_PUT_FILE = "SolrCloud.csv";
    ArrayList<String> testdatainputs = null;
    private static Logger log = LoggerFactory.getLogger(SolrCloud.class);

    public CloudSolrClient createConnection() {
        List<String> zkServers = Constants.zookeeperServers();
        solrClient = new CloudSolrClient.Builder(zkServers, Optional.of("/gaana-solr")).build();
        solrClient.setDefaultCollection(Constants.COLLECTION);
        return solrClient;
    }

    public SolrDocumentList getSolrResponse(SolrQuery solrQuery) {
        SolrDocumentList list = null;
        try {
            QueryRequest req = new QueryRequest(solrQuery);
            // solrClient.setDefaultCollection(COLLECTION);
            QueryResponse response = req.process(solrClient);
            list = response.getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @BeforeClass
    public void prepareEnv(){
        getTestData();
    }

    @DataProvider(name = "key_provider")
    public Object [][] url(){
        if(counter == 0)
            counter++;
        return new Object[][]
        {
            {
                testdatainputs.get(counter).toString().trim()
            }
        };
    }

    @Test(priority = 1, dataProvider = "key_provider", invocationCount = Constants.ZOOKEEPER_INVOCATION_COUNT)
    public void solrCloudTest(String keyword) {
        Map<Integer, String[]> result = new HashMap<>();
        ArrayList<String> response_ids = new ArrayList<>();

        if (solrClient == null) {
            createConnection();
        }

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("title:" + keyword);
        solrQuery.setRows(50);
        solrQuery.set("collection", Constants.COLLECTION);
        solrQuery.set("wt", "json");
        // solrQuery.setFilterQueries(keyword);
        long start = System.currentTimeMillis();
        SolrDocumentList documentList = getSolrResponse(solrQuery);
        long finish = System.currentTimeMillis();
        long time_taken = (finish - start);
        // long time_took = TimeUnit.MILLISECONDS.toSeconds(finish - start);

        if((documentList != null && documentList.size() > 0)) {
            ArrayList<Integer> idList = new ArrayList<>();
            for (SolrDocument document : documentList) {
                int id = Integer.parseInt(document.get("id").toString().trim());
                idList.add(id);
                Assert.assertEquals(id > 0, true, "Document not holding any unique id!");
            }

            if(idList.size() == documentList.size()){
                response_ids.add(idList.toString().trim());
            }else{
                response_ids.add("N/A");
                log.error("Error! Response list size and ids list size misatched.");
            }
        }else{
            response_ids.add("N/A");
            log.info("Error! for keyword : "+"\""+keyword+"\"" +" no response reverted from solr!");
            //Assert.assertEquals(documentList.size() > 0, true, "Document list is empty!");
        }

        String result_val [] = {String.valueOf(counter), keyword, String.valueOf(time_taken), response_ids.toString().trim().replaceAll("[\\[\\]\\(\\)]", "")};
        result.put(counter, result_val);
        processCsvWrite(result);
        response_ids.clear();
        if(counter == Constants.ZOOKEEPER_INVOCATION_COUNT){
            sendEmail();
        }
        counter++;
    }

    private void sendEmail() {
        if(counter == Constants.ZOOKEEPER_INVOCATION_COUNT){
            Map<String, String> mail_data = new HashMap<>();
            mail_data.put("task_name", "Solr Cloud Testing");
            mail_data.put("file_name", OUT_PUT_FILE);
            mail_data.put("scope", "Direct queried keyword from solr using zookeeper.");
            CommonUtils.processMailer(mail_data);
        }
    }

    private void getTestData() {
        String file_path = "./src/test/resources/data/Search_Data/";
        String file_name = "Auto_Suggest_Lite.csv";
        testdatainputs = CsvReader.readCsv(file_path + file_name);
    }

    private void processCsvWrite(Map<Integer, String[]> result) {

        if(counter == 1){
            String head[] = { "Sr No", "Keyword", "Time (ms)", "Response Ids"};
            WriteCsv.writeCsvWithHeader(OUT_PUT_FILE, head, result, true);
        }else{
            WriteCsv.writeCsvWithHeader(OUT_PUT_FILE, null, result, true);
        }
    }
}