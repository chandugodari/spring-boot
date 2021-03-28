package com.codechandra.jpacurd.service;

import com.codechandra.jpacurd.entity.StockDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CurdService {
    StockDetails saveStockDetails(StockDetails stockDetails);

    List<StockDetails> getAllStockDetails();

    void deleteStock(Long stockId);
}
