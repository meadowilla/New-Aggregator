package com.newsagg.spring;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class RunController {
    @GetMapping("/home")
    public String home(ModelMap model) {
        GetData getData = new GetData();
        List<String> websiteList = new ArrayList<String>();
        for (Data data : getData.getData()) {
            if (!websiteList.contains(data.getSourceWebsite())) {
                websiteList.add(data.getSourceWebsite());
            }
        }
        List<Data> dataForSpecificWebsite = new ArrayList<Data>();
        dataForSpecificWebsite = new GetData().getData();
        dataForSpecificWebsite.sort(Comparator.comparing(Data::getPublishedDate).reversed());
        List<Data> dataListInProcess = new ArrayList<Data>();
        for (Data data : getData.getData()) {
            if (data.getSourceWebsite().equals("The Block")) {
                dataListInProcess.add(data);
            }
        }
        dataForSpecificWebsite = dataListInProcess;

        model.addAttribute("news", getData.getData());
        model.addAttribute("websiteList", websiteList);
        model.addAttribute("selectWebsite", "The Block");
        model.addAttribute("dataForSpecificWebsite", dataForSpecificWebsite);

        return "home";
    }

    @PostMapping("/home")
    public String homePost(@RequestParam(value = "selectWebsite", defaultValue = "The Block") String selectWebsite,
            ModelMap model) throws JsonMappingException, JsonProcessingException {

        List<Data> dataList = new ArrayList<Data>();
        dataList = new GetData().getData();
        List<Data> dataForSpecificWebsite = new ArrayList<Data>();
        dataForSpecificWebsite = new GetData().getData();
        dataList.sort(Comparator.comparing(Data::getPublishedDate).reversed());
        List<String> websiteList = new ArrayList<String>();
        for (Data data : dataList) {
            if (!websiteList.contains(data.getSourceWebsite())) {
                websiteList.add(data.getSourceWebsite());
            }
        }
        if (!selectWebsite.equals("All")) {
            List<Data> dataListInProcess = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getSourceWebsite().equals(selectWebsite)) {
                    dataListInProcess.add(data);
                }
            }
            dataForSpecificWebsite = dataListInProcess;
        }

        model.addAttribute("news", dataList);
        model.addAttribute("websiteList", websiteList);
        model.addAttribute("dataForSpecificWebsite", dataForSpecificWebsite);
        model.addAttribute("selectWebsite", selectWebsite);
        return "home";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "searchKey", required = false) String searchKey,
            @RequestParam(value = "year", defaultValue = "Year") String year,
            @RequestParam(value = "month", defaultValue = "Month") String month,
            @RequestParam(value = "newestOldest", defaultValue = "Newest") String newestOldest,
            @RequestParam(value = "selectWebsite", defaultValue = "All") String selectWebsite,
            @RequestParam(value = "selectWriter", defaultValue = "All") String selectWriter,
            @RequestParam(value = "selectType", defaultValue = "All") String selectType,
            ModelMap model) throws JsonMappingException, JsonProcessingException {

        // If the search key is empty, display all result in database
        // Otherwise, get search results from the API
        GetData getData = new GetData();
        List<Data> dataList = new ArrayList<Data>();
        if (searchKey.equals("")) {
            dataList = getData.getData();
            searchKey = "All";
        } else {
            // Get search results from the API
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(
                    "http://localhost:5000/search?searchkey=" + searchKey + "&year=" + year + "&month=" + month,
                    String.class);
            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result);
            // Create a list of Data objects to store the search results
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
        // Sort the search results based on the user's selection
        if (newestOldest.equals("Oldest")) {
            dataList.sort(Comparator.comparing(Data::getPublishedDate));
        } else {
            dataList.sort(Comparator.comparing(Data::getPublishedDate).reversed());
        }
        // Get the list of years for the dropdown menu
        List<String> yearList = new ArrayList<String>();
        for (Data data : getData.getData()) {
            if (!yearList.contains(data.getPublishedDate().substring(0, 4))) {
                yearList.add(data.getPublishedDate().substring(0, 4));
                yearList.sort(Comparator.reverseOrder());
            }
        }

        List<String> websiteList = new ArrayList<String>();
        for (Data data : dataList) {
            if (!websiteList.contains(data.getSourceWebsite())) {
                websiteList.add(data.getSourceWebsite());
            }
        }
        websiteList.sort(Comparator.naturalOrder());
        if (!selectWebsite.equals("All")) {
            List<Data> dataListInProcess = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getSourceWebsite().equals(selectWebsite)) {
                    dataListInProcess.add(data);
                }
            }
            dataList = dataListInProcess;
        }

        List<String> writerList = new ArrayList<String>();
        for (Data data : dataList) {
            if (!writerList.contains(data.getAuthor())) {
                writerList.add(data.getAuthor());
            }
        }
        writerList.sort(Comparator.naturalOrder());
        if (!selectWriter.equals("All")) {
            List<Data> dataListInProcess = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getAuthor().equals(selectWriter)) {
                    dataListInProcess.add(data);
                }
            }
            dataList = dataListInProcess;
        }

        List<String> typeList = new ArrayList<String>();
        for (Data data : dataList) {
            if (!typeList.contains(data.getType())) {
                typeList.add(data.getType());
            }
        }
        typeList.sort(Comparator.naturalOrder());
        if (!selectType.equals("All")) {
            List<Data> dataListInProcess = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getType().equals(selectType)) {
                    dataListInProcess.add(data);
                }
            }
            dataList = dataListInProcess;
        }

        model.addAttribute("newestOldest", newestOldest);
        model.addAttribute("yearList", yearList);
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("resultCount", dataList.size());
        model.addAttribute("result", dataList);
        model.addAttribute("websitelist", websiteList);
        model.addAttribute("selectWebsite", selectWebsite);
        model.addAttribute("writerList", writerList);
        model.addAttribute("selectWriter", selectWriter);
        model.addAttribute("typeList", typeList);
        model.addAttribute("selectType", selectType);

        return "search";
    }

    @PostMapping("/search")
    public String searchPost(@RequestParam(value = "searchKey", required = true) String searchKey,
            @RequestParam(value = "year", defaultValue = "Year") String year,
            @RequestParam(value = "month", defaultValue = "Month") String month,
            @RequestParam(value = "newestOldest", defaultValue = "Newest") String newestOldest,
            @RequestParam(value = "selectWebsite", defaultValue = "All") String selectWebsite,
            @RequestParam(value = "selectWriter", defaultValue = "All") String selectWriter,
            @RequestParam(value = "selectType", defaultValue = "All") String selectType,
            ModelMap model) throws JsonMappingException, JsonProcessingException {

        GetData getData = new GetData();
        List<Data> dataList = new ArrayList<Data>();
        if (searchKey.equals("") || (searchKey.equals("All"))) {
            dataList = getData.getData();
            searchKey = "All";
        } else {
            // Get search results from the API
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(
                    "http://localhost:5000/search?searchkey=" + searchKey + "&year=" + year + "&month=" + month,
                    String.class);
            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result);
            // Create a list of Data objects to store the search results
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

        // Sort the search results based on the user's selection
        if (newestOldest.equals("Oldest")) {
            dataList.sort(Comparator.comparing(Data::getPublishedDate));
        } else {
            dataList.sort(Comparator.comparing(Data::getPublishedDate).reversed());
        }

        List<String> yearList = new ArrayList<String>();
        for (Data data : getData.getData()) {
            if (!yearList.contains(data.getPublishedDate().substring(0, 4))) {
                yearList.add(data.getPublishedDate().substring(0, 4));
                yearList.sort(Comparator.reverseOrder());
            }
        }
        yearList.sort(Comparator.reverseOrder());
        if (yearList.contains(year) == false) {
            year = "Year";
        }
        if (!year.equals("Year")) {
            List<Data> dataListInProcess = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getPublishedDate().substring(0, 4).equals(year)) {
                    dataListInProcess.add(data);
                }
            }
            dataList = dataListInProcess;
        }

        List<String> monthlist = new ArrayList<String>();
        for (Data data : new GetData().getData()) {
            if (!monthlist.contains(data.getPublishedDate().substring(5, 7))) {
                monthlist.add(data.getPublishedDate().substring(5, 7));
                monthlist.sort(Comparator.naturalOrder());
            }
        }
        monthlist.sort(Comparator.naturalOrder());
        if (monthlist.contains(month) == false) {
            month = "Month";
        }
        if (!month.equals("Month")) {
            List<Data> dataListInProcess = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getPublishedDate().substring(5, 7).equals(month)) {
                    dataListInProcess.add(data);
                }
            }
            dataList = dataListInProcess;
        }

        List<String> websitelist = new ArrayList<String>();
        for (Data data : dataList) {
            if (!websitelist.contains(data.getSourceWebsite())) {
                websitelist.add(data.getSourceWebsite());
            }
        }
        websitelist.sort(Comparator.naturalOrder());
        if (websitelist.contains(selectWebsite) == false) {
            selectWebsite = "All";
        }
        if (!selectWebsite.equals("All")) {
            List<Data> dataListInProcess = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getSourceWebsite().equals(selectWebsite)) {
                    dataListInProcess.add(data);
                }
            }
            dataList = dataListInProcess;
        }

        List<String> writerList = new ArrayList<String>();
        for (Data data : dataList) {
            if (!writerList.contains(data.getAuthor())) {
                writerList.add(data.getAuthor());
            }
        }
        writerList.sort(Comparator.naturalOrder());
        if (writerList.contains(selectWriter) == false) {
            selectWriter = "All";
        }
        if (!selectWriter.equals("All")) {
            List<Data> dataListInProcess = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getAuthor().equals(selectWriter)) {
                    dataListInProcess.add(data);
                }
            }
            dataList = dataListInProcess;
        }

        List<String> typeList = new ArrayList<String>();
        for (Data data : dataList) {
            if (!typeList.contains(data.getType())) {
                typeList.add(data.getType());
            }
        }
        typeList.sort(Comparator.naturalOrder());
        if (typeList.contains(selectType) == false) {
            selectType = "All";
        }
        if (!selectType.equals("All")) {
            List<Data> dataListInProcess = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getType().equals(selectType)) {
                    dataListInProcess.add(data);
                }
            }
            dataList = dataListInProcess;
        }

        model.addAttribute("newestOldest", newestOldest);
        model.addAttribute("yearList", yearList);
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("resultCount", dataList.size());
        model.addAttribute("result", dataList);
        model.addAttribute("websitelist", websitelist);
        model.addAttribute("selectWebsite", selectWebsite);
        model.addAttribute("writerList", writerList);
        model.addAttribute("selectWriter", selectWriter);
        model.addAttribute("typeList", typeList);
        model.addAttribute("selectType", selectType);

        return "search";
    }

}