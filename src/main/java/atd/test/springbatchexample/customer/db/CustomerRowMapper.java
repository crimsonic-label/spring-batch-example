package atd.test.springbatchexample.customer.db;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<CustomerEntity> {
    @Override
    public CustomerEntity mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(resultSet.getLong("id"));
        customer.setAddress(resultSet.getString("address"));
        customer.setCity(resultSet.getString("city"));
        customer.setFirstName(resultSet.getString("first_name"));
        customer.setLastName(resultSet.getString("last_name"));
        customer.setMiddleInitial(resultSet.getString("middle_initial"));
        customer.setState(resultSet.getString("state"));
        customer.setZipCode(resultSet.getString("zip_code"));
        return customer;
    }
}
