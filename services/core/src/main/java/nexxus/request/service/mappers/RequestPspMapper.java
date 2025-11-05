package nexxus.request.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import nexxus.request.dto.RequestOutputDto;
import nexxus.request.entity.RequestPsp;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface RequestPspMapper {

  @Mapping(target = "id", source = "pspId")
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "description", ignore = true)
  @Mapping(target = "logo", ignore = true)
  @Mapping(target = "brandId", ignore = true)
  @Mapping(target = "environmentId", ignore = true)
  @Mapping(target = "flowActionId", ignore = true)
  @Mapping(target = "flowDefintionId", source = "flowDefinitionId")
  @Mapping(target = "walletId", ignore = true)
  @Mapping(target = "isFeeApplied", ignore = true)
  RequestOutputDto.PspInfo toPspInfo(RequestPsp requestPsp);

  @Mapping(target = "requestId", source = "requestId")
  @Mapping(target = "pspId", source = "pspInfo.id")
  @Mapping(target = "flowTargetId", source = "pspInfo.flowTarget.flowTargetId")
  @Mapping(target = "flowDefinitionId", source = "pspInfo.flowDefintionId")
  @Mapping(target = "currency", source = "pspInfo.currency")
  @Mapping(target = "originalAmount", source = "pspInfo.originalAmount")
  @Mapping(target = "appliedFeeAmount", source = "pspInfo.appliedFeeAmount")
  @Mapping(target = "totalAmount", source = "pspInfo.totalAmount")
  @Mapping(target = "netAmountToUser", source = "pspInfo.netAmountToUser")
  @Mapping(target = "inclusiveFeeAmount", source = "pspInfo.inclusiveFeeAmount")
  @Mapping(target = "exclusiveFeeAmount", source = "pspInfo.exclusiveFeeAmount")
  @Mapping(target = "isFeeApplied", source = "pspInfo.feeApplied")
  @Mapping(target = "conversionFromCurrency", source = "pspInfo.conversionFromCurrency")
  @Mapping(target = "conversionToCurrency", source = "pspInfo.conversionToCurrency")
  @Mapping(target = "conversionExchangeRate", source = "pspInfo.conversionExchangeRate")
  @Mapping(target = "conversionConvertedAmount", source = "pspInfo.conversionConvertedAmount")
  @Mapping(target = "isConversionApplied", source = "pspInfo.conversionApplied")
  RequestPsp toRequestPsp(String requestId, RequestOutputDto.PspInfo pspInfo);
}
