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
    ArrayList<Long> TIME_TAKEN = new ArrayList<>();
    ArrayList<String> RESPONSE_IDS = new ArrayList<>();
    Map<Integer, String[]> result = new HashMap<>();
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

    @Test(priority = 1, invocationCount = Constants.ZOOKEEPER_INVOCATION_COUNT)
    public void solrCloudTest() {
        if (solrClient == null) {
            createConnection();
        }

        getTestData();
        String keyword = testdatainputs.get(counter);

        if (!keyword.equals("Keyword")) {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery("title:" + keyword);
            solrQuery.setRows(50);
            solrQuery.set("collection", Constants.COLLECTION);
            solrQuery.set("wt", "json");
            // solrQuery.setFilterQueries(keyword);
            long start = System.currentTimeMillis();
            SolrDocumentList documentList = getSolrResponse(solrQuery);
            long finish = System.currentTimeMillis();
            TIME_TAKEN.add(finish - start);
            // long time_took = TimeUnit.MILLISECONDS.toSeconds(finish - start);

            if((documentList != null && documentList.size() > 0)) {
                ArrayList<Integer> idList = new ArrayList<>();
                for (SolrDocument document : documentList) {
                    int id = Integer.parseInt(document.get("id").toString().trim());
                    idList.add(id);
                    Assert.assertEquals(id > 0, true, "Document not holding any unique id!");
                }

                if(idList.size() == documentList.size()){
                    RESPONSE_IDS.add(idList.toString().trim());
                }else{
                    RESPONSE_IDS.add("N/A");
                    log.error("Error! Response list size and ids list size misatched.");
                }
            }else{
                log.error("Error!");
                RESPONSE_IDS.add("N/A");
                //Assert.assertEquals(documentList.size() > 0, true, "Document list is empty!");
            }
        }

        counter++;
        if(counter == Constants.ZOOKEEPER_INVOCATION_COUNT){
            int count = 1;
            for(String ids : RESPONSE_IDS){
                if((Constants.ZOOKEEPER_INVOCATION_COUNT-1) == TIME_TAKEN.size() && TIME_TAKEN.size() == RESPONSE_IDS.size()){
                    String result_val [] = {String.valueOf(count), testdatainputs.get(count), String.valueOf(TIME_TAKEN.get(count-1)), ids.replaceAll("[\\[\\]\\(\\)]", " ")};
                    result.put(count-1, result_val);
                    count++;
                }
            }

            processCsvWrite(result);
            // sendEmail();
            /*ArrayList<String> values = new ArrayList<>();
            for(Long t_ms : time_taken){
                if(t_ms > 200){
                    values.add(testdatainputs.get(count));
                }
                count++;
            }
            log.info("Times in ms for each request : "+time_taken+"\n\n");
            log.info("These keywords took more than 200ms to fetch data from solr : "+values);
            log.info("\n\nThese keywords having no value in solr : "+novalue);*/
        }
    }

    private void sendEmail() {
        Map<String, String> mail_data = new HashMap<>();
        mail_data.put("task_name", "Solr Cloud Testing");
        mail_data.put("file_name", OUT_PUT_FILE);
        mail_data.put("scope", "Direct queried keyword from solr using zookeeper.");
        CommonUtils.processMailer(mail_data);
    }

    private void getTestData() {
        String file_path = "./src/test/resources/data/Search_Data/";
        String file_name = "Auto_Suggest_Lite.csv";
        testdatainputs = CsvReader.readCsv(file_path + file_name);
    }

    private void processCsvWrite(Map<Integer, String[]> result) {
        String head[] = { "Sr No", "Keyword", "Time (ms)", "Response Ids"};
        WriteCsv.writeCsvWithHeader(OUT_PUT_FILE, head, result);
    }
}