package com.javatech.springbatch.multithread;

import com.javatech.springbatch.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class MultiThreadJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multithreadReaderJob() {
        return this.jobBuilderFactory.get("multithreadedJob")
                .start(stepOne())
                .build();
    }

    @Bean
    public Step stepOne() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.afterPropertiesSet();
        return this.stepBuilderFactory.get("stepOne")
                .<User, User>chunk(100)
                .reader(userDataReader(null))
                .writer(writer(null))
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<User> userDataReader(@Value("${inputFile}") Resource resource) {
        return new FlatFileItemReaderBuilder<User>()
                .name("csv-reader")
                .linesToSkip(1)
                .resource(resource)
                .delimited()
                .names(new String[]{"id", "name", "dept", "salary"})
                .fieldSetMapper(fieldSet -> {
                    User user = new User();
                    user.setId(fieldSet.readInt("id"));
                    user.setName(fieldSet.readString("name"));
                    user.setDept(fieldSet.readString("dept"));
                    user.setSalary(fieldSet.readInt("salary"));
                    return user;
                }).build();

    }

    @Bean
    @StepScope
    public ItemWriter<? super User> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql("INSERT into user(id, name, dept, salary) values(:id, :name,:dept,:salary)")
                .beanMapped()
                .build();
    }

    public static void main(String[] args) {
        String[] newArgs = new String[]{"inputFlatFile =/data/csv/transactions.csv"};
        SpringApplication.run(MultiThreadJob.class, newArgs);
    }
}
