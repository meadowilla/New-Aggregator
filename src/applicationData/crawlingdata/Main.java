package crawlingdata;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
            // Define motherUrls for WebScraper1
            String[] motherUrls = {
                    "https://www.blockchain-council.org/category/web-3/",
                    "https://www.blockchain-council.org/category/web-3/page/3/",
                    "https://www.blockchain-council.org/category/web-3/page/2/",
                    "https://www.blockchain-council.org/category/web-3/page/4/"
            };

            // Instantiate WebScraper1 with motherUrls
            WebScraper1 webScraper = new WebScraper1(motherUrls);

            // Scrape web pages
            Set<Data> webPages = webScraper.scrapePages();

            // Write data to CSV file
            CSVWriter csvWriter = new CSVWriter("blockchain.csv");
            csvWriter.writeData(webPages);
            System.out.println("Scraping done. Check blockchain.csv for the data.");
    }
}
