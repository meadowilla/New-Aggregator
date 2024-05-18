package com.newsagg.spring;

import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class RunController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/home")
    public String home(ModelMap model) {
        List<Data> allData = newsService.getAllData();
        List<String> websiteList = newsService.getWebsiteList(allData);
        List<Data> dataForSpecificWebsite = newsService.getDataForSpecificWebsite("The Block", allData);
        dataForSpecificWebsite.sort(Comparator.comparing(Data::getPublishedDate).reversed());

        model.addAttribute("news", allData);
        model.addAttribute("websiteList", websiteList);
        model.addAttribute("selectWebsite", "The Block");
        model.addAttribute("dataForSpecificWebsite", dataForSpecificWebsite);

        return "home";
    }

    @PostMapping("/home")
    public String homePost(@RequestParam(value = "selectWebsite", defaultValue = "The Block") String selectWebsite,
                           ModelMap model) throws JsonMappingException, JsonProcessingException {
        List<Data> allData = newsService.getAllData();
        allData.sort(Comparator.comparing(Data::getPublishedDate).reversed());
        List<Data> dataForSpecificWebsite = selectWebsite.equals("All") ? allData : newsService.getDataForSpecificWebsite(selectWebsite, allData);
        List<String> websiteList = newsService.getWebsiteList(allData);

        model.addAttribute("news", allData);
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

        List<Data> allData = newsService.getAllData();
        List<Data> searchResults = searchKey == null || searchKey.isEmpty() ? allData : newsService.getSearchResults(searchKey, year, month);
        searchResults = newestOldest.equals("Oldest") ? searchResults.stream().sorted(Comparator.comparing(Data::getPublishedDate)).toList() : searchResults.stream().sorted(Comparator.comparing(Data::getPublishedDate).reversed()).toList();

        if (!year.equals("Year")) {
            searchResults = newsService.filterDataByAttribute(searchResults, "year", year);
        }
        if (!month.equals("Month")) {
            searchResults = newsService.filterDataByAttribute(searchResults, "month", month);
        }
        if (!selectWebsite.equals("All")) {
            searchResults = newsService.filterDataByAttribute(searchResults, "website", selectWebsite);
        }
        if (!selectWriter.equals("All")) {
            searchResults = newsService.filterDataByAttribute(searchResults, "author", selectWriter);
        }
        if (!selectType.equals("All")) {
            searchResults = newsService.filterDataByAttribute(searchResults, "type", selectType);
        }

        model.addAttribute("newestOldest", newestOldest);
        model.addAttribute("yearList", newsService.getDistinctYears(allData));
        model.addAttribute("searchKey", searchKey);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("resultCount", searchResults.size());
        model.addAttribute("result", searchResults);
        model.addAttribute("websitelist", newsService.getWebsiteList(searchResults));
        model.addAttribute("selectWebsite", selectWebsite);
        model.addAttribute("writerList", newsService.getDistinctAuthors(searchResults));
        model.addAttribute("selectWriter", selectWriter);
        model.addAttribute("typeList", newsService.getDistinctTypes(searchResults));
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

        return search(searchKey, year, month, newestOldest, selectWebsite, selectWriter, selectType, model);
    }
}
