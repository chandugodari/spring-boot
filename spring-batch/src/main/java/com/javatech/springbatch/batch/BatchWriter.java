package com.javatech.springbatch.batch;


import com.javatech.springbatch.entity.User;
import com.javatech.springbatch.repository.UserRepo;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatchWriter implements ItemWriter<User> {

    @Autowired
    private UserRepo userRepo;

    @Override
    public void write(List<? extends User> users) throws Exception {
        System.out.println("Data Save for users" + users);
        userRepo.saveAll(users);

    }
}
