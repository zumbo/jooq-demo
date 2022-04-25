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

    public String demoCode() {
        // Fetch a SQL string from a jOOQ Query in order to manually execute it with another tool.
        var result = create.select(BOOK.TITLE, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .from(BOOK)
                .join(AUTHOR)
                .on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .where(BOOK.PUBLISHED_IN.eq(1948))
                .fetchOne();
        return result.getValue(BOOK.TITLE) + ", " +
                result.getValue(AUTHOR.FIRST_NAME) + " " +
                result.getValue(AUTHOR.LAST_NAME);
    }

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

    public String demoCode4() throws SQLException {
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

    /*
     * Aufgaben:
     * 1) Mache eine DB-Abfrage nach dem Titel und Autor aller Bücher, die 1948 erschienen sind und zeige das
     *    Resultat an. (Hint: der DSLContext (Variablenname per Konvention create) ist Ausgangspunkt für alle Queries.)
     *    Bewahre den Code zu dieser Query für Übung 8) auf.
     * 2) Zeige das generierte SQL aus obiger Query an (Hint: Query.getSQL())
     * 3) Verwende jOOQ als reinen SQL-Generator, indem du die eigentliche Query in pure JDBC ausführst
     *    (connection.createStatement().executeQuery(sql))
     * 4) Und umgekehrt: Mach eine jOOQ-Query (create.fetch(sql)), die "handgeschriebenes" SQL entgegennimmt.
     * 5) Ändere mit der Query-DSL den Buchtitel von "O Alquimista" in "Der Alchimist" (Hint: create.update())
     * 6) Lege mit der Query-DSL einen neuen Autor namens Dan Brown an.
     * 7) Lade das Buch mit dem Titel "Animal Farm" als Active Record
     *    (Hint: Mit create.fetchOne(Table<R> table, Condition condition) ist es besonders einfach, du kannst aber
     *    auch andere Methoden ausprobieren) und ändere den Titel in "Farm der Tiere". (Hint: store() speichert
     *    einen Active Record).
     * 8) Was passiert, wenn du in der Query aus 1) die Jahreszahl als String statt als Integer übergibst?
     *    Lösche nun den Ordner target/generated-sources/jooq und schreibe den Code so um, dass er ohne
     *    generierte Klassen auskommt. (Hint: statische Methoden table() und field()).
     *    Was passiert nun, wenn die Jahreszahl als String übergeben wird?
     * 9) Generiere die DB-Klassen wieder (kompletter Rebuild, oder ausführen des Plugins jooq-codegen).
     *    Schaue die generierte Klasse Book an. Was ändert sich, wenn du in schema.sql die foreign keys löschst?
     *    Füge die foreign keys wieder ein und regeneriere Book wieder. Verwende nun den zum FK generierten Code,
     *    um die Query aus 1) ohne JOIN (implicit JOIN) umzuschreiben.
     * 10) Gib eine Liste aller Bücher aus, ohne Loops zu verwenden.
     *     (Hint: Result hat die Methoden map() und stream())
     * 11) Bonusaufgabe: Schreibe einen ExecuteListener, der ein Audit-Log (Konsolenausgabe reicht hier) für
     *     alle Schreiboperationen führt.
     */
}
