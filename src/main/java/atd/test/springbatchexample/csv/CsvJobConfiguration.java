package atd.test.springbatchexample.csv;

import atd.test.springbatchexample.utils.DailyJobTimestamper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
public class CsvJobConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * Job to read csv file
     *
     * @return the job
     */
    @Bean(name = "csvJob")
    public Job csvJob() {
        return jobBuilderFactory.get("csvJob")
                // incrementer to update run.id parameter,
                //  parameters should be set before applying to a job
                .incrementer(new DailyJobTimestamper())
                .start(csvStep())
                .build();
    }

    /**
     * explorer step ro diplay jobs information
     *
     * @return the step
     */
    @Bean
    public Step csvStep() {
        return stepBuilderFactory.get("csvReaderStep")
                .<FieldSet, FieldSet>chunk(100)
                .reader(csvFileReader(null))
                .writer(fieldSetWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<? super FieldSet> fieldSetWriter() {
        return (ItemWriter<FieldSet>) list -> list
                .forEach(fieldSet -> {
                    log.info("row:");
                    Arrays.stream(fieldSet.getValues()).forEach(value -> log.info("\tvalue: " + value));
                });
    }

    @Bean
    @StepScope
    public FlatFileItemReader<FieldSet> csvFileReader(@Value("#{jobParameters['transactionFile']}") String inputFile) {
        return new FlatFileItemReaderBuilder<FieldSet>()
                .name("fileItemReader")
                .resource(new ClassPathResource(inputFile))
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PassThroughFieldSetMapper())
                .build();
    }
}
