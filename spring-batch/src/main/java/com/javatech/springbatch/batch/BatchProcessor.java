package com.javatech.springbatch.batch;

import com.javatech.springbatch.entity.User;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BatchProcessor implements ItemProcessor<User, User> {

    private static final Map<String, String> DEPT_NAME = new HashMap<>();

    public BatchProcessor() {
        DEPT_NAME.put("001", "JAVA Technology");
        DEPT_NAME.put("002", ".Net technology");
        DEPT_NAME.put("003", "Python Technology");
    }

    @Override
    public User process(User user) throws Exception {
        String deptCode = user.getDept();
        String deptDesc = DEPT_NAME.get(deptCode);
        user.setDept(deptDesc);
        return user;
    }
}
