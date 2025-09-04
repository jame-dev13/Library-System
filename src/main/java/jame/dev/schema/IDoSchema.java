package jame.dev.schema;

/**
 * Defines the way to create a Sql Table.
 */
public interface IDoSchema {
   /**
    * Creates a sql named table.
    * @param name The name of the table.
    * @param query The query to be executed.
    */
    void createTable(String name, String query);
}
