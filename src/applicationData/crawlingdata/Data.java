package crawlingdata;

public class Data {
    private String url;
    private String site;
    private String title;
    private String description;
    private String author;
    private String publicationDate;
    private String type;
    private String imageLink;
    private String webLink;

    public String getWebLink() {
        return webLink;
    }

    public String getSite() {
        return site;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getType() {
        return type;
    }

    public String getImageLink() {
        return imageLink;
    }

    public Data(String url, String site, String webLink, String title, String description, String author, String publicationDate, String type, String imageLink) {
        this.url = url;
        this.site = site;
        this.webLink=webLink;
        this.title = title;
        this.description = description;
        this.author = author;
        this.publicationDate = publicationDate;
        this.type = type;
        this.imageLink = imageLink;
    }  //constructor

    public String toCsvFormat() {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n", url, site, title, description, author, publicationDate, type, imageLink);
    }
}
