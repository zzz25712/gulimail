package com.itdemo.gulimail.search.service;

import com.itdemo.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    boolean saveIndex(List<SkuEsModel> skuEsModels) throws IOException;
}
