package pojo;
import java.util.List;

/**
 * @author Umesh.Shukla
 */
public class AssociatedLanguagesPojo {
    
    private List<AssociatedLanguages> associatedLanguages;

    public AssociatedLanguagesPojo() {
    }

    public AssociatedLanguagesPojo(List<AssociatedLanguages> associatedLanguages) {
        this.associatedLanguages = associatedLanguages;
    }

    public List<AssociatedLanguages> getAssociatedLanguages() {
        return associatedLanguages;
    }

    public void setAssociatedLanguages(List<AssociatedLanguages> associatedLanguages) {
        this.associatedLanguages = associatedLanguages;
    }
}
