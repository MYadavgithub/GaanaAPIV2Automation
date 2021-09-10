package pojo;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Umesh.Shukla
 */
public class SimilarTopShowPojo {
    
    private String count;
    private int status;
    private int [] shows; 
    @JsonProperty(value = "user-token-status")
    private int userTokenStatus;

    public SimilarTopShowPojo() {

    }
    
    public SimilarTopShowPojo(String count, int status, int [] shows, int userTokenStatus) {
        this.count = count;
        this.status = status;
        this.shows = shows;
        this.userTokenStatus = userTokenStatus;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int [] getShows() {
        return shows;
    }

    public void setShows(int [] shows) {
        this.shows = shows;
    }

    public int getUserTokenStatus() {
        return userTokenStatus;
    }

    public void setUserTokenStatus(int userTokenStatus) {
        this.userTokenStatus = userTokenStatus;
    }
}
