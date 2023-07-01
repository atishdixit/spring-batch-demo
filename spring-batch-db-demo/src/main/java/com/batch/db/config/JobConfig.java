package com.batch.db.config;

import com.batch.db.model.User;
import com.batch.db.writer.DBItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

import static com.batch.db.constant.AppConstant.*;

@Configuration
public class JobConfig {

    public static final String QUERY = "select id, first_name as firstName, last_name as lastName,"
            + "email from user";
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DBItemWriter DBItemWriter;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource datasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.userdatasource")
    public DataSource usersDatasource() {
        return DataSourceBuilder.create().build();
    }

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
                .reader(jdbcCursorItemReader())
                .writer(DBItemWriter)
                .build();
    }

    /**
     * To Read DB file spring batch provides JdbcCursorItemReader class
     *
     * @return
     */

    public JdbcCursorItemReader<User> jdbcCursorItemReader() {
        JdbcCursorItemReader<User> jdbcCursorItemReader =
                new JdbcCursorItemReader<User>();

        jdbcCursorItemReader.setDataSource(usersDatasource());
        jdbcCursorItemReader.setSql(
                QUERY);

        jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<User>() {
            {
                setMappedClass(User.class);
            }
        });

        //want to skip from starting or last
        //jdbcCursorItemReader.setCurrentItemCount(2);
        //jdbcCursorItemReader.setMaxItemCount(8);

        return jdbcCursorItemReader;
    }
}
