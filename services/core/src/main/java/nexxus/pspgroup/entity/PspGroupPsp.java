package nexxus.pspgroup.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "psp_group_psps")
@IdClass(PspGroupPspId.class)
public class PspGroupPsp {

  @Id
  @Column(name = "psp_group_id")
  private String pspGroupId;

  @Id
  @Column(name = "psp_group_version")
  private Integer pspGroupVersion;

  @Id
  @Column(name = "psp_id")
  private String pspId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumns({
    @JoinColumn(
        name = "psp_group_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false),
    @JoinColumn(
        name = "psp_group_version",
        referencedColumnName = "version",
        insertable = false,
        updatable = false)
  })
  private PspGroup pspGroup;
}
