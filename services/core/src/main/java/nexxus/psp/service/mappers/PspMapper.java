package nexxus.psp.service.mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import nexxus.flowtarget.dto.FlowTargetDto;
import nexxus.psp.dto.*;
import nexxus.psp.entity.MaintenanceWindow;
import nexxus.psp.entity.Psp;
import nexxus.psp.entity.PspOperation;
import nexxus.shared.db.mappers.MapperCoreConfig;

/**
 * MapStruct mapper for PSP entity conversions Uses MapperCoreConfig and CommonMappingUtil for
 * consistent mapping behavior
 */
@Mapper(config = MapperCoreConfig.class)
public interface PspMapper {

  Psp toEntity(PspDto pspDto);

  PspDto toDto(Psp psp);

  void updateEntityFromDto(UpdatePspDto updatePspDto, @MappingTarget Psp psp);

  PspSummaryDto toPspSummaryDto(Psp psp);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  IdNameDto toIdNameDto(Psp psp);

  List<IdNameDto> toIdNameDto(List<Psp> psps);

  default Map<String, IdNameDto> toIdNameDtoMap(List<Psp> psps) {
    if (psps == null) {
      return Map.of();
    }
    return psps.stream().collect(Collectors.toMap(Psp::getId, this::toIdNameDto));
  }

  @Mapping(target = "pspId", source = "pspId")
  @Mapping(target = "flowActionId", source = "dto.flowActionId")
  @Mapping(target = "startAt", source = "dto.startAt", qualifiedByName = "stringToLocalDateTime")
  @Mapping(target = "endAt", source = "dto.endAt", qualifiedByName = "stringToLocalDateTime")
  MaintenanceWindow toMaintenanceWindow(UpdatePspDto.MaintenanceWindowDto dto, String pspId);

  //    @Mapping(target = "brandId", source = "brandId")
  @Mapping(target = "pspId", source = "pspId")
  @Mapping(target = "flowActionId", source = "dto.flowActionId")
  @Mapping(target = "flowDefinitionId", source = "dto.flowDefinitionId")
  @Mapping(target = "status", source = "dto.status")
  @Mapping(target = "currencies", source = "dto.currencies")
  @Mapping(target = "countries", source = "dto.countries")
  PspOperation toPspOperation(
      UpdatePspDto.PspOperationDto dto, String brandId, String environmentId, String pspId);

  @org.mapstruct.Named("extractCurrencies")
  default List<String> extractCurrencies(List<UpdatePspDto.CurrencyDto> currencyDtos) {
    if (currencyDtos == null) {
      return List.of();
    }
    return currencyDtos.stream().map(UpdatePspDto.CurrencyDto::getCurrency).toList();
  }

  // PspDetailsDto mapping methods
  @Mapping(target = "id", source = "psp.id")
  @Mapping(target = "credential", constant = "***ENCRYPTED***")
  @Mapping(target = "ipAddress", source = "psp.ipAddress")
  @Mapping(target = "maintenanceWindow", source = "maintenanceWindows")
  @Mapping(target = "operations", source = "operations")
  @Mapping(target = "flowTarget", source = "flowTarget")
  PspDetailsDto toPspDetailsDto(
      Psp psp,
      List<MaintenanceWindow> maintenanceWindows,
      List<PspOperation> operations,
      PspDetailsDto.FlowTargetInfo flowTarget);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "flowActionId", source = "flowActionId")
  @Mapping(target = "startAt", source = "startAt")
  @Mapping(target = "endAt", source = "endAt")
  PspDetailsDto.MaintenanceWindowDto toMaintenanceWindowDto(MaintenanceWindow maintenanceWindow);

  @Mapping(target = "flowActionId", source = "flowActionId")
  @Mapping(target = "flowDefinitionId", source = "flowDefinitionId")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "currencies", source = "currencies")
  PspDetailsDto.PspOperationDto toPspOperationDto(PspOperation operation);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "credentialSchema", source = "credentialSchema")
  @Mapping(target = "flowTypeId", source = "flowTypeId")
  @Mapping(target = "currencies", source = "currencies")
  @Mapping(target = "countries", source = "countries")
  @Mapping(target = "paymentMethods", source = "paymentMethods")
  @Mapping(target = "supportedActions", source = "supportedActions")
  PspDetailsDto.FlowTargetInfo toFlowTargetInfo(FlowTargetDto flowTargetDto);

  @Mapping(target = "flowActionId", source = "flowActionId")
  @Mapping(target = "flowDefinitionId", source = "id")
  @Mapping(target = "flowActionName", source = "flowActionName")
  PspDetailsDto.SupportedActionInfo toSupportedActionInfo(
      FlowTargetDto.SupportedActionInfo supportedActionInfo);

  @org.mapstruct.Named("stringArrayToList")
  default List<String> stringArrayToList(String[] stringArray) {
    if (stringArray == null) {
      return List.of();
    }
    return List.of(stringArray);
  }

  @org.mapstruct.Named("stringToLocalDateTime")
  default LocalDateTime stringToLocalDateTime(String dateTimeString) {
    if (dateTimeString == null) {
      return null;
    }
    return LocalDateTime.parse(dateTimeString);
  }

  @org.mapstruct.Named("extractFlowDefinitionId")
  default String extractFlowDefinitionId(PspOperation pspOperation) {
    if (pspOperation == null) {
      return null;
    }
    return pspOperation.getFlowDefinitionId();
  }
}
