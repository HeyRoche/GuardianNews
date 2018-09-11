package com.example.rojo.guardiannews;

// An {@link Guardian} object contains information related to a single news story.

public class Guardian {

    // Article
    private String mArticle;

    //Section of Article
    private String mSection;

    //Author of the news story
    private String mAuthor;

    //Date of the news story
    private String mDate;

    //Website URL for articles
    private String mURL;

    public Guardian( String article, String section, String date, String url, String author) {
        mArticle = article;
        mAuthor= author;
        mDate= date;
        mSection = section;
        mURL=url;
    }
    public String getArticle(){return mArticle;}
    public String getSection(){return mSection;}
    public String getAuthor(){return mAuthor;}
    public String getDate(){return mDate;}
    public String getUrl(){return mURL;}
}
