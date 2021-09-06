package pojo;
import org.testng.Assert;

public class RecommendedShowPojo {
    
    private int iid;
    private String ti;
    private String aw;
    private String seo;
    private String ty;
    private boolean vty;
    private String fty;
    private String language;
    private String subtitle;
    
    public RecommendedShowPojo() {
    }
    
    public RecommendedShowPojo(int iid, String ti, String aw, String seo, String ty, boolean vty, String fty,
            String language, String subtitle) {
        this.iid = iid;
        this.ti = ti;
        this.aw = aw;
        this.seo = seo;
        this.ty = ty;
        this.vty = vty;
        this.fty = fty;
        this.language = language;
        this.subtitle = subtitle;
    }

    public int getIid() {
        return iid;
    }
    public void setIid(int iid) {
        this.iid = iid;
    }
    public String getTi() {
        return ti;
    }
    public void setTi(String ti) {
        this.ti = ti;
    }
    public String getAw() {
        return aw;
    }
    public void setAw(String aw) {
        this.aw = aw;
    }
    public String getSeo() {
        return seo;
    }
    public void setSeo(String seo) {
        this.seo = seo;
    }
    public String getTy() {
        return ty;
    }
    public void setTy(String ty) {
        this.ty = ty;
    }
    public boolean isVty() {
        return vty;
    }
    public void setVty(boolean vty) {
        this.vty = vty;
    }
    public String getFty() {
        return fty;
    }
    public void setFty(String fty) {
        this.fty = fty;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getSubtitle() {
        return subtitle;
    }
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void validIid(int iid){
        Assert.assertTrue(iid >= 0);
    }

    public void validTi(String ti){
        Assert.assertTrue(ti.trim().length() > 0);
    }

    public void validAw(String aw){
        Assert.assertTrue(aw.length() > 0);
    }

    public void validateShowSubtitle(String ex_subtitle){
        Assert.assertTrue(ex_subtitle.equals(subtitle));
    }

    public void validateLanguage(String language) {
        Assert.assertTrue(language.length() > 0);
    }
}