package ch.zumbo.jooqdemo;

import ch.zumbo.jooqdemo.tables.records.BookRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static ch.zumbo.jooqdemo.Tables.AUTHOR;
import static ch.zumbo.jooqdemo.Tables.BOOK;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Component
public class DemoService {

    private final DSLContext create;
    private final Connection connection;

    DemoService(DataSource dataSource) throws SQLException {
        connection = dataSource.getConnection();
        // Create your Configuration
        Configuration configuration = new DefaultConfiguration()
                .set(connection).set(SQLDialect.H2)
                .set(new DefaultExecuteListenerProvider(new AuditListener())
        );

        create = DSL.using(configuration);
    }
 /* 1) Mache eine DB-Abfrage nach dem Titel und Autor aller Bücher, die 1948 erschienen sind und zeige das
       Resultat an. (Hint: der DSLContext (Variablenname per Konvention create) ist Ausgangspunkt für alle Queries.)
       Bewahre den Code zu dieser Query für Übung 8) auf. */
    public String demoCode1() {
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
    // 2) Zeige das generierte SQL aus obiger Query an (Hint: Query.getSQL())
    public String demoCode2() {
        var query = create.select(BOOK.TITLE, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .from(BOOK)
                .join(AUTHOR)
                .on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                .where(BOOK.PUBLISHED_IN.eq(1948));
        return query.getSQL(ParamType.INLINED);
    }

    // 3) Verwende jOOQ als reinen SQL-Generator, indem du die eigentliche Query in pure JDBC ausführst
    public String demoCode3() throws SQLException {
        String sql = demoCode2();
        var result = connection.createStatement().executeQuery(sql);
        if (result.next()) {
            return result.getString("TITLE") + ", " +
                    result.getString("FIRST_NAME") + " " +
                    result.getString("LAST_NAME");
        } else {
            return "Nichts gefunden";
        }
    }

    // 4) Und umgekehrt: Mach eine jOOQ-Query (create.fetch(sql)), die "handgeschriebenes" SQL entgegennimmt.
    public String demoCode4() throws SQLException {
        String sql = "SELECT title, first_name, last_name FROM book JOIN author ON book.author_id = author.id " +
                "WHERE book.published_in = 1948";

        // Fetch results using jOOQ
        var result = create.fetchOne(sql);

        return result.getValue(BOOK.TITLE) + ", " +
                result.getValue(AUTHOR.FIRST_NAME) + " " +
                result.getValue(AUTHOR.LAST_NAME);
    }

    // 5) Ändere mit der Query-DSL den Buchtitel von "O Alquimista" in "Der Alchimist" (Hint: create.update())
    public String demoCode5() {
        int result =
                create.update(BOOK).set(BOOK.TITLE, "Der Alchimist").where(BOOK.TITLE.eq("O Alquimista")).execute();
        return "" + result;
    }

    // 6) Lege mit der Query-DSL einen neuen Autor namens Dan Brown an.
    public String demoCode6() {
        int result = create.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .values(100, "Dan", "Brown")
                .execute();
        return "" + result;
    }
    /*
     * 7) Lade das Buch mit dem Titel "Animal Farm" als Active Record
     *    (Hint: Mit create.fetchOne(Table<R> table, Condition condition) ist es besonders einfach, du kannst aber
     *    auch andere Methoden ausprobieren) und ändere den Titel in "Farm der Tiere". (Hint: store() speichert
     *    einen Active Record).
     */
    public String demoCode7() {
        BookRecord book = create.fetchOne(BOOK, BOOK.TITLE.eq("Animal Farm"));
        book.setTitle("Farm der Tiere");
        int result = book.store();
        return "" + result;
    }

    /*
     * 8) Was passiert, wenn du in der Query aus 1) die Jahreszahl als String statt als Integer übergibst?
     *    Lösche nun den Ordner target/generated-sources/jooq und schreibe den Code so um, dass er ohne
     *    generierte Klassen auskommt. (Hint: statische Methoden table() und field()).
     *    Was passiert nun, wenn die Jahreszahl als String übergeben wird?
     */
    public String demoCode8() {
        var result = create.select(BOOK.TITLE, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .from("BOOK")
                .join(table("AUTHOR"))
                .on(field("BOOK.AUTHOR_ID").eq(field("AUTHOR.ID")))
                .where(field("BOOK.PUBLISHED_IN").eq("1948"))
                .fetchOne();
        return result.getValue("TITLE") + ", " +
                result.getValue("FIRST_NAME") + " " +
                result.getValue("LAST_NAME");
    }
     /* 9) Generiere die DB-Klassen wieder (kompletter Rebuild, oder ausführen des Plugins jooq-codegen).
     *    Schaue die generierte Klasse Book an. Was ändert sich, wenn du in schema.sql die foreign keys löschst?
     *    Füge die foreign keys wieder ein und regeneriere Book wieder. Verwende nun den zum FK generierten Code,
     *    um die Query aus 1) ohne JOIN (implicit JOIN) umzuschreiben.
     */
     public String demoCode9() {
         var result = create.select(BOOK.TITLE, BOOK.author().FIRST_NAME,  BOOK.author().LAST_NAME)
                 .from(BOOK)
                 .where(BOOK.PUBLISHED_IN.eq(1948))
                 .fetchOne();
         return result.getValue(BOOK.TITLE) + ", " +
                 result.getValue(AUTHOR.FIRST_NAME) + " " +
                 result.getValue(AUTHOR.LAST_NAME);
     }
     /* 10) Gib eine Liste aller Bücher aus, ohne Loops zu verwenden.
     *     (Hint: Result hat die Methoden map() und stream())
     */
    public String demoCode10() {
        return create.selectFrom(BOOK).stream().map(BookRecord::getTitle).collect(Collectors.joining(" "));
    }
     /* 11) Bonusaufgabe: Schreibe einen ExecuteListener, der ein Audit-Log (Konsolenausgabe reicht hier) für
     *     alle Schreiboperationen führt.
     */
}
