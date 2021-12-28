package atd.test.springbatchexample.customer.adapter;

import atd.test.springbatchexample.customer.db.CustomerEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CustomerService {
    private List<CustomerEntity> customers;
    private int curIndex;

    private final String[] firstNames = {"Michael", "Warren", "Ann", "Terrence", "Erica", "Laura", "Steve", "Larry" };
    private final String middleInitials = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String[] lastNames = {"Gates", "Darrow", "Donnelly", "Jobs", "Buffett", "Ellison", "Obama" };
    private final String[] streets = {"4th Street", "Wall Street", "Fifth Avenue", "Mt. Lee Drive", "Jeopardy Lane",
            "Infinite Loop Drive", "Farnam Street", "Isabella Ave", "S. Greenwood Ave" };
    private final String[] cities = {"Chicago", "New York", "Hollywood", "Aurora", "Omaha", "Atherton" };
    private final String[] states = {"IL", "NY", "CA", "NE" };

    private Random generator = new Random();

    public CustomerService() {
        curIndex = 0;
        customers = IntStream.range(0, 100).mapToObj(i -> buildCustomer()).collect(Collectors.toList());
    }

    private CustomerEntity buildCustomer() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId((long) generator.nextInt(Integer.MAX_VALUE));
        customer.setFirstName(firstNames[generator.nextInt(firstNames.length - 1)]);
        customer.setMiddleInitial(String.valueOf(middleInitials.charAt(generator.nextInt(middleInitials.length() - 1))));
        customer.setLastName(lastNames[generator.nextInt(lastNames.length - 1)]);
        customer.setAddress(generator.nextInt(9999) + " " + streets[generator.nextInt(streets.length - 1)]);
        customer.setCity(cities[generator.nextInt(cities.length - 1)]);
        customer.setState(states[generator.nextInt(states.length - 1)]);
        customer.setZipCode(String.valueOf(generator.nextInt(99999)));
        return customer;
    }

    public CustomerEntity getCustomer() {
        if(curIndex < customers.size()) {
            return customers.get(curIndex++);
        }
        return null;
    }
}
