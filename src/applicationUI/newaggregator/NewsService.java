package application_ui.newaggregator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NewsService {

    private final DataFetcher getData;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public NewsService(DataFetcher getData, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.getData = getData;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    public List<Data> getAllData() {
        return getData.getData();
    }

    public List<String> getWebsiteList(List<Data> dataList) {
        return dataList.stream()
                .map(Data::getSourceWebsite)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Data> getSearchResults(String searchKey, String year, String month) throws JsonProcessingException {
        List<Data> dataList = new ArrayList<>();
        
        if (searchKey == null || searchKey.isEmpty() || searchKey.equals("All")) {
            dataList = getAllData();
        } else {
            String result = restTemplate.getForObject(
                    "http://localhost:5000/search?searchkey=" + searchKey + "&year=" + year + "&month=" + month,
                    String.class);
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
        }
        
        return dataList;
    }


    public List<String> getDistinctYears(List<Data> dataList) {
        return dataList.stream()
                .map(data -> data.getPublishedDate().substring(0, 4))
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public List<String> getDistinctAuthors(List<Data> dataList) {
        return dataList.stream()
                .map(Data::getAuthor)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getDistinctTypes(List<Data> dataList) {
        return dataList.stream()
                .map(Data::getType)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Data> filterDataByAttribute(List<Data> dataList, String attribute, String value) {
        return dataList.stream()
                .filter(data -> {
                    switch (attribute) {
                        case "website":
                            return data.getSourceWebsite().equals(value);
                        case "author":
                            return data.getAuthor().equals(value);
                        case "type":
                            return data.getType().equals(value);
                        case "year":
                            return data.getPublishedDate().substring(0, 4).equals(value);
                        case "month":
                            return data.getPublishedDate().substring(5, 7).equals(value);
                        default:
                            return false;
                    }
                })
                .collect(Collectors.toList());
    }
}
