package connxt.fee.service.mappers;

import java.util.Arrays;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import connxt.fee.dto.FeeDto;
import connxt.fee.entity.EmbeddableFeeId;
import connxt.fee.entity.Fee;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface FeeMapper {

  @Mapping(target = "id", source = "feeId.id")
  @Mapping(target = "version", source = "feeId.version")
  @Mapping(target = "countries", expression = "java(countriesArrayToList(fee.getCountries()))")
  @Mapping(target = "components", ignore = true)
  @Mapping(target = "psps", ignore = true)
  FeeDto toFeeDto(Fee fee);

  @Mapping(target = "feeId", expression = "java(createEmbeddableFeeId(feeDto.getId(), version))")
  @Mapping(target = "countries", expression = "java(countriesListToArray(feeDto.getCountries()))")
  Fee toFee(FeeDto feeDto, Integer version);

  @Mapping(
      target = "feeId",
      expression = "java(createEmbeddableFeeId(existingFee.getFeeId().getId(), version))")
  @Mapping(target = "countries", source = "existingFee.countries")
  Fee createUpdatedFee(Fee existingFee, Integer version);

  default EmbeddableFeeId createEmbeddableFeeId(String id, Integer version) {
    return new EmbeddableFeeId(id, version);
  }

  default List<String> countriesArrayToList(String[] countries) {
    return countries != null ? Arrays.asList(countries) : null;
  }

  default String[] countriesListToArray(List<String> countries) {
    return countries != null ? countries.toArray(new String[0]) : null;
  }
}
