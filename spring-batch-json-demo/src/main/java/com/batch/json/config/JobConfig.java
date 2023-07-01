package com.batch.json.config;

import com.batch.json.model.User;
import com.batch.json.writer.JSONItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import static com.batch.json.constant.AppConstant.*;

@Configuration
public class JobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JSONItemWriter JSONItemWriter;

    @Bean
    public Job jsonFileChunkReader() {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(jsonChunkStep())
                .build();
    }

    private Step jsonChunkStep() {
        return stepBuilderFactory.get(STEP_NAME)
                .<User, User>chunk(CHUNK_SIZE)
                .reader(jsonFileItemReader(null))
                .writer(JSONItemWriter)
                .build();
    }

    /**
     * To Read JSON file spring batch provides JsonItemReader class
     *
     * @param fileSystemResource
     * @return
     */
    @StepScope
    @Bean
    public JsonItemReader<User> jsonFileItemReader(
            @Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        JsonItemReader<User> jsonItemReader =  new JsonItemReader<User>();

        jsonItemReader.setResource(fileSystemResource);
        jsonItemReader.setJsonObjectReader(
                new JacksonJsonObjectReader<>(User.class));
        return jsonItemReader;
    }
}
