package nexxus.autoapproval.service.mappers;

import java.util.Arrays;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import nexxus.autoapproval.dto.AutoApprovalDto;
import nexxus.autoapproval.entity.AutoApproval;
import nexxus.autoapproval.entity.EmbeddableAutoApprovalId;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface AutoApprovalMapper {

  @Mapping(target = "id", source = "autoApprovalId.id")
  @Mapping(target = "version", source = "autoApprovalId.version")
  @Mapping(
      target = "countries",
      expression = "java(countriesArrayToList(autoApproval.getCountries()))")
  @Mapping(target = "psps", ignore = true)
  AutoApprovalDto toAutoApprovalDto(AutoApproval autoApproval);

  @Mapping(
      target = "autoApprovalId",
      expression = "java(createEmbeddableAutoApprovalId(autoApprovalDto.getId(), version))")
  @Mapping(
      target = "countries",
      expression = "java(countriesListToArray(autoApprovalDto.getCountries()))")
  AutoApproval toAutoApproval(AutoApprovalDto autoApprovalDto, Integer version);

  @Mapping(
      target = "autoApprovalId",
      expression =
          "java(createEmbeddableAutoApprovalId(existingAutoApproval.getAutoApprovalId().getId(), version))")
  @Mapping(target = "name", source = "updateDto.name")
  @Mapping(target = "currency", source = "updateDto.currency")
  @Mapping(target = "brandId", source = "updateDto.brandId")
  @Mapping(target = "environmentId", source = "updateDto.environmentId")
  @Mapping(target = "flowActionId", source = "updateDto.flowActionId")
  @Mapping(target = "maxAmount", source = "updateDto.maxAmount")
  @Mapping(target = "status", source = "updateDto.status")
  @Mapping(
      target = "countries",
      expression = "java(countriesListToArray(updateDto.getCountries()))")
  AutoApproval createUpdatedAutoApproval(
      AutoApproval existingAutoApproval, AutoApprovalDto updateDto, Integer version);

  default EmbeddableAutoApprovalId createEmbeddableAutoApprovalId(String id, Integer version) {
    return new EmbeddableAutoApprovalId(id, version);
  }

  default List<String> countriesArrayToList(String[] countries) {
    return countries != null ? Arrays.asList(countries) : null;
  }

  default String[] countriesListToArray(List<String> countries) {
    return countries != null ? countries.toArray(new String[0]) : null;
  }
}
