package atd.test.springbatchexample.transactions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.transform.FieldSet;

/**
 * Customized reader for csv file
 * <p>
 * ExitStatus of the step is tied to the state of the reader
 * It counts read records.
 * If we do not read the same number of rows as specified in footer line, we do not continue
 * <p>
 * wrap a FlatFileItemReader with our custom ItemReader
 */

@Slf4j
public class TransactionReader implements ItemStreamReader<TransactionDto> {

    // delegate to FlatFileItemReader, it returns FieldSet because we have two record formats
    private final ItemStreamReader<FieldSet> fieldSetReader;
    private StepExecution stepExecution;

    // counting items  read
    private int recordCount = 0;
    private int expectedRecordCount = 0;

    public TransactionReader(ItemStreamReader<FieldSet> fieldSetReader) {
        this.fieldSetReader = fieldSetReader;
    }

    /**
     * @return returns TransactionDto or null when everything is read
     * @throws Exception                     from fieldSetReader.read
     * @throws UnexpectedInputException      from fieldSetReader.read
     * @throws ParseException                from fieldSetReader.read
     * @throws NonTransientResourceException from fieldSetReader.read
     */
    @Override public TransactionDto read()
            throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // to test exception handling
        if(this.recordCount == 25) {
            throw new ParseException("This isn't what I hoped to happen");
        }
        // delegate to the field set reader
        return process(fieldSetReader.read());
    }

    /**
     * transform field set to transaction dto, or store footer record number
     *
     * @param fieldSet input field set
     * @return read transaction dto
     */
    private TransactionDto process(FieldSet fieldSet) {
        log.debug("processing transaction: {}", fieldSet.toString());
        if (fieldSet != null) {
            if (fieldSet.getFieldCount() > 1) {
                // line with transaction
                recordCount++;
                return TransactionDto.builder()
                        .accountNumber(fieldSet.readString(0))
                        .timestamp(fieldSet.readDate(1, "yyyy-MM-dd HH:mm:ss"))
                        .amount(fieldSet.readBigDecimal(2))
                        .build();
            } else {
                // footer, summary row with the only field: expected records number
                expectedRecordCount = fieldSet.readInt(0);
                if (expectedRecordCount != recordCount) {
                    // sets a flag that tells Spring Batch to end after the step is complete
                    stepExecution.setTerminateOnly();
                }
            }
        }
        return null;
    }

    /**
     * assign step execution property used in process method for terminating the step
     *
     * @param execution the execution
     */
    @BeforeStep
    public void beforeStep(StepExecution execution) {
        this.stepExecution = execution;
    }

    @Override public void open(ExecutionContext executionContext) throws ItemStreamException {
        fieldSetReader.open(executionContext);
    }

    @Override public void update(ExecutionContext executionContext) throws ItemStreamException {
        fieldSetReader.update(executionContext);
    }

    @Override public void close() throws ItemStreamException {
        fieldSetReader.close();
    }
}
