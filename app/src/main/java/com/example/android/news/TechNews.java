package com.example.android.news;

public class TechNews {

    /**
     * Technology News Section
     */
    private String section;

    /**
     * Technology News Title
     */
    private String title;

    /**
     * Time of published news
     */
    private String date;

    /**
     * Author of published news
     */
    private String author;

    /**
     * Website URL of the technology news
     */
    private String url;

    /**
     * Constructs a new {@link TechNews} object.
     *
     * @param section            is the section in which the technology news belongs
     * @param title              is the title of the technology news
     * @param date               is the date when the technology news was published
     * @param author             is the author of technology news
     * @param url                is the website URL to find more details about the technology news
     */
    public TechNews(String section, String title, String date, String author, String url) {
        this.section = section;
        this.title = title;
        this.date= date;
        this.author = author;
        this.url = url;
    }

    /**
     * Getters
     */
    public String getSection() {
        return section;
    }
    public String getTitle() {
        return title;
    }
    public String getDate() {
        return date;
    }
    public String getAuthor() {
        return author;
    }
    public String getUrl() {
        return url;
    }
}