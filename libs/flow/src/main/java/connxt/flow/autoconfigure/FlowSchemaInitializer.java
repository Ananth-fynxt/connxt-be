package connxt.flow.autoconfigure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class FlowSchemaInitializer implements ApplicationRunner {

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
          status status NOT NULL DEFAULT 'ENABLED',
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

  @Override
  public void run(ApplicationArguments args) {
    TABLE_DDL.forEach(
        (table, ddl) -> {
          if (!tableExists(table)) {
            log.info("Creating flow table '{}'", table);
            jdbcTemplate.execute(ddl);
          }
        });

    INDEX_DDL.forEach(sql -> jdbcTemplate.execute(sql));
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
