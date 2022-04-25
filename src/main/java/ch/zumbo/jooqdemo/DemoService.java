package ch.zumbo.jooqdemo;

import static ch.zumbo.jooqdemo.Tables.AUTHOR;
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
        var t = create.fetch("");
    }

    public String demoCode() throws SQLException {
        return "Hier ist die Spielwiese für euren jOOQ-Code!";
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
}
