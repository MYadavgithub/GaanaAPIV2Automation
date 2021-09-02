package pojo;

import org.junit.Assert;

public class RecommendedEntityPojo {

    private int iid;
    private String ti;
    private String aw;
    private String sti;
    private String lang[];
    private String ty;
    private String fty;
    private String tags[];
    private float scoreF;
    private String subtitle;
    
    public RecommendedEntityPojo() {
    }

    public RecommendedEntityPojo(int iid, String ti, String aw, String sti, String[] lang, String ty, String fty,
            String[] tags, float scoreF, String subtitle) {
        this.iid = iid;
        this.ti = ti;
        this.aw = aw;
        this.sti = sti;
        this.lang = lang;
        this.ty = ty;
        this.fty = fty;
        this.tags = tags;
        this.scoreF = scoreF;
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
    public String getSti() {
        return sti;
    }
    public void setSti(String sti) {
        this.sti = sti;
    }
    public String[] getLang() {
        return lang;
    }
    public void setLang(String[] lang) {
        this.lang = lang;
    }
    public String getTy() {
        return ty;
    }
    public void setTy(String ty) {
        this.ty = ty;
    }
    public String getFty() {
        return fty;
    }
    public void setFty(String fty) {
        this.fty = fty;
    }
    public String[] getTags() {
        return tags;
    }
    public void setTags(String[] tags) {
        this.tags = tags;
    }
    public float getScoreF() {
        return scoreF;
    }
    public void setScoreF(float scoreF) {
        this.scoreF = scoreF;
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

    public void validLang(String[] lang) {
        Assert.assertTrue(lang.length >= 1);
    }

    public void validTy(String ty) {
        Assert.assertTrue(ty.length() > 0);
    }
}