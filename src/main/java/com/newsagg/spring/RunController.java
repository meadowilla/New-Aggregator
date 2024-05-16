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
        List<String> websitelist = new ArrayList<String>();
        for (Data data : getData.getData()) {
            if (!websitelist.contains(data.getSourceWebsite())) {
                websitelist.add(data.getSourceWebsite());
            }
        }
        List<Data> dataForSpecificWebsite = new ArrayList<Data>();
        dataForSpecificWebsite = new GetData().getData();
        dataForSpecificWebsite.sort(Comparator.comparing(Data::getPublishedDate).reversed());
        List<Data> temp = new ArrayList<Data>();
        for (Data data : getData.getData()) {
            if (data.getSourceWebsite().equals("The Block")) {
                temp.add(data);
            }
        }
        dataForSpecificWebsite = temp;


        model.addAttribute("news", getData.getData());
        model.addAttribute("websitelist", websitelist);
        model.addAttribute("selectwebsite", "The Block");
        model.addAttribute("dataForSpecificWebsite", dataForSpecificWebsite);


        return "home";
    }

    @PostMapping("/home")
    public String homePost(@RequestParam(value = "selectwebsite", defaultValue = "The Block") String selectwebsite,
                            ModelMap model) throws JsonMappingException, JsonProcessingException {

        List<Data> dataList = new ArrayList<Data>();
        dataList = new GetData().getData();
        List<Data> dataForSpecificWebsite = new ArrayList<Data>();
        dataForSpecificWebsite = new GetData().getData();
        dataList.sort(Comparator.comparing(Data::getPublishedDate).reversed());
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
            dataForSpecificWebsite = temp;
        }

        model.addAttribute("news", dataList);
        model.addAttribute("websitelist", websitelist);
        model.addAttribute("dataForSpecificWebsite", dataForSpecificWebsite);
        model.addAttribute("selectwebsite", selectwebsite);
        return "home";
    }


    @GetMapping("/search")
    public String searchPage(@RequestParam(value="searchkey", required = false) String searchkey,
                                @RequestParam(value = "year", defaultValue = "Year") String year,
                                @RequestParam(value = "month", defaultValue = "Month") String month,
                                @RequestParam(value = "newestoldest", defaultValue = "Newest") String newestoldest,
                                @RequestParam(value = "selectwebsite", defaultValue = "All") String selectwebsite,
                                @RequestParam(value = "selectwriter", defaultValue = "All") String selectwriter,
                                @RequestParam(value = "selecttype", defaultValue = "All") String selecttype,
                                ModelMap model) throws JsonMappingException, JsonProcessingException {

    // If the search key is empty, display all result in database
    // Otherwise, get search results from the API
        GetData getData = new GetData();
        List<Data> dataList = new ArrayList<Data>();
        if (searchkey.equals("")) {
            dataList = getData.getData();
            searchkey = "All";
        }
        else {
            // Get search results from the API
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject("http://localhost:5000/search?searchkey=" + searchkey + "&year=" + year + "&month=" + month, String.class);
            // Parse the JSON response 
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result);
            // Create a list of Data objects to store the search results
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
        }  
        // Sort the search results based on the user's selection
        if (newestoldest.equals("Oldest")) {
            dataList.sort(Comparator.comparing(Data::getPublishedDate));
        } else {
            dataList.sort(Comparator.comparing(Data::getPublishedDate).reversed());
        }
        // Get the list of years for the dropdown menu
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
        websitelist.sort(Comparator.naturalOrder());
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
        writerlist.sort(Comparator.naturalOrder());
        if (!selectwriter.equals("All")) {
            List<Data> temp = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getAuthor().equals(selectwriter)) {
                    temp.add(data);
                }
            }
            dataList = temp;
        }

        List<String> typelist = new ArrayList<String>();
        for (Data data : dataList) {
            if (!typelist.contains(data.getType())) {
                typelist.add(data.getType());
            }
        }
        typelist.sort(Comparator.naturalOrder());
        if (!selecttype.equals("All")) {
            List<Data> temp = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getType().equals(selecttype)) {
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
        model.addAttribute("typelist", typelist);
        model.addAttribute("selecttype", selecttype);

        return "search";
    }    
    @PostMapping("/search")
    public String search(@RequestParam(value="searchkey", required = true) String searchkey,
                                @RequestParam(value = "year", defaultValue = "Year") String year,
                                @RequestParam(value = "month", defaultValue = "Month") String month,
                                @RequestParam(value = "newestoldest", defaultValue = "Newest") String newestoldest,
                                @RequestParam(value = "selectwebsite", defaultValue = "All") String selectwebsite,
                                @RequestParam(value = "selectwriter", defaultValue = "All") String selectwriter,
                                @RequestParam(value = "selecttype", defaultValue = "All") String selecttype,
                                ModelMap model) throws JsonMappingException, JsonProcessingException {

        GetData getData = new GetData();
        List<Data> dataList = new ArrayList<Data>();
        if (searchkey.equals("") || (searchkey.equals("All")))  {
            dataList = getData.getData();
            searchkey = "All";
        }
        else {
            // Get search results from the API
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject("http://localhost:5000/search?searchkey=" + searchkey + "&year=" + year + "&month=" + month, String.class);
            // Parse the JSON response 
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result);
            // Create a list of Data objects to store the search results
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
        }

        // Sort the search results based on the user's selection
        if (newestoldest.equals("Oldest")) {
            dataList.sort(Comparator.comparing(Data::getPublishedDate));
        } else {
            dataList.sort(Comparator.comparing(Data::getPublishedDate).reversed());
        }
        

        List<String> yearlist = new ArrayList<String>();
        for (Data data : getData.getData()) {
            if (!yearlist.contains(data.getPublishedDate().substring(0, 4))) {
                yearlist.add(data.getPublishedDate().substring(0, 4));
                yearlist.sort(Comparator.reverseOrder());
            }
        }
        yearlist.sort(Comparator.reverseOrder());
        if(yearlist.contains(year) == false){
            year = "Year";
        }
        if (!year.equals("Year")) {
            List<Data> temp = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getPublishedDate().substring(0, 4).equals(year)) {
                    temp.add(data);
                }
            }
            dataList = temp;
        }

        List<String> monthlist = new ArrayList<String>();
        for (Data data : new GetData().getData()) {
            if (!monthlist.contains(data.getPublishedDate().substring(5, 7))) {
                monthlist.add(data.getPublishedDate().substring(5, 7));
                monthlist.sort(Comparator.naturalOrder());
            }
        }
        monthlist.sort(Comparator.naturalOrder());
        if(monthlist.contains(month) == false){
            month = "Month";
        }
        if (!month.equals("Month")) {
            List<Data> temp = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getPublishedDate().substring(5, 7).equals(month)) {
                    temp.add(data);
                }
            }
            dataList = temp;
        }
        

        List<String> websitelist = new ArrayList<String>();
        for (Data data : dataList) {
            if (!websitelist.contains(data.getSourceWebsite())) {
                websitelist.add(data.getSourceWebsite());
            }
        }
        websitelist.sort(Comparator.naturalOrder());
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
        writerlist.sort(Comparator.naturalOrder());
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

        List<String> typelist = new ArrayList<String>();
        for (Data data : dataList) {
            if (!typelist.contains(data.getType())) {
                typelist.add(data.getType());
            }
        }
        typelist.sort(Comparator.naturalOrder());
        if(typelist.contains(selecttype) == false){
            selecttype = "All";
        }
        if (!selecttype.equals("All")) {
            List<Data> temp = new ArrayList<Data>();
            for (Data data : dataList) {
                if (data.getType().equals(selecttype)) {
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
        model.addAttribute("typelist", typelist);
        model.addAttribute("selecttype", selecttype);

        return "search";
    }    

}