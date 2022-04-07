package ch.zumbo.jooqdemo;

import static org.jooq.impl.DSL.*;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.*;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Component
public class DemoService {

    private final DSLContext create;

    DemoService(DataSource dataSource) throws SQLException {
        var connection = dataSource.getConnection();
        create = DSL.using(connection, SQLDialect.H2);
    }

    // https://www.jooq.org/doc/latest/manual/getting-started/use-cases/jooq-as-a-sql-builder-without-codegeneration/
    public String demoCode1() {
        // Fetch a SQL string from a jOOQ Query in order to manually execute it with another tool.
        Query query = create.select(field("BOOK.TITLE"), field("AUTHOR.FIRST_NAME"), field("AUTHOR.LAST_NAME"))
                .from(table("BOOK"))
                .join(table("AUTHOR"))
                .on(field("BOOK.AUTHOR_ID").eq(field("AUTHOR.ID")))
                .where(field("BOOK.PUBLISHED_IN").eq(1948));
        String sql = query.getSQL();
        List<Object> bindValues = query.getBindValues();
        return query.getSQL(ParamType.INLINED);
    }

    public String demoCode() {
// Typesafely execute the SQL statement directly with jOOQ
        var result =
                create.select(field("BOOK.TITLE"), field("AUTHOR.FIRST_NAME"), field("AUTHOR.LAST_NAME"))
                        .from(table("BOOK"))
                        .join(table("AUTHOR"))
                        .on(field("BOOK.AUTHOR_ID").eq(field("AUTHOR.ID")))
                        .where(field("BOOK.PUBLISHED_IN").eq(1948))
                        .fetch();
        return result.get(0).getValue(1, String.class);
    }
}
