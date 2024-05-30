package crawlingdata;

import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.Set;

public class WebScraper2 extends PageDataExtractor {
    private String[] motherUrls;

    public WebScraper2(String[] motherUrls) {
        this.motherUrls = motherUrls;
    }

    @Override
    public Data extractPageData(String url, Document document) throws IOException {
        String site = document.select("meta[name=citation_publisher]").attr("content");
        String title = document.select("meta[name=citation_title]").attr("content");
        final String webLink = "https://www.springeropen.com";
        String description = document.select("meta[property=og:description]").attr("content");
        String publicationDate = document.select("meta[name=citation_publication_date]").attr("content");
        String author = document.select("meta[name=citation_author]").attr("content");
        String type = document.select("meta[name=citation_article_type]").attr("content");
        final String imageLink = "https://www.springeropen.com/static/images/springeropen/logo-springer-open-d04c3ea16c.svg";
        return new Data(url, site, webLink, title, description, author, publicationDate, type, imageLink);
    }

    @Override
    protected String[] getMotherUrls() {
        return motherUrls;
    }

    public static void main(String[] args) {
        // Define motherUrls for WebScraper1
        String[] motherUrls = {
                "https://www.springeropen.com/search?query=blockchain&searchType=publisherSearch"
        };

        // Instantiate WebScraper2 with motherUrls
        WebScraper2 webScraper = new WebScraper2(motherUrls);

        // Scrape web pages
        Set<Data> webPages = webScraper.scrapePages();

        // Write data to CSV file
        CSVWriter csvWriter = new CSVWriter("blockchain.csv");
        csvWriter.writeData(webPages);
        System.out.println("Scraping done. Check blockchain.csv for the data.");
    }
}
