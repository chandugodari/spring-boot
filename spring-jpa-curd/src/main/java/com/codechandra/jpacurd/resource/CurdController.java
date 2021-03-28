package com.codechandra.jpacurd.resource;

import com.codechandra.jpacurd.entity.StockDetails;
import com.codechandra.jpacurd.service.CurdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CurdController {

    @Autowired
    private CurdService curdService;

    @PostMapping("/save")
    public ResponseEntity<StockDetails> saveStockDetails(@RequestBody StockDetails stockDetails) {
        StockDetails saveStockDetails = curdService.saveStockDetails(stockDetails);
        return new ResponseEntity<>(saveStockDetails, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<StockDetails> updateStockDetails(@RequestBody StockDetails stockDetails) {
        StockDetails updatedStockDetails = curdService.saveStockDetails(stockDetails);
        return new ResponseEntity<>(updatedStockDetails, HttpStatus.OK);
    }

    @GetMapping("get")
    public ResponseEntity<List<StockDetails>> getStockDetails() {
        List<StockDetails> allStockDetails = curdService.getAllStockDetails();
        return new ResponseEntity<>(allStockDetails, HttpStatus.OK);
    }
    @DeleteMapping("delete/{stockid}")
    public ResponseEntity<Void> deleteStock(@PathVariable("stockid") Long stockId){
        curdService.deleteStock(stockId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
