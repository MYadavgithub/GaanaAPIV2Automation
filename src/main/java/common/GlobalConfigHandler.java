package common;
import config.Constants;

public class GlobalConfigHandler {
    
    /**
     * Get Execution Environment
     * @return
     */
    public static String getEnv(){
        String environment = "";
        String env = System.getProperty("env");
        if(env != null){
            if(env.equalsIgnoreCase("local")){
                environment = Constants.STAGE_ENV;
            }else if(env.equalsIgnoreCase("preprod")){
                environment = Constants.PRE_PROD_ENV;
            }else if(env.equalsIgnoreCase("production")){
                environment = Constants.PROD_ENV;
            }
        }else{
            environment = Constants.STAGE_ENV;
        }
        return environment;
    }

    /**
     * Get API type
     */
    public static String getType(){
        String type = "";
        String env = System.getProperty("type");
        if(env != null){
            if(env.equalsIgnoreCase("Search")){
                type = Constants.API_TYPE_SEARCH;
            }else if(env.equalsIgnoreCase("Reco")){
                type = Constants.API_TYPE_RECO;
            }
        }else{
            type = null;
        }
        return type;
    }

    /**
     * Get Device Type
     * default value android.
     */
    public static int getDeviceType(){
        String env = System.getProperty("device_type");
        if(env != null){
            if(env.equalsIgnoreCase(Constants.ANDROID)){
                return 0;
            }else if(env.equalsIgnoreCase(Constants.IOS)){
                return 1;
            }else if(env.equalsIgnoreCase(Constants.WEB)){
                return 2;
            }
        }
        return 0;
    }

    /**
     * API loop count 
     * @param loop_count
     * @param context
     */
    // public void provideApiHitCount(ITestContext context, int count){
    //     ITestNGMethod currentTestNGMethod = null;
    //     for(ITestNGMethod testNGMethod : context.getAllTestMethods()) {
    //         if(testNGMethod.getInstance() == this) {
    //             currentTestNGMethod = testNGMethod;
    //             break;
    //         }
    //     }
    //     currentTestNGMethod.setInvocationCount(count);
    // }
}
