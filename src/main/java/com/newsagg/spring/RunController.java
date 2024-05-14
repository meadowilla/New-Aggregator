package com.newsagg.spring;

import java.util.ArrayList;
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
    @GetMapping("/test")
    public String test(ModelMap model) {
        GetData gd = new GetData();
        model.addAttribute("news", gd.getData());
        return "test";
    }
    
    @GetMapping("/hello")
    public String hello(ModelMap model) {
        GetData gd = new GetData();
        model.addAttribute("news", gd.getData());
        return "index";
    }
    @GetMapping("/home")
    public String showSearchForm(ModelMap model) {
    GetData getData = new GetData();
    model.addAttribute("news", getData.getData());
        return "search";
    }

    @PostMapping("/home")
    public String performSearch(@RequestParam("searchkey") String searchkey,
                                @RequestParam("year") String year,
                                @RequestParam("month") String month,
                                ModelMap model) throws JsonMappingException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:5000/search?searchkey=" + searchkey + "&year=" + year + "&month=" + month, String.class);
        if (result == null) {
            int resultCount = 0;
            model.addAttribute("resultCount", resultCount);
            return "search";
        }
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
        model.addAttribute("resultCount", dataList.size());
        model.addAttribute("result", dataList);
        return "search";

    }
    
}