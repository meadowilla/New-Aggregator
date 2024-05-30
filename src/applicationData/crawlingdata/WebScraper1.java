package crawlingdata;

import org.jsoup.nodes.Document;
import java.io.IOException;

public class WebScraper1 extends PageDataExtractor {
    private String[] motherUrls;

    public WebScraper1(String[] motherUrls) {
        this.motherUrls = motherUrls;
    }

    @Override
    public Data extractPageData(String url, Document document) throws IOException {
        String site = document.select("meta[property=og:site_name]").attr("content");
        String title = document.select("meta[property=og:title]").attr("content");
        final String webLink = "https://www.blockchain-council.org";
        String description = document.select("meta[property=og:description]").attr("content");
        String publicationDate = document.select("meta[property=article:published_time]").attr("content");
        String author = document.select("meta[name=author]").attr("content");
        String type = document.select("meta[property=og:type]").attr("content");
        String imageLink = document.select("meta[property=og:image]").attr("content");
        return new Data(url, site, webLink, title, description, author, publicationDate, type, imageLink);
    }

    @Override
    protected String[] getMotherUrls() {
        return motherUrls;
    }
}
