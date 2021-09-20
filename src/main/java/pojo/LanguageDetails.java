package pojo;
import java.util.List;

/**
 * @author Umesh.Shukla
 */
public class LanguageDetails {

    List<LanguageEntity> languageDetails;
    
    public LanguageDetails() {

    }

    public LanguageDetails(List<LanguageEntity> languageDetails) {
        this.languageDetails = languageDetails;
    }

    public List<LanguageEntity> getLanguageDetails() {
        return languageDetails;
    }

    public void setLanguageDetails(List<LanguageEntity> languageDetails) {
        this.languageDetails = languageDetails;
    }
}