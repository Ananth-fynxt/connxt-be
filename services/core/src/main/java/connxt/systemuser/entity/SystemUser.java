package connxt.systemuser.entity;

import org.hibernate.annotations.Type;

import connxt.shared.constants.Scope;
import connxt.shared.constants.UserStatus;
import connxt.shared.db.AuditingEntity;
import connxt.shared.db.PostgreSQLEnumType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 * Entity representing FYNXT system users with SYSTEM scope. These users have full access to all FIs
 * and brands in the system.
 */
@Entity
@Table(name = "system_users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemUser extends AuditingEntity {

  @Id @SystemUserId private String id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "user_id")
  private String userId;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "connxt.shared.constants.Scope"))
  @Enumerated(EnumType.STRING)
  @Column(name = "scope", nullable = false, columnDefinition = "scope")
  @Builder.Default
  private Scope scope = Scope.SYSTEM;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "connxt.shared.constants.UserStatus"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, columnDefinition = "user_status")
  @Builder.Default
  private UserStatus status = UserStatus.ACTIVE;
}
