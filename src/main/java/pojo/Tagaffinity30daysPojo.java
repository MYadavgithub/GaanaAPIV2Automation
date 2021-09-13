package pojo;
import java.util.List;

public class Tagaffinity30daysPojo {

    private String id;
    private List<String> tags;
    
    public Tagaffinity30daysPojo(String id, List<String> tags) {
        this.id = id;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Tagaffinity30days [id=" + id + ", tags=" + tags + "]";
    }    
}