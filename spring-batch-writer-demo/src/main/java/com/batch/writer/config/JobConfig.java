package com.batch.writer.config;

import com.batch.writer.model.User;
import com.batch.writer.model.UserDto;
import com.batch.writer.processor.ItemProcessorSample;
import com.batch.writer.service.UserService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import static com.batch.writer.constant.AppConstant.*;

@Configuration
public class JobConfig {

     @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemProcessorSample itemProcessorSample;

    @Bean
    public Job xmlFileChunkReader() {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(xmlChunkStep())
                .build();
    }

    private Step xmlChunkStep() {
        return stepBuilderFactory.get(STEP_NAME)
                //.<User, UserDto>chunk(CHUNK_SIZE)
                .<User, User>chunk(CHUNK_SIZE)
                .reader(itemReaderAdapter())
                //.processor(itemProcessorSample)
                //.writer(csvFileItemWriter(null))
                //.writer(jsonFileItemWriter(null))
                //.writer(jsonFileItemWriterWithProcessedData(null))
                //.writer(staxEventItemWriter(null))
                .writer(itemWriterAdapter())
                .build();
    }


    public ItemReaderAdapter<User> itemReaderAdapter() {
        ItemReaderAdapter<User> itemReaderAdapter = new ItemReaderAdapter<User>();

        itemReaderAdapter.setTargetObject(userService);
        itemReaderAdapter.setTargetMethod("getUser");
        return itemReaderAdapter;
    }

    /**
     * Edit run config and pass as program arguments
     * outputFile=E://gitbubrepositories//spring-batch-demo//spring-batch-writer-demo//OutputFiles//users.csv
     * @param fileSystemResource
     * @return
     */
    @StepScope
    @Bean
    public FlatFileItemWriter<User> csvFileItemWriter(
            @Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        FlatFileItemWriter<User> flatFileItemWriter = new FlatFileItemWriter<User>();
        flatFileItemWriter.setResource(fileSystemResource);

        flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("Id,First Name,Last Name,Email");
                //writer.write("Id|First Name|Last Name|Email");
            }
        });
        //Add Delimiter ans sey header values
        flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<User>() {
            {
                //you can override default delimiter
                //setDelimiter("|");
                setFieldExtractor(new BeanWrapperFieldExtractor<User>() {{
                        setNames(new String[] {"id", "firstName", "lastName", "email"});
                    }
                });
            }
        });

        //Set footer optional
        flatFileItemWriter.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("Created @ " + new Date());
            }
        });

        return flatFileItemWriter;
    }


    @StepScope
    @Bean
    public JsonFileItemWriter<User> jsonFileItemWriter(
            @Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        //Required parameter file source and Marshaller
        JsonFileItemWriter<User> jsonFileItemWriter =  new JsonFileItemWriter<>(fileSystemResource,new JacksonJsonObjectMarshaller<User>());
        return jsonFileItemWriter;
    }

    @StepScope
    @Bean
    public JsonFileItemWriter<UserDto> jsonFileItemWriterWithProcessedData(
            @Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        //Required parameter file source and Marshaller
        JsonFileItemWriter<UserDto> jsonFileItemWriter =  new JsonFileItemWriter<>(fileSystemResource,new JacksonJsonObjectMarshaller<UserDto>());
        return jsonFileItemWriter;
    }


    @StepScope
    @Bean
    public StaxEventItemWriter<User> staxEventItemWriter(
            @Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
        StaxEventItemWriter<User> staxEventItemWriter =
                new StaxEventItemWriter<User>();

        staxEventItemWriter.setResource(fileSystemResource);
        staxEventItemWriter.setRootTagName("users");

        staxEventItemWriter.setMarshaller(new Jaxb2Marshaller() {{
                setClassesToBeBound(User.class);
            }
        });

        return staxEventItemWriter;
    }

    public ItemWriterAdapter<User> itemWriterAdapter() {
        ItemWriterAdapter<User> itemWriterAdapter = new ItemWriterAdapter<User>();
        itemWriterAdapter.setTargetObject(userService);
        itemWriterAdapter.setTargetMethod("addNewUser");

        return itemWriterAdapter;
    }
}
