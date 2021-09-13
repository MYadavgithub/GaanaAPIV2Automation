package pojo;
import java.util.List;

/**
 * @author Umesh.Shukla
 */
public class AssociatedLanguages {
    
    private String language;
    private List<AssociatedLanguageEntity> associatedlanguageEntity;

    public AssociatedLanguages() {

    }
   
    public AssociatedLanguages(String language, List<AssociatedLanguageEntity> associatedlanguageEntity) {
        this.language = language;
        this.associatedlanguageEntity = associatedlanguageEntity;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<AssociatedLanguageEntity> getLanguageEntity() {
        return associatedlanguageEntity;
    }

    public void setLanguageEntity(List<AssociatedLanguageEntity> associatedlanguageEntity) {
        this.associatedlanguageEntity = associatedlanguageEntity;
    }
}