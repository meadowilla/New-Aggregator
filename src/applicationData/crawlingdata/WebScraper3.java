package crawlingdata;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WebScraper3 extends PageDataExtractor {
    private String[] motherUrls;

    public WebScraper3(String[] motherUrls) {
        this.motherUrls = motherUrls;
    }

    @Override
    public Set<Data> scrapePages() {
        Set<Data> articles = new HashSet<>();

        try {
            String[] motherUrls = getMotherUrls();
            for (String motherUrl : motherUrls) {
                Set<String> childUrls = new HashSet<>(); // Initialize for each mother page

                boolean hasNextPage = true;
                while (hasNextPage) {
                    Document motherDoc = Jsoup.connect(motherUrl)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                            .get();

                    Elements links = motherDoc.select("a[href]"); // This selects all <a> elements with an href attribute
                    for (Element link : links) {
                        String childUrl = link.attr("abs:href"); // 'abs:href' gets the absolute URL
                        childUrls.add(childUrl);
                    }

                    Elements nextElements = motherDoc.select("a.c-pagination__link[rel=next]");
                    if (!nextElements.isEmpty()) {
                        Element nextElement = nextElements.first();
                        motherUrl = nextElement.attr("abs:href");
                    } else {
                        hasNextPage = false;
                    }
                }

                // Now iterate through the child URLs and scrape each page
                for (String childUrl : childUrls) {
                    Document childDoc = Jsoup.connect(childUrl).get();
                    Data article = extractPageData(childUrl, childDoc);

                    if (article != null) {
                        articles.add(article);
                        // Delay before scraping next page to avoid overloading server
                        TimeUnit.MILLISECONDS.sleep(1000); // 1 second delay
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return articles;
    }



    @Override
    public Data extractPageData(String url, Document document) throws IOException {
        final String site = "Consumer News and Business Channel";
        String title = document.select("meta[property=og:title]").attr("content");
        final String webLink = "https://www.cnbc.com";
        String description = document.select("meta[property=og:description]").attr("content");
        String publicationDate = document.select("meta[itemprop=dateCreated]").attr("content");
        String author = document.select("meta[name=author]").attr("content");
        String type = document.select("meta[property=og:type]").attr("content");
        String imageLink = document.select("meta[property=og:image]").attr("content");
        return new Data(url, site, webLink, title, description, author, publicationDate, type, imageLink);
    }

    @Override
    protected String[] getMotherUrls() {
        return motherUrls;
    }

    public static void main(String[] args) {
        // Define motherUrls for WebScraper1
        String[] motherUrls = {
                "https://www.cnbc.com/blockchain/"
        };

        // Instantiate WebScraper3 with motherUrls
        WebScraper3 webScraper = new WebScraper3(motherUrls);

        // Scrape web pages
        Set<Data> webPages = webScraper.scrapePages();

        // Write data to CSV file
        CSVWriter csvWriter = new CSVWriter("blockchain.csv");
        csvWriter.writeData(webPages);
        System.out.println("Scraping done. Check blockchain.csv for the data.");
    }
}
