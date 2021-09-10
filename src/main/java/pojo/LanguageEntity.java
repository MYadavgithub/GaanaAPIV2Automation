package pojo;

/**
 * @author Umesh.Shukla
 */
public class LanguageEntity {
        
    private int id;
    private String language;
    private double weight;
    
    public LanguageEntity() {
    }

    public LanguageEntity(int id, String language, double weight) {
        this.id = id;
        this.language = language;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}