package nexxus.pspgroup.service.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import nexxus.psp.dto.IdNameDto;
import nexxus.pspgroup.dto.PspGroupDto;
import nexxus.pspgroup.entity.EmbeddablePspGroupId;
import nexxus.pspgroup.entity.PspGroup;
import nexxus.pspgroup.entity.PspGroupPsp;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface PspGroupMapper {

  @Mapping(target = "id", source = "pspGroup.pspGroupId.id")
  @Mapping(target = "version", source = "pspGroup.pspGroupId.version")
  @Mapping(target = "psps", source = "psps")
  PspGroupDto toPspGroupDto(PspGroup pspGroup, List<IdNameDto> psps);

  @Mapping(
      target = "pspGroupId",
      expression = "java(createEmbeddablePspGroupId(pspGroupDto.getId(), version))")
  @Mapping(target = "pspGroupPsps", ignore = true)
  PspGroup toPspGroup(PspGroupDto pspGroupDto, Integer version);

  @Mapping(
      target = "pspGroupId",
      expression =
          "java(createEmbeddablePspGroupId(existingPspGroup.getPspGroupId().getId(), version))")
  @Mapping(
      target = "pspGroupPsps",
      expression =
          "java(mapExistingPspsToPspGroupPsps(existingPspGroup.getPspGroupPsps(), existingPspGroup.getPspGroupId().getId(), version))")
  PspGroup createUpdatedPspGroup(PspGroup existingPspGroup, Integer version);

  default EmbeddablePspGroupId createEmbeddablePspGroupId(String id, Integer version) {
    return new EmbeddablePspGroupId(id, version);
  }

  default List<PspGroupPsp> createPspGroupPsps(
      List<IdNameDto> psps, String pspGroupId, Integer version) {
    if (psps == null || psps.isEmpty()) {
      return List.of();
    }

    return psps.stream()
        .filter(psp -> psp != null && psp.getId() != null)
        .map(
            psp ->
                PspGroupPsp.builder()
                    .pspGroupId(pspGroupId)
                    .pspGroupVersion(version)
                    .pspId(psp.getId())
                    .build())
        .collect(Collectors.toList());
  }

  default List<PspGroupPsp> mapExistingPspsToPspGroupPsps(
      List<PspGroupPsp> existingPsps, String pspGroupId, Integer version) {
    if (existingPsps == null || existingPsps.isEmpty()) {
      return List.of();
    }

    return existingPsps.stream()
        .filter(existingPsp -> existingPsp != null && existingPsp.getPspId() != null)
        .map(
            existingPsp ->
                PspGroupPsp.builder()
                    .pspGroupId(pspGroupId)
                    .pspGroupVersion(version)
                    .pspId(existingPsp.getPspId())
                    .build())
        .collect(Collectors.toList());
  }
}
