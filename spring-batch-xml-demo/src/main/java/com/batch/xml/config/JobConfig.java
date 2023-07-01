package com.batch.xml.config;

import com.batch.xml.model.User;
import com.batch.xml.writer.XMLItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import static com.batch.xml.constant.AppConstant.*;

@Configuration
public class JobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private XMLItemWriter XMLItemWriter;

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
                .reader(xmlFileItemReader(null))
                .writer(XMLItemWriter)
                .build();
    }

    /**
     * To Read JSON file spring batch provides StaxEventItemReader class
     *
     * @param fileSystemResource
     * @return
     */
    @StepScope
    @Bean
    public StaxEventItemReader<User> xmlFileItemReader(
            @Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        StaxEventItemReader<User> staxEventItemReader = new StaxEventItemReader<User>();

        staxEventItemReader.setResource(fileSystemResource);
        staxEventItemReader.setFragmentRootElementName(ROOT_TAG_NAME);
        staxEventItemReader.setUnmarshaller(new Jaxb2Marshaller() {
            {
                setClassesToBeBound(User.class);
            }
        });

        return staxEventItemReader;
    }
}
