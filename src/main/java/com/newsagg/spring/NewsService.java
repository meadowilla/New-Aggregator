package com.newsagg.spring;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class NewsService {
    
    public List<Data> getAllData() {
        GetData getData = new GetData();
        return getData.getData();
    }

    public List<String> getWebsiteList(List<Data> dataList) {
        List<String> websiteList = new ArrayList<>();
        for (Data data : dataList) {
            if (!websiteList.contains(data.getSourceWebsite())) {
                websiteList.add(data.getSourceWebsite());
            }
        }
        return websiteList;
    }

    public List<Data> getDataForSpecificWebsite(String website, List<Data> dataList) {
        if (website.equals("All")) {
            return dataList;
        } else {
            List<Data> dataForSpecificWebsite = new ArrayList<>();
            for (Data data : dataList) {
                if (data.getSourceWebsite().equals(website)) {
                    dataForSpecificWebsite.add(data);
                }
            }
            return dataForSpecificWebsite;
        }
    }

    public List<Data> getSearchResults(String searchKey, String year, String month) throws JsonProcessingException {
        List<Data> dataList = new ArrayList<>();
        if (searchKey == null || searchKey.isEmpty() || searchKey.equals("All")) {
            dataList = getAllData();
            return dataList;
        }
        else {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(
                    "http://localhost:5000/search?searchkey=" + searchKey + "&year=" + year + "&month=" + month,
                    String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result);
            for (JsonNode node : jsonNode) {
                Data data = new Data();
                data.setLink(node.get(0).asText());
                data.setSourceWebsite(node.get(1).asText());
                data.setWebsite(node.get(2).asText());
                data.setTitle(node.get(3).asText());
                data.setDescription(node.get(4).asText());
                data.setAuthor(node.get(5).asText());
                data.setPublishedDate(node.get(6).asText());
                data.setType(node.get(7).asText());
                data.setImage(node.get(8).asText());
                dataList.add(data);
            }
            return dataList;
        }
    }

    public List<String> getDistinctYears(List<Data> dataList) {
        List<String> yearList = new ArrayList<>();
        for (Data data : dataList) {
            String year = data.getPublishedDate().substring(0, 4);
            if (!yearList.contains(year)) {
                yearList.add(year);
            }
        }
        yearList.sort(Comparator.reverseOrder());
        return yearList;
    }

    public List<String> getDistinctAuthors(List<Data> dataList) {
        List<String> writerList = new ArrayList<>();
        for (Data data : dataList) {
            if (!writerList.contains(data.getAuthor())) {
                writerList.add(data.getAuthor());
            }
        }
        writerList.sort(Comparator.naturalOrder());
        return writerList;
    }

    public List<String> getDistinctTypes(List<Data> dataList) {
        List<String> typeList = new ArrayList<>();
        for (Data data : dataList) {
            if (!typeList.contains(data.getType())) {
                typeList.add(data.getType());
            }
        }
        typeList.sort(Comparator.naturalOrder());
        return typeList;
    }

    public List<Data> filterDataByAttribute(List<Data> dataList, String attribute, String value) {
        List<Data> filteredData = new ArrayList<>();
        for (Data data : dataList) {
            switch (attribute) {
                case "website":
                    if (data.getSourceWebsite().equals(value)) {
                        filteredData.add(data);
                    }
                    break;
                case "author":
                    if (data.getAuthor().equals(value)) {
                        filteredData.add(data);
                    }
                    break;
                case "type":
                    if (data.getType().equals(value)) {
                        filteredData.add(data);
                    }
                    break;
                case "year":
                    if (data.getPublishedDate().substring(0, 4).equals(value)) {
                        filteredData.add(data);
                    }
                    break;
                case "month":
                    if (data.getPublishedDate().substring(5, 7).equals(value)) {
                        filteredData.add(data);
                    }
                    break;
            }
        }
        return filteredData;
    }
}
