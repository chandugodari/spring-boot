package com.javatech.springbatch.asyncprocessor;

import com.javatech.springbatch.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;


@Configuration
@EnableBatchProcessing
public class AsyncProcessorJobApp {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;



    @Bean
    public Job asyncJob(){
        return this.jobBuilderFactory.get("asynchJob")
                .start(stepOneAsync())
                .listener(new ExecutionTimeJobListner())
                .build();

    }

    @Bean
    public Step stepOneAsync() {

        return this.stepBuilderFactory.get("stepOneAsync")
                .<User, User>chunk(100)
                .reader(userDataReader(null))
                .processor((ItemProcessor) asynItemProcessor())
                .writer(asyncItemWriter())
                .build();
    }

    @Bean
    public ItemProcessor<User,User> processor(){
        return (transaction) -> {
            Thread.sleep(5);
            return transaction;
        };
    }

    @Bean
    public AsyncItemProcessor<User  , User> asynItemProcessor(){
        AsyncItemProcessor<User, User> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(processor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return asyncItemProcessor;
    }

    @Bean
    public AsyncItemWriter<User> asyncItemWriter(){
        AsyncItemWriter<User> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(writer(null));
        return asyncItemWriter;
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
    public ItemWriter<User> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql("INSERT into user(id, name, dept, salary) values(:id, :name,:dept,:salary)")
                .beanMapped()
                .build();
    }

}
