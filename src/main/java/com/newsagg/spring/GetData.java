package com.newsagg.spring;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.*;




public class GetData {
    public List<Data> getData() {
        String csvFile = "src/main/resources/blockchain.csv";
        List<Data> d = new ArrayList<Data>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            Iterable<CSVRecord> csvParser = CSVFormat.DEFAULT.parse(br);
                for (CSVRecord csvRecord : csvParser) {
                    Data data = new Data();
                    data.setLink(csvRecord.get(0));
                    data.setWebsite(csvRecord.get(1));
                    data.setTitle(csvRecord.get(2));
                    data.setDescription(csvRecord.get(3));
                    data.setAuthor(csvRecord.get(4));
                    data.setDate(csvRecord.get(5));
                    data.setType(csvRecord.get(6));
                    data.setKeywords(csvRecord.get(7)); 
                    d.add(data);   
                }
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    return d;
    }
}
