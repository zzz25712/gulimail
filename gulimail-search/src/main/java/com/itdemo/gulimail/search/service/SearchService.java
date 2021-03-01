package com.itdemo.gulimail.search.service;

import com.itdemo.gulimail.search.vo.ProSearchResult;
import com.itdemo.gulimail.search.vo.SearchParam;

public interface SearchService {
    ProSearchResult searchObj(SearchParam searchParam);
}
