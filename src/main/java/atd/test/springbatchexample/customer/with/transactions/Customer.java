package atd.test.springbatchexample.customer.with.transactions;

import lombok.*;

import javax.xml.bind.annotation.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {
    private String firstName;
    private String middleInitial;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zip;

    @XmlElementWrapper(name = "transactions")
    @XmlElement(name = "transaction")
    private List<Transaction> transactions;
}
