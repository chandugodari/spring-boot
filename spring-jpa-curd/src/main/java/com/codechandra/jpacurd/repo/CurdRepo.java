package com.codechandra.jpacurd.repo;

import com.codechandra.jpacurd.entity.StockDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurdRepo extends JpaRepository<StockDetails, Long> {
}
