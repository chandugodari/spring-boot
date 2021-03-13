package com.javatech.springbatch.parallel;

import com.javatech.springbatch.entity.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
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
public class ParallelStepJobApp {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job parallelStepJob(){

        Flow secondFlow = new FlowBuilder<Flow>("secondFlow")
                .start(stepTwo())
                .build();

        Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
                .start(stepOne())
                .split(new SimpleAsyncTaskExecutor())
                .add(secondFlow)
                .build();

        return this.jobBuilderFactory.get("ParallelStepJob")
                .start(parallelFlow)
                .end()
                .build();
    }

//    @Bean
//    public Job sequentialStepJob(){
//        return this.jobBuilderFactory.get("sequentialStepJob")
//                .start(stepOne())
//                .next(stepTwo())
//                .build();
//    }


    @Bean
    public Step stepOne(){
        return this.stepBuilderFactory.get("stepOne")
                .<User, User>chunk(100)
                .reader(userDataReader(null))
                .writer(writer(null))
                .build();
    }

    public Step stepTwo(){
        return this.stepBuilderFactory.get("stepTwo")
                .<User, User>chunk(100)
                .reader(userDataReaderTwo(null))
                .writer(writer(null))
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
    public FlatFileItemReader<User> userDataReaderTwo(@Value("${inputFileTwo}") Resource resource) {
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

}
