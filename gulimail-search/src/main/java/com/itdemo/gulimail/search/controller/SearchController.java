package com.itdemo.gulimail.search.controller;

import com.itdemo.gulimail.search.service.SearchService;
import com.itdemo.gulimail.search.vo.ProSearchResult;
import com.itdemo.gulimail.search.vo.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/list.html")
    public String toSearch(SearchParam searchParam, Model model, HttpServletRequest httpServletRequest) {
        searchParam.set_querystring(httpServletRequest.getQueryString());
        ProSearchResult result = searchService.searchObj(searchParam);

        model.addAttribute("result",result);
        System.out.println("searchParam------"+searchParam);
        return "list";
    }
}
