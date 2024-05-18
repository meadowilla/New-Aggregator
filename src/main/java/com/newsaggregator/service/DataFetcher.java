package com.newsaggregator.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.csv.*;
import org.springframework.stereotype.Service;

// Link,Source website,Website,Title,Description,Author,Published date,Type,Image
// This class reads the data from the CSV file and returns a list of Data objects
@Service
public class DataFetcher {
    public List<Data> getData() {
        String csvFile = "src\\main\\resources\\data.csv";
        List<Data> dataList = new ArrayList<Data>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine();
            Iterable<CSVRecord> csvParser = CSVFormat.DEFAULT.parse(br);
            for (CSVRecord csvRecord : csvParser) {
                Data data = new Data();
                data.setLink(csvRecord.get(0));
                data.setSourceWebsite(csvRecord.get(1));
                data.setWebsite(csvRecord.get(2));
                data.setTitle(csvRecord.get(3));
                data.setDescription(csvRecord.get(4));
                data.setAuthor(csvRecord.get(5));
                data.setPublishedDate(csvRecord.get(6));
                data.setType(csvRecord.get(7));
                data.setImage(csvRecord.get(8));
                dataList.add(data);
            }
            dataList.sort(Comparator.comparing(Data::getPublishedDate).reversed());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
