package com.batch.rest.config;

import com.batch.rest.model.User;
import com.batch.rest.service.UserService;
import com.batch.rest.writer.ItemWriterJob;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static com.batch.rest.constant.AppConstant.*;

@Configuration
public class JobConfig {

     @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ItemWriterJob ItemWriterJob;

    @Autowired
    private UserService userService;

    @Bean
    public Job xmlFileChunkReader() {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(xmlChunkStep())
                .build();
    }

    private Step xmlChunkStep() {
        return stepBuilderFactory.get(STEP_NAME)
                .<User, User>chunk(CHUNK_SIZE)
                .reader(itemReaderAdapter())
                .writer(ItemWriterJob)
                .build();
    }


    public ItemReaderAdapter<User> itemReaderAdapter() {
        ItemReaderAdapter<User> itemReaderAdapter = new ItemReaderAdapter<User>();

        itemReaderAdapter.setTargetObject(userService);
        itemReaderAdapter.setTargetMethod("getUser");

        //if uou want pass argument in above method
        //itemReaderAdapter.setArguments(new Object[] {1L, "Test"});

        return itemReaderAdapter;
    }

}
