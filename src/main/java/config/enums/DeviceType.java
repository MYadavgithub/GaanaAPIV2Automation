package config.enums;

public enum DeviceType {
    
    ANDROID_APP("GaanaAndroidApp"),
    IOS_APP("GaanaIphoneApp"),
    MXPLAYER_APP("GaanaMxplayerApp"),
    BOSCH_APP("GaanaBoschApp"),
    MIKO_APP("GaanaMikoApp"),
    GREAT_WALL_APP("GaanaGreatwallmotorsApp"),
    SAMSUNG_FRIDGE_APP("GaanaSamsungfridgeApp"),
    HOMEPOD_APP("GaanaHomePodApp"),
    PODCASTER_APP("GaanaPodcasterApp"),
    GAANA_FAURECIA_APTOIDE_APP("GaanaFaureciaaptoideApp"),
    GAANA_WAP_APP("GaanaWapApp"),
    GAANA_WEBSITE_APP("GaanaWebsiteApp");

    private final String deviceType;

    DeviceType(String deviceType){
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public static DeviceType valueDeviceType(String deviceType){
        for(DeviceType dt : values()){
            if(dt.deviceType.equalsIgnoreCase(deviceType)){
                return dt;
            }
        }
        return null;
    }
}
