package com.example.news_app;

public class HomePageCardStructure {


    private String title;
    private String image;
    private String date;
    private String section;
    private String id;
    private String webUrl;
    private String currentDate;
    private String periodDiff;

    public HomePageCardStructure(String image, String title){
        this.image = image;
        this.title = title;
    }

    public void setPeriodDiff(String periodDiff) {
        this.periodDiff = periodDiff;
    }

    public String getPeriodDiff() {
        return periodDiff;
    }

    public String getWebUrl(){
        return webUrl;
    }
    public String getTitle(){
        return title;
    }

    public String getImage(){
        return image;
    }

    public String getDate(){
        return date;
    }

    public String getSection(){
        return section;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setSection(String section){
        this.section = section;
    }

    public void setWebUrl(String webUrl){
        this.webUrl = webUrl;
    }

}
