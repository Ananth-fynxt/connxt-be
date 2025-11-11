package connxt.flow.boot.bootstrap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlowSchemaInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(FlowSchemaInitializer.class);

  private static final Map<String, String> ENUM_DDL = new LinkedHashMap<>();
  private static final Map<String, String> TABLE_DDL = new LinkedHashMap<>();
  private static final List<String> INDEX_DDL =
      List.of(
          "CREATE UNIQUE INDEX IF NOT EXISTS flow_action_unique_constraint ON flow_actions(flow_type_id, name)",
          "CREATE INDEX IF NOT EXISTS idx_flow_actions_flow_type_id ON flow_actions(flow_type_id)",
          "CREATE UNIQUE INDEX IF NOT EXISTS flow_target_type_name ON flow_targets(flow_type_id, name)",
          "CREATE INDEX IF NOT EXISTS idx_flow_targets_flow_type_id ON flow_targets(flow_type_id)",
          "CREATE UNIQUE INDEX IF NOT EXISTS flow_definition_code_unique ON flow_definitions(code)",
          "CREATE UNIQUE INDEX IF NOT EXISTS flow_definition_action_target ON flow_definitions(flow_action_id, flow_target_id)",
          "CREATE INDEX IF NOT EXISTS idx_flow_definitions_flow_target_id ON flow_definitions(flow_target_id)",
          "CREATE INDEX IF NOT EXISTS idx_flow_definitions_flow_action_id ON flow_definitions(flow_action_id)");

  static {
    ENUM_DDL.put(
        "flow_status",
        """
        CREATE TYPE flow_status AS ENUM ('ENABLED', 'DISABLED')
        """);

    TABLE_DDL.put(
        "flow_types",
        """
        CREATE TABLE IF NOT EXISTS flow_types (
          id TEXT PRIMARY KEY,
          name TEXT NOT NULL UNIQUE,
          created_at TIMESTAMP NOT NULL,
          updated_at TIMESTAMP NOT NULL,
          created_by TEXT NOT NULL,
          updated_by TEXT NOT NULL
        )
        """);

    TABLE_DDL.put(
        "flow_actions",
        """
        CREATE TABLE IF NOT EXISTS flow_actions (
          id TEXT PRIMARY KEY,
          name TEXT NOT NULL,
          steps TEXT[] NOT NULL,
          flow_type_id TEXT NOT NULL REFERENCES flow_types(id),
          input_schema JSONB NOT NULL DEFAULT '{}',
          output_schema JSONB NOT NULL DEFAULT '{}',
          created_at TIMESTAMP NOT NULL,
          updated_at TIMESTAMP NOT NULL,
          created_by TEXT NOT NULL,
          updated_by TEXT NOT NULL
        )
        """);

    TABLE_DDL.put(
        "flow_targets",
        """
        CREATE TABLE IF NOT EXISTS flow_targets (
          id TEXT PRIMARY KEY,
          name TEXT NOT NULL,
          logo TEXT,
          status flow_status NOT NULL DEFAULT 'ENABLED',
          credential_schema JSONB NOT NULL DEFAULT '{}',
          input_schema JSONB NOT NULL DEFAULT '{}',
          flow_type_id TEXT NOT NULL REFERENCES flow_types(id),
          created_at TIMESTAMP NOT NULL,
          updated_at TIMESTAMP NOT NULL,
          created_by TEXT NOT NULL,
          updated_by TEXT NOT NULL
        )
        """);

    TABLE_DDL.put(
        "flow_definitions",
        """
        CREATE TABLE IF NOT EXISTS flow_definitions (
          id TEXT PRIMARY KEY,
          flow_action_id TEXT NOT NULL REFERENCES flow_actions(id),
          flow_target_id TEXT NOT NULL REFERENCES flow_targets(id),
          description TEXT,
          code TEXT NOT NULL,
          flow_configuration JSONB,
          created_at TIMESTAMP NOT NULL,
          updated_at TIMESTAMP NOT NULL,
          created_by TEXT NOT NULL,
          updated_by TEXT NOT NULL
        )
        """);
  }

  private final JdbcTemplate jdbcTemplate;

  public void initialize() {
    LOGGER.info("Starting flow schema bootstrap");
    try {
      ENUM_DDL.forEach(
          (type, ddl) -> {
            if (!enumTypeExists(type)) {
              LOGGER.info("Creating flow enum type '{}'", type);
              jdbcTemplate.execute(ddl);
            }
          });

      TABLE_DDL.forEach(
          (table, ddl) -> {
            if (!tableExists(table)) {
              LOGGER.info("Creating flow table '{}'", table);
              jdbcTemplate.execute(ddl);
            }
          });

      INDEX_DDL.forEach(sql -> jdbcTemplate.execute(sql));
      LOGGER.info("Flow schema bootstrap completed");
    } catch (DataAccessException ex) {
      LOGGER.warn(
          "Flow schema initialization skipped due to database access failure: {}", ex.getMessage());
      LOGGER.debug("Flow schema initialization error", ex);
    }
  }

  private boolean enumTypeExists(String typeName) {
    Boolean exists =
        jdbcTemplate.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM pg_type WHERE typname = ?)",
            Boolean.class,
            typeName);
    return Boolean.TRUE.equals(exists);
  }

  private boolean tableExists(String tableName) {
    Boolean exists =
        jdbcTemplate.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = current_schema() AND table_name = ?)",
            Boolean.class,
            tableName);
    return Boolean.TRUE.equals(exists);
  }
}
