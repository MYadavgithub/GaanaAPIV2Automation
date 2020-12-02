package solr;
import java.util.List;
import utils.CsvReader;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.net.URISyntaxException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrCloud {

    int counter = 0;
    CloudSolrClient solrClient;
    ArrayList<String> testdatainputs = null;
    static final int INVOCATION_COUNT = 164;
    public String collection = "searchcollection";
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
            long start = System.currentTimeMillis();
            QueryResponse response = req.process(solrClient);
            long finish = System.currentTimeMillis();
            long issue = finish - start;
            list = response.getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Test(priority = 1, invocationCount = INVOCATION_COUNT)
    public void solrCloudTest() throws URISyntaxException {

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
            long time_took = TimeUnit.MILLISECONDS.toSeconds(finish - start);
            
            if((documentList != null && documentList.size() > 0) && time_took <= 1) {
                for (SolrDocument document : documentList) {
                    int id = Integer.parseInt(document.get("id").toString().trim());
                    Assert.assertEquals(id > 0, true, "Document not holding any unique id!");
                }
            }else{
                log.error("Error!");
                Assert.assertEquals(documentList.size() > 0, true, "Document list is empty!");
            }
        }

        if(counter == 3){
           System.exit(1);
        }
        counter++;
    }

    private void getTestData(){
        String file_path = "./src/test/resources/data/Search_Data/";
        String file_name = "Auto_Suggest_Lite.csv";
        testdatainputs = CsvReader.readCsv(file_path + file_name);
    }
}
