package stream.stream_pojo;

public class GetUrlV1Response {
    
    /**
     * @author [umesh.shukla]
     * @email [umesh.shukla@gaana.com]
     * @create date 2021-09-20 19:36:23
     * @modify date 2021-09-20 19:36:23
     * @desc [description]
     */

    private int status;
    private String data;
    private String bitrate;
    private String quality;
    private String user_token_status;
    private String protocol;
    private int content_source;
    private String track_format;
    private String premium_content;
    private String parental_warning;
    private int et;
    private int av_ad;

    public GetUrlV1Response(){

    }

    public GetUrlV1Response(int status, String data, String bitrate, String quality, String user_token_status,
            String protocol, int content_source, String track_format, String premium_content, 
            String parental_warning, int et, int av_ad) {
        this.status = status;
        this.data = data;
        this.bitrate = bitrate;
        this.quality = quality;
        this.user_token_status = user_token_status;
        this.protocol = protocol;
        this.content_source = content_source;
        this.track_format = track_format;
        this.premium_content = premium_content;
        this.parental_warning = parental_warning;
        this.et = et;
        this.av_ad = av_ad;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getBitrate() {
        return bitrate;
    }
    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }
    public String getQuality() {
        return quality;
    }
    public void setQuality(String quality) {
        this.quality = quality;
    }
    public String getUser_token_status() {
        return user_token_status;
    }
    public void setUser_token_status(String user_token_status) {
        this.user_token_status = user_token_status;
    }
    public int getContent_source() {
        return content_source;
    }
    public void setContent_source(int content_source) {
        this.content_source = content_source;
    }
    public String getTrack_format() {
        return track_format;
    }
    public void setTrack_format(String track_format) {
        this.track_format = track_format;
    }
    public String getPremium_content() {
        return premium_content;
    }
    public void setPremium_content(String premium_content) {
        this.premium_content = premium_content;
    }
    public String getParental_warning() {
        return parental_warning;
    }
    public void setParental_warning(String parental_warning) {
        this.parental_warning = parental_warning;
    }
    public int getEt() {
        return et;
    }
    public void setEt(int et) {
        this.et = et;
    }
    public int getAv_ad() {
        return av_ad;
    }
    public void setAv_ad(int av_ad) {
        this.av_ad = av_ad;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
