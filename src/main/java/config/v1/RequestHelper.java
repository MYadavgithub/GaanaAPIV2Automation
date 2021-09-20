package config.v1;
import java.util.HashMap;
import java.util.Map;
import common.GlobalConfigHandler;
import config.enums.DeviceType;

/**
 * @author umesh-shukla
 * @version v1
 */
public class RequestHelper {
	public static GetProp prop = null;
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
        if(DeviceType.ANDROID_APP.equals(deviceType)){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
            headers.put("deviceType", prop.getDeviceTypeAndroid());
            headers.put("deviceId", prop.getDeviceId());
            headers.put("appVersion", prop.getAppVersion());
        }
        else if(DeviceType.IOS_APP.equals(deviceType)){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionIos());
            headers.put("deviceType", prop.getDeviceTypeIos());
            headers.put("deviceId", prop.getIosDeviceId());
            headers.put("appVersion", prop.getAppVersionIOS());
        }
        else if(DeviceType.MXPLAYER_APP.equals(deviceType)){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionMx());
            headers.put("deviceType", prop.getDeviceTypeMx());
            headers.put("deviceId", prop.getDeviceId());
            headers.put("appVersion", prop.getAppVersion());
        }
        else if(DeviceType.BOSCH_APP.equals(deviceType)){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
            headers.put("deviceType", prop.getDeviceTypeBosch());
            headers.put("deviceId", prop.getDeviceId());
            headers.put("appVersion", prop.getAppVersionBosch());
        }
        else if(DeviceType.MIKO_APP.equals(deviceType)){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
            headers.put("deviceType", prop.getDeviceTypeMiko());
            headers.put("deviceId", prop.getDeviceId());
            headers.put("appVersion", prop.getAppVersionBosch());
        }
        else if(DeviceType.GREAT_WALL_APP.equals(deviceType)){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
            headers.put("deviceType", prop.getDeviceTypeGreatWall());
            headers.put("deviceId", prop.getDeviceId());
            headers.put("appVersion", prop.getAppVersionBosch());
        }
        else if(DeviceType.SAMSUNG_FRIDGE_APP.equals(deviceType)){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
            headers.put("deviceType", prop.getDeviceTypeSamsungfridge());
            headers.put("deviceId", prop.getDeviceId());
            headers.put("appVersion", prop.getAppVersionSamsungfridge());
        }
        else if(DeviceType.HOMEPOD_APP.equals(deviceType)){
            // not found
        }
        else if(DeviceType.PODCASTER_APP.equals(deviceType)){
            // nnot found
        }
        else if(DeviceType.GAANA_FAURECIA_APTOIDE_APP.equals(deviceType)){
            headers.put("gaanaAppVersion", prop.getGaanaAppVersionAndroid());
            headers.put("deviceType", prop.getDeviceTypeGFA());
            headers.put("deviceId", prop.getDeviceId());
            headers.put("appVersion", prop.getAppVersionBosch());
        }
        else if(DeviceType.GAANA_WAP_APP.equals(deviceType)){
            headers.put("deviceType", prop.getDeviceTypeWap());
            headers.put("deviceId", prop.getDeviceIdWap());
            headers.put("CIP", prop.getCIP());
            headers.put("appVersion", prop.getAppVersionWapApp());
        }
        else if(DeviceType.GAANA_WEBSITE_APP.equals(deviceType)){
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
}