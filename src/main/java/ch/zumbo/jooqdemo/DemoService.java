package ch.zumbo.jooqdemo;

import static ch.zumbo.jooqdemo.Tables.AUTHOR;
import static ch.zumbo.jooqdemo.Tables.BOOK;
import static org.jooq.impl.DSL.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.conf.ParamType;
import org.jooq.impl.*;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DemoService {

    private final DSLContext create;
    private final Connection connection;

    DemoService(DataSource dataSource) throws SQLException {
        connection = dataSource.getConnection();
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

    // https://www.jooq.org/doc/latest/manual/getting-started/use-cases/jooq-as-a-sql-builder-with-code-generation/
    public String demoCode2() {
        // Fetch a SQL string from a jOOQ Query in order to manually execute it with another tool.
        Query query = create.select(BOOK.TITLE, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .from(BOOK)
                .join(AUTHOR)
                .on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .where(BOOK.PUBLISHED_IN.eq(1948));

        String sql = query.getSQL();
        List<Object> bindValues = query.getBindValues();
        return query.getSQL(ParamType.INLINED);
    }

    // https://www.jooq.org/doc/latest/manual/getting-started/use-cases/jooq-as-a-sql-executor/
    public String demoCode3a() {
        // Typesafely execute the SQL statement directly with jOOQ
        Result<Record3<String, String, String>> result =
                create.select(BOOK.TITLE, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                        .from(BOOK)
                        .join(AUTHOR)
                        .on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                        .where(BOOK.PUBLISHED_IN.eq(1948))
                        .fetch();
        return result.get(0).getValue(1, String.class);
    }

    public String demoCode() throws SQLException {
        // Use your favourite tool to construct SQL strings:
        String sql = "SELECT title, first_name, last_name FROM book JOIN author ON book.author_id = author.id " +
                "WHERE book.published_in = 1984";

        // Fetch results using jOOQ
        Result<Record> jooqResult = create.fetch(sql);

        // Or execute that SQL with JDBC, fetching the ResultSet with jOOQ:
        ResultSet rs = connection.createStatement().executeQuery(sql);
        Result<Record> result = create.fetch(rs);
        return jooqResult.get(0).getValue(1, String.class);
    }
}
