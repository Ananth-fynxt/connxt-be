package connxt.webhook.entity;

import org.hibernate.annotations.Type;

import connxt.shared.constants.Status;
import connxt.shared.constants.WebhookStatusType;
import connxt.shared.db.AuditingEntity;
import connxt.shared.db.PostgreSQLEnumType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "webhooks")
@Builder
public class Webhook extends AuditingEntity {

  @WebhookId
  @Id
  @Column(name = "id")
  private String id;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "connxt.shared.constants.WebhookStatusType"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status_type", columnDefinition = "webhook_status_type")
  private WebhookStatusType statusType;

  @Column(name = "url")
  private String url;

  @Column(name = "retry")
  @Builder.Default
  private Integer retry = 3;

  @Column(name = "brand_id")
  private String brandId;

  @Column(name = "environment_id")
  private String environmentId;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "connxt.shared.constants.Status"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "status")
  @Builder.Default
  private Status status = Status.ENABLED;
}
