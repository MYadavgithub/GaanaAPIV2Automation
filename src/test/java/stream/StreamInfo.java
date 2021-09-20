// package stream;
// import org.slf4j.*;
// import org.testng.annotations.BeforeClass;
// import org.testng.annotations.DataProvider;
// import org.testng.annotations.Test;
// import common.GlobalConfigHandler;
// import test_data.StreamTd;

// /**
//  * @author [umesh.shukla]
//  * @email [umesh.shukla@gaana.com]
//  * @create date 2021-09-20 12:07:36
//  * @modify date 2021-09-20 12:07:36
//  * @desc [StreamInfo streaming validations]
//  */

// public class StreamInfo {
    
//     int API_CALL = 0;
//     int MAX_CALL = 0;
//     public String BASEURL;
//     GlobalConfigHandler handler = new GlobalConfigHandler();
//     private static Logger LOGGER = LoggerFactory.getLogger(StreamInfo.class);

//     @BeforeClass
//     public void setEnv(){
//         BASEURL = GlobalConfigHandler.baseurl();
//         // MAX_CALL = StreamTd.TRACK_IDS.length;
//     }

//     @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = StreamTd.INVOCATION)
//     public void prepareStramInfoUrls(int track_id) {
        
//         LOGGER.info("Umesh => "+BASEURL+"/"+track_id);
//         API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
//     }

//     @DataProvider(name = "dp")
//     public Object [][] trackIds(){
//         return new Object[][]
//         {
//             {
//                 StreamTd.TRACK_IDS[API_CALL]
//             }
//         };
//     }
// }