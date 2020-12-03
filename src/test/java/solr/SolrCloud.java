package solr;
import java.util.List;
import utils.CsvReader;
import org.slf4j.Logger;
import org.testng.Assert;
import java.util.Optional;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import java.util.concurrent.TimeUnit;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;

public class SolrCloud {

    int counter = 0;
    CloudSolrClient solrClient;
    ArrayList<String> testdatainputs = null;
    static final int INVOCATION_COUNT = 164;
    public String collection = "searchcollection";
    ArrayList<Long> time_taken = new ArrayList<>();
    private static Logger log = LoggerFactory.getLogger(SolrCloud.class);

    public CloudSolrClient createConnection() {
        List<String> zkServers = new ArrayList<String>();
        zkServers.add("172.26.11.216:2181");
        zkServers.add("172.26.11.217:2181");
        zkServers.add("172.26.11.218:2181");

        solrClient = new CloudSolrClient.Builder(zkServers, Optional.of("/gaana-solr")).build();
        solrClient.setDefaultCollection(collection);
        return solrClient;
    }

    public SolrDocumentList getSolrResponse(SolrQuery solrQuery) {
        SolrDocumentList list = null;
        try {
            QueryRequest req = new QueryRequest(solrQuery);
            // solrClient.setDefaultCollection(collection);
            // long start = System.currentTimeMillis();
            QueryResponse response = req.process(solrClient);
            // long finish = System.currentTimeMillis();
            // long issue = finish - start;
            // log.error("msg --> "+issue);
            list = response.getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Test(priority = 1, invocationCount = INVOCATION_COUNT)
    public void solrCloudTest() {
        ArrayList<String> novalue = new ArrayList<>();
        if (solrClient == null) {
            createConnection();
        }

        getTestData();
        String keyword = testdatainputs.get(counter);

        if (!keyword.equals("Keyword")) {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery("title:" + keyword);
            solrQuery.setRows(50);
            solrQuery.set("collection", collection);
            solrQuery.set("wt", "json");
            // solrQuery.setFilterQueries(keyword);
            long start = System.currentTimeMillis();
            SolrDocumentList documentList = getSolrResponse(solrQuery);
            long finish = System.currentTimeMillis();
            time_taken.add(finish - start);
            long time_took = TimeUnit.MILLISECONDS.toSeconds(finish - start);

            if((documentList != null && documentList.size() > 0) && time_took <= 1) {
                for (SolrDocument document : documentList) {
                    int id = Integer.parseInt(document.get("id").toString().trim());
                    Assert.assertEquals(id > 0, true, "Document not holding any unique id!");
                }
            }else{
                log.error("Error!");
                novalue.add(keyword);
                //Assert.assertEquals(documentList.size() > 0, true, "Document list is empty!");
            }
        }

        counter++;
        if(counter == INVOCATION_COUNT){
            int count = 1;
            ArrayList<String> values = new ArrayList<>();
            for(Long t_ms : time_taken){
                if(t_ms > 200){
                    values.add(testdatainputs.get(count));
                }
                count++;
            }
            log.info("Times in ms for each request : "+time_taken+"\n\n");
            log.info("These keywords took more than 200ms to fetch data from solr : "+values);
            log.info("\n\nThese keywords having no value in solr : "+novalue);
        }

    }

    private void getTestData(){
        String file_path = "./src/test/resources/data/Search_Data/";
        String file_name = "Auto_Suggest_Lite.csv";
        testdatainputs = CsvReader.readCsv(file_path + file_name);
    }
}
