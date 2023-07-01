package com.batch.csv.config;

import com.batch.csv.constant.AppConstant;
import com.batch.csv.model.User;
import com.batch.csv.writer.CSVItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.Arrays;

import static com.batch.csv.constant.AppConstant.*;

@Configuration
public class JobConfig {

    private final static String[] header = new String[]{"ID", "First Name", "Last Name", "Email"};

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private CSVItemWriter csvItemWriter;

    @Bean
    public Job csvFileChunkReader() {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(csvChunkStep())
                .build();
    }

    private Step csvChunkStep() {
        return stepBuilderFactory.get(STEP_NAME)
                .<User, User>chunk(CHUNK_SIZE)
                .reader(csvFileItemReader(null))
                .writer(csvItemWriter)
                .build();
    }

    /**
     * To Read CSV file spring batch provides FlatFileItemReader class
     *
     * @param fileSystemResource
     * @return
     */
    @StepScope
    @Bean
    public FlatFileItemReader<User> csvFileItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {

        FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<User>();

		DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<User>();

		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames(header);

		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

		BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<User>();
		fieldSetMapper.setTargetType(User.class);

		defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		//Set source file
        flatFileItemReader.setResource(fileSystemResource);

        //Set mapper
		flatFileItemReader.setLineMapper(defaultLineMapper);

		//Skip header
        flatFileItemReader.setLinesToSkip(1);

        return flatFileItemReader;
    }


}
