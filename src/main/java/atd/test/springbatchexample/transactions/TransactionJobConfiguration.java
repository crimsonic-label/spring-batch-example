package atd.test.springbatchexample.transactions;

import atd.test.springbatchexample.transactions.db.AccountSummary;
import atd.test.springbatchexample.transactions.db.TransactionRepository;
import atd.test.springbatchexample.utils.DailyJobTimestamper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@Slf4j
public class TransactionJobConfiguration {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * reader for csv file
     *
     * @param inputFile the file to be read
     * @return the file set reader
     */
    @Bean
    @StepScope
    public FlatFileItemReader<FieldSet> fileItemReader(@Value("#{jobParameters['transactionFile']}") String inputFile) {
        return new FlatFileItemReaderBuilder<FieldSet>()
                .name("fileItemReader")
                .resource(new ClassPathResource(inputFile))
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PassThroughFieldSetMapper())
                .build();
    }

    /**
     * the transaction reader - flat file reader with validation
     *
     * @return the reader
     */
    @Bean
    @StepScope
    public TransactionReader transactionReader() {
        return new TransactionReader(fileItemReader(null));
    }

    /**
     * database writer to store transactions in database
     *
     * @param dataSource data source
     * @return item writer
     */
    @Bean
    public JdbcBatchItemWriter<TransactionDto> transactionWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<TransactionDto>()
                .dataSource(dataSource)
                .sql("insert into transaction (id, timestamp, amount, account_id) values ((values next value for transaction_id_seq), :timestamp, :amount, (select id from account_summary where number = :accountNumber))")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider())
                .build();
    }

    /**
     * step configuration for reading csv file
     *
     * @return the step
     */
    @Bean
    public Step importTransactionFileStep() {
        return stepBuilderFactory.get("importTransactionFileStep")
                .<TransactionDto, TransactionDto>chunk(100)
                .reader(transactionReader())
                .writer(transactionWriter(null))
                .allowStartIfComplete(true)
                .listener(transactionReader())
                .build();
    }

    /**
     * define JdbcCursorItemReader to read account summary records from db
     *
     * @param dataSource database data source
     * @return the reader
     * StepScope annotation because we need new instances for two steps
     */
    @Bean
    @StepScope
    public JdbcCursorItemReader<AccountSummary> accountSummaryReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<AccountSummary>()
                .name("accountSummaryReader")
                .dataSource(dataSource)
                .sql("SELECT NUMBER, CURRENT_BALANCE FROM ACCOUNT_SUMMARY A " +
                        "WHERE A.ID IN (" +
                        "SELECT DISTINCT T.ACCOUNT_ID FROM TRANSACTION T" +
                        ") " +
                        "ORDER BY A.NUMBER")
                .rowMapper((resultSet, rowNumber) ->
                        AccountSummary.builder()
                                .number(resultSet.getString("number"))
                                .currentBalance(resultSet.getBigDecimal("current_balance"))
                                .build()
                ).build();
    }

    /**
     * processor that applies transactions to accounts balance
     *
     * @return the processor
     */
    @Bean
    public TransactionApplierProcessor transactionApplierProcessor() {
        return new TransactionApplierProcessor(transactionRepository);
    }

    /**
     * writer to update the account ballance to database
     *
     * @param dataSource database data source
     * @return the writer
     */
    @Bean
    public JdbcBatchItemWriter<AccountSummary> accountSummaryJdbcBatchItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<AccountSummary>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("UPDATE ACCOUNT_SUMMARY " +
                        "SET CURRENT_BALANCE = :currentBalance " +
                        "WHERE number = :number")
                .build();
    }

    /**
     * step configuration
     *
     * @return apply transactions step
     */
    @Bean
    public Step applyTransactionsStep() {
        return stepBuilderFactory.get("applyTransactionsStep")
                // chunk size of 100 records
                .<AccountSummary, AccountSummary>chunk(100)
                .reader(accountSummaryReader(null))
                .processor(transactionApplierProcessor())
                .writer(accountSummaryJdbcBatchItemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<AccountSummary> accountSummaryFileWriter(@Value("#{jobParameters['summaryFile']}")
            Resource summaryFile) throws IOException {
        BeanWrapperFieldExtractor<AccountSummary> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"number", "currentBalance"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<AccountSummary> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setFieldExtractor(fieldExtractor);

        log.debug("creating flat file writer for {} file", summaryFile.getFile().getAbsolutePath());
        return new FlatFileItemWriterBuilder<AccountSummary>()
                .name("accountSummaryFileWriter")
                .resource(summaryFile)
                .lineAggregator(lineAggregator)
                .build();
    }

    /**
     * reuse the item reader
     *
     * @return the step
     */
    @Bean
    public Step generateAccountSummaryStep() throws IOException {
        return stepBuilderFactory.get("generateAccountSummaryStep")
                .<AccountSummary, AccountSummary>chunk(100)
                // reuse the item reader - read account summaries
                .reader(accountSummaryReader(null))
                .writer(accountSummaryFileWriter(null))
                .build();
    }

    /**
     * job contains 3 steps
     *
     * @return the job
     */
    @Bean
    public Job transactionJob() throws IOException {
        return jobBuilderFactory.get("transactionJob")
                // add timestamp parameter to enable restart
                .incrementer(new DailyJobTimestamper())
                // 1. read transaction csv file
                .start(importTransactionFileStep())

                // 2. on all other conditions do to apply transaction step
                .next(applyTransactionsStep())

                // 3. generate new csv with account summary
                .next(generateAccountSummaryStep())
                .build();
    }
}
