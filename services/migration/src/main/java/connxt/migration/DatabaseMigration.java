package connxt.migration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

public class DatabaseMigration {

  public static void main(String[] args) {
    try {
      DatabaseConfig config = loadDatabaseConfig();
      Flyway flyway = configureFlyway(config);

      if (args.length > 0 && "repair".equals(args[0])) {
        flyway.repair();
        System.out.println("Flyway repair completed");
        return;
      }

      MigrateResult result = flyway.migrate();
      logMigrationResult(result);

    } catch (Exception e) {
      System.err.println("Migration failed: " + e.getMessage());
      System.exit(1);
    }
  }

  private static DatabaseConfig loadDatabaseConfig() {
    String host = System.getenv("DATABASE_HOST");
    String port = System.getenv("DATABASE_PORT");
    String dbName = System.getenv("DATABASE_NAME");
    String username = System.getenv("DATABASE_USERNAME");
    String password = System.getenv("DATABASE_PASSWORD");

    if (host == null || port == null || dbName == null || username == null || password == null) {
      throw new IllegalStateException(
          "Missing required database environment variables: DATABASE_HOST, DATABASE_PORT, DATABASE_NAME, DATABASE_USERNAME, DATABASE_PASSWORD");
    }

    String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
    return new DatabaseConfig(jdbcUrl, username, password);
  }

  private static Flyway configureFlyway(DatabaseConfig config) {
    return Flyway.configure()
        .dataSource(config.jdbcUrl, config.username, config.password)
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .baselineVersion("0")
        .validateOnMigrate(true)
        .cleanDisabled(false)
        .outOfOrder(true)
        .table("flyway_schema_history")
        .sqlMigrationPrefix("V")
        .sqlMigrationSeparator("__")
        .sqlMigrationSuffixes(".sql")
        .encoding("UTF-8")
        .placeholderReplacement(false)
        .load();
  }

  private static void logMigrationResult(MigrateResult result) {
    if (result.migrationsExecuted > 0) {
      System.out.println("Applied " + result.migrationsExecuted + " migrations");
    } else {
      System.out.println("Database is up to date");
    }
  }

  public static void migrate() {
    main(new String[0]);
  }

  private static class DatabaseConfig {
    final String jdbcUrl;
    final String username;
    final String password;

    DatabaseConfig(String jdbcUrl, String username, String password) {
      this.jdbcUrl = jdbcUrl;
      this.username = username;
      this.password = password;
    }
  }
}
