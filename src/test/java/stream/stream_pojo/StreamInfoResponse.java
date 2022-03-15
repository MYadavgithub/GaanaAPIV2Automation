package stream.stream_pojo;
import java.util.List;

public class StreamInfoResponse {

    /**
     * @author [umesh.shukla]
     * @email [umesh.shukla@gaana.com]
     * @create date 2021-09-20 19:36:23
     * @modify date 2021-09-20 19:36:23
     * @desc [description]
     */

    private int status;
    private String message;
    private List<StreamUrlDetails> streamingDetails;

    public StreamInfoResponse() {

    }

    public StreamInfoResponse(int status, String message, List<StreamUrlDetails> streamingDetails) {
        this.status = status;
        this.message = message;
        this.streamingDetails = streamingDetails;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public List<StreamUrlDetails> getStreamingDetails() {
        return streamingDetails;
    }
    public void setStreamingDetails(List<StreamUrlDetails> streamingDetails) {
        this.streamingDetails = streamingDetails;
    }
}