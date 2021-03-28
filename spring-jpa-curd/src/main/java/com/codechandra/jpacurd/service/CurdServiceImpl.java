package com.codechandra.jpacurd.service;

import com.codechandra.jpacurd.entity.StockDetails;
import com.codechandra.jpacurd.repo.CurdRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurdServiceImpl implements CurdService {

    @Autowired
    CurdRepo curdRepo;

    @Override
    public StockDetails saveStockDetails(StockDetails stockDetails) {
        return curdRepo.save(stockDetails);

    }

    @Override
    public List<StockDetails> getAllStockDetails() {
        return curdRepo.findAll();
    }

    @Override
    public void deleteStock(Long stockId) {
        curdRepo.deleteById(stockId);
    }
}
