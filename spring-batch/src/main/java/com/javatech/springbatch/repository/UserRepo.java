package com.javatech.springbatch.repository;


import com.javatech.springbatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
}
