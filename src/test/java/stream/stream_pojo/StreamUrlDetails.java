package stream.stream_pojo;
import com.fasterxml.jackson.annotation.JsonProperty;

 /**
 * @author [umesh.shukla]
 * @email [umesh.shukla@gaana.com]
 * @create date 2021-09-20 19:36:23
 * @modify date 2021-09-20 19:36:23
 * @desc [description]
 */

public class StreamUrlDetails {

    @JsonProperty(value = "track_id")
    private String trackId;

    @JsonProperty(value = "stream_url")
    private String streamUrl;

    @JsonProperty(value = "expiry_time")
    private String expiryTime;

    public String getTrackId() {
        return trackId;
    }
    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }
    public String getStreamUrl() {
        return streamUrl;
    }
    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
    public String getExpiryTime() {
        return expiryTime;
    }
    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }
}