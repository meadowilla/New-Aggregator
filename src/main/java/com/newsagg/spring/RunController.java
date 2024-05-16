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
        public String showSearchForm(ModelMap model) {
        GetData getData = new GetData();
        model.addAttribute("news", getData.getData());
            return "home";
        }

    @GetMapping("/search")
    public String searchPage(@RequestParam(value="searchkey", required = false) String searchkey,
                                @RequestParam(value = "year", defaultValue = "Year") String year,
                                @RequestParam(value = "month", defaultValue = "Month") String month,
                                @RequestParam(value = "newestoldest", defaultValue = "Newest") String newestoldest,
                                @RequestParam(value = "selectwebsite", defaultValue = "All") String selectwebsite,
                                @RequestParam(value = "selectwriter", defaultValue = "All") String selectwriter,
                                ModelMap model) throws JsonMappingException, JsonProcessingException {
         
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:5000/search?searchkey=" + searchkey + "&year=" + year + "&month=" + month, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);
        
        List<Data> dataList = new ArrayList<Data>();
        for (JsonNode node : jsonNode) {
                Data data = new Data();
                data.setLink(node.get(0).asText() );
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
        if (newestoldest.equals("Oldest")) {
            dataList.sort(Comparator.comparing(Data::getPublishedDate));
        } else {
            dataList.sort(Comparator.comparing(Data::getPublishedDate).reversed());
        }

        GetData getData = new GetData();
        List<String> yearlist = new ArrayList<String>();
        for (Data data : getData.getData()) {
            if (!yearlist.contains(data.getPublishedDate().substring(0, 4))) {
                yearlist.add(data.getPublishedDate().substring(0, 4));
                yearlist.sort(Comparator.reverseOrder());
            }
        }

        
        List<String> websitelist = new ArrayList<String>();
        for (Data data : dataList) {
            if (!websitelist.contains(data.getSourceWebsite())) {
                websitelist.add(data.getSourceWebsite());
            }
        }
        if (!selectwebsite.equals("All")) {
            List<Data> temp = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getSourceWebsite().equals(selectwebsite)) {
                    temp.add(data);
                }
            }
            dataList = temp;
        }

        List<String> writerlist = new ArrayList<String>();
        for (Data data : getData.getData()) {
            if (!writerlist.contains(data.getAuthor())) {
                writerlist.add(data.getAuthor());
            }
        }
        if (!selectwriter.equals("All")) {
            List<Data> temp = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getAuthor().equals(selectwriter)) {
                    temp.add(data);
                }
            }
            dataList = temp;
        }


        model.addAttribute("newestoldest", newestoldest);
        model.addAttribute("yearlist", yearlist);
        model.addAttribute("searchkey", searchkey);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("resultCount", dataList.size());
        model.addAttribute("result", dataList);
        model.addAttribute("websitelist", websitelist);
        model.addAttribute("selectwebsite", selectwebsite);
        model.addAttribute("writerlist", writerlist);
        model.addAttribute("selectwriter", selectwriter);

        return "search";
    }    
    @PostMapping("/search")
    public String search(@RequestParam(value="searchkey", required = true) String searchkey,
                                @RequestParam(value = "year", defaultValue = "Year") String year,
                                @RequestParam(value = "month", defaultValue = "Month") String month,
                                @RequestParam(value = "newestoldest", defaultValue = "Newest") String newestoldest,
                                @RequestParam(value = "selectwebsite", defaultValue = "All") String selectwebsite,
                                @RequestParam(value = "selectwriter", defaultValue = "All") String selectwriter,
                                ModelMap model) throws JsonMappingException, JsonProcessingException {
        // Get search results from the API
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:5000/search?searchkey=" + searchkey + "&year=" + year + "&month=" + month, String.class);
        // Parse the JSON response 
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);
        // Create a list of Data objects to store the search results
        List<Data> dataList = new ArrayList<Data>();
        for (JsonNode node : jsonNode) {
                Data data = new Data();
                data.setLink(node.get(0).asText() );
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
        // Sort the search results based on the user's selection
        if (newestoldest.equals("Oldest")) {
            dataList.sort(Comparator.comparing(Data::getPublishedDate));
        } else {
            dataList.sort(Comparator.comparing(Data::getPublishedDate).reversed());
        }
        
        GetData getData = new GetData();

        List<String> yearlist = new ArrayList<String>();
        for (Data data : getData.getData()) {
            if (!yearlist.contains(data.getPublishedDate().substring(0, 4))) {
                yearlist.add(data.getPublishedDate().substring(0, 4));
                yearlist.sort(Comparator.reverseOrder());
            }
        }

        List<String> websitelist = new ArrayList<String>();
        for (Data data : dataList) {
            if (!websitelist.contains(data.getSourceWebsite())) {
                websitelist.add(data.getSourceWebsite());
            }
        }
        if(websitelist.contains(selectwebsite) == false){
            selectwebsite = "All";
        }

        if (!selectwebsite.equals("All")) {
            List<Data> temp = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getSourceWebsite().equals(selectwebsite)) {
                    temp.add(data);
                }
            }
            dataList = temp;
        }

        List<String> writerlist = new ArrayList<String>();
        for (Data data : dataList) {
            if (!writerlist.contains(data.getAuthor())) {
                writerlist.add(data.getAuthor());
            }
        }
        if(writerlist.contains(selectwriter) == false){
            selectwriter = "All";
        }
        if (!selectwriter.equals("All")) {
            List<Data> temp = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getAuthor().equals(selectwriter)) {
                    temp.add(data);
                }
            }
            dataList = temp;
        }
        
        model.addAttribute("newestoldest", newestoldest);
        model.addAttribute("yearlist", yearlist);
        model.addAttribute("searchkey", searchkey);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("resultCount", dataList.size());
        model.addAttribute("result", dataList);
        model.addAttribute("websitelist", websitelist);
        model.addAttribute("selectwebsite", selectwebsite);
        model.addAttribute("writerlist", writerlist);
        model.addAttribute("selectwriter", selectwriter);

        return "search";
    }    

}