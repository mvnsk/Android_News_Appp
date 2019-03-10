package com.example.news_app;

public class HeadLineStructure {
    private String image;
    private String title;
    private String section;
    private String date;
    private String source_name;
    private  String comment_box_id;
    private String sharing_modal_url;
    private String bookmark_identifier;
    private String id_for_details_page;
    private  String description;
    private String periodDiff;

    public void setPeriodDiff(String periodDiff){
        this.periodDiff = periodDiff;
    }

    public String getPeriodDiff(){
        return periodDiff;
    }

    public HeadLineStructure(String image, String title, String description){
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public void setImage(String image){
        this.image = image;

    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getTitle(){
        return  title;
    }

    public String getDescription(){
        return description;
    }

    public String getImage(){
        return  image;
    }

    public String getSection(){return section;}

    public String getDate(){return date;}

    public void setDate(String date){
        this.date=date;
    }

    public String getSourceName(){return source_name;}

    public String getSharingModalUrl(){return sharing_modal_url;}

    public String getId(){return id_for_details_page;}

    public void setId(String id){this.id_for_details_page = id;}

    public void setUrl(String url){this.sharing_modal_url = url;}

    public void setSection(String section){
        this.section = section;
    }
}
