package config.v1;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.*;
import common.GlobalConfigHandler;
import config.enums.DeviceType;

/**
 * @author umesh-shukla
 * @version v1
 */
public class RequestHelper {

	public static GetProp prop = null;
    private static Logger LOGGER = LoggerFactory.getLogger(RequestHelper.class);

    /* Request type for API */
	public enum ApiRequestTypes{
		DELETE, GET, POST, PUT, PATCH
	}

	public enum ContentTypes{
		ANY, BINARY, HTML, JSON, TEXT, URLENC, XML
	}

	/** 
     * @param country 0 for country name and 1 for x-forwarded-for
     * @return
     */
	public static Map<String, String> getHeader(int country){
		int device_type = 0;
		Map<String, String> headers = new HashMap<String, String>();
		device_type = GlobalConfigHandler.getDeviceType();
		prop = new GetProp();
		if(device_type == 0){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
            headers.put("deviceType", prop.getDeviceTypeAndroid());
            headers.put("deviceId", prop.getDeviceId());
            headers.put("appVersion", prop.getAppVersion());
        }
        else if(device_type == 1){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionIos());
            headers.put("deviceType", prop.getDeviceTypeIos());
            headers.put("deviceId", prop.getIosDeviceId());
            headers.put("appVersion", prop.getAppVersionIOS());
        }
        else if(device_type == 2){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
            headers.put("deviceType", prop.getDeviceTypeBosch());
            headers.put("deviceId", prop.getDeviceId());
            headers.put("appVersion", prop.getAppVersionBosch());
        }
        else if(device_type == 3){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
            headers.put("deviceType", prop.getDeviceTypeGreatWall());
            headers.put("deviceId", prop.getDeviceId());
            headers.put("appVersion", prop.getAppVersionBosch());
        }
        else if(device_type == 4){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
            headers.put("deviceType", prop.getDeviceTypeSamsungfridge());
            headers.put("deviceId", prop.getDeviceId());
            headers.put("appVersion", prop.getAppVersionSamsungfridge());
        }
        else if(device_type == 5){
            headers.put("deviceType", prop.getDeviceTypeWap());
            headers.put("deviceId", prop.getDeviceIdWap());
            headers.put("CIP", prop.getCIP());
            headers.put("appVersion", prop.getAppVersionWapApp());
        }
        else if(device_type == 6){
            headers.put("deviceType", prop.getDeviceTypeWebApp());
            headers.put("deviceId", prop.getDeviceIdWebApp());
            headers.put("CIP", prop.getCIPWebApp());
            headers.put("appVersion", prop.getAppVersionWebApp());
        }

		if(country == 0){
            headers.put("COUNTRY", prop.getCountry());
        }else{
            headers.put("X-FORWARDED-FOR", prop.getxForwardedFor());
        }
		return headers;
	}

    public static Map<String, String> getHeaders(int country, DeviceType deviceType){
        Map<String, String> headers = new HashMap<String, String>();
        switch (deviceType) {
            case ANDROID_APP:
                headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
                headers.put("deviceType", prop.getDeviceTypeAndroid());
                headers.put("deviceId", prop.getDeviceId());
                headers.put("appVersion", prop.getAppVersion());
            break;

            case IOS_APP:
                headers.put("gaanaAppVersion", prop.getGaanaAppVersionIos());
                headers.put("deviceType", prop.getDeviceTypeIos());
                headers.put("deviceId", prop.getIosDeviceId());
                headers.put("appVersion", prop.getAppVersionIOS());
            break;

            case MXPLAYER_APP:
                headers.put("gaanaAppVersion", prop.getGaanaAppVersionMx());
                headers.put("deviceType", prop.getDeviceTypeMx());
                headers.put("deviceId", prop.getDeviceId());
                headers.put("appVersion", prop.getAppVersion());
            break;

            case BOSCH_APP:
                headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
                headers.put("deviceType", prop.getDeviceTypeBosch());
                headers.put("deviceId", prop.getDeviceId());
                headers.put("appVersion", prop.getAppVersionBosch());
            break;

            case MIKO_APP:
                headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
                headers.put("deviceType", prop.getDeviceTypeMiko());
                headers.put("deviceId", prop.getDeviceId());
                headers.put("appVersion", prop.getAppVersionBosch());
            break;

            case GREAT_WALL_APP:
                headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
                headers.put("deviceType", prop.getDeviceTypeGreatWall());
                headers.put("deviceId", prop.getDeviceId());
                headers.put("appVersion", prop.getAppVersionBosch());
            break;

            case SAMSUNG_FRIDGE_APP:
                headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
                headers.put("deviceType", prop.getDeviceTypeSamsungfridge());
                headers.put("deviceId", prop.getDeviceId());
                headers.put("appVersion", prop.getAppVersionSamsungfridge());
            break;

            case HOMEPOD_APP:
                // not found);
            break;

            case PODCASTER_APP:
                // not found);
            break;

            case GAANA_FAURECIA_APTOIDE_APP:
                headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
                headers.put("deviceType", prop.getDeviceTypeGFA());
                headers.put("deviceId", prop.getDeviceId());
                headers.put("appVersion", prop.getAppVersionBosch());
            break;

            case GAANA_WAP_APP:
                headers.put("deviceType", prop.getDeviceTypeWap());
                headers.put("deviceId", prop.getDeviceIdWap());
                headers.put("CIP", prop.getCIP());
                headers.put("appVersion", prop.getAppVersionWapApp());
            break;

            case GAANA_WEBSITE_APP:
                headers.put("deviceType", prop.getDeviceTypeWebApp());
                headers.put("deviceId", prop.getDeviceIdWebApp());
                headers.put("CIP", prop.getCIPWebApp());
                headers.put("appVersion", prop.getAppVersionWebApp());
            break;

            default :
                LOGGER.warn("Device type invalid : "+deviceType);

        }

        if(country == 0){
            headers.put("COUNTRY", prop.getCountry());
        }else{
            headers.put("X-FORWARDED-FOR", prop.getxForwardedFor());
        }
        return headers;
    }
}