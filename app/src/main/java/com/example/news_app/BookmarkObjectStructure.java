package com.example.news_app;

public class BookmarkObjectStructure {

    private  String image;
    private String title;
    private String date;
    private String section;
    private String id;
    private String url;
    private String periodDiff;

    public void setUrl(String url){
        this.url = url;
    }

    public String getUrl(){
        return url;
    }


    public BookmarkObjectStructure(){
        //Do nothing as of now
    }

    public void setPeriodDiff(String periodDiff){
        this.periodDiff = periodDiff;
    }

    public String getPeriodDiff(){
        return periodDiff;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setSection(String section){
        this.section = section;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getSection() {
        return section;
    }

    public String getId() {
        return id;
    }
}
