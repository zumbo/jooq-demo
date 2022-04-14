package ch.zumbo.jooqdemo;

import static org.jooq.impl.DSL.*;
import org.jooq.*;
import org.jooq.impl.*;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DemoService {

    private final DSLContext create;
    private final Connection connection;

    DemoService(DataSource dataSource) throws SQLException {
        connection = dataSource.getConnection();
        create = DSL.using(connection, SQLDialect.H2);
    }

    public String demoCode() throws SQLException {
        return "Hier ist die Spielwiese für euren jOOQ-Code!";
    }
}
