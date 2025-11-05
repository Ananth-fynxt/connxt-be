package nexxus.systemuser.entity;

import org.hibernate.annotations.Type;

import nexxus.shared.constants.Scope;
import nexxus.shared.constants.UserStatus;
import nexxus.shared.db.AuditingEntity;
import nexxus.shared.db.PostgreSQLEnumType;

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
              value = "nexxus.shared.constants.Scope"))
  @Enumerated(EnumType.STRING)
  @Column(name = "scope", nullable = false, columnDefinition = "scope")
  @Builder.Default
  private Scope scope = Scope.SYSTEM;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.UserStatus"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, columnDefinition = "user_status")
  @Builder.Default
  private UserStatus status = UserStatus.ACTIVE;
}
