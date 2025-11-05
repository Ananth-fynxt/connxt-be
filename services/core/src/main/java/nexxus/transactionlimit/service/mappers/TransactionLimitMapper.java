package nexxus.transactionlimit.service.mappers;

import java.util.Arrays;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import nexxus.shared.db.mappers.MapperCoreConfig;
import nexxus.transactionlimit.dto.TransactionLimitDto;
import nexxus.transactionlimit.entity.EmbeddableTransactionLimitId;
import nexxus.transactionlimit.entity.TransactionLimit;

@Mapper(config = MapperCoreConfig.class)
public interface TransactionLimitMapper {

  @Mapping(target = "id", source = "transactionLimitId.id")
  @Mapping(target = "version", source = "transactionLimitId.version")
  @Mapping(
      target = "countries",
      expression = "java(countriesArrayToList(transactionLimit.getCountries()))")
  @Mapping(
      target = "customerTags",
      expression = "java(customerTagsArrayToList(transactionLimit.getCustomerTags()))")
  @Mapping(target = "pspActions", ignore = true)
  @Mapping(target = "psps", ignore = true)
  TransactionLimitDto toTransactionLimitDto(TransactionLimit transactionLimit);

  @Mapping(
      target = "transactionLimitId",
      expression = "java(createEmbeddableTransactionLimitId(transactionLimitDto.getId(), version))")
  @Mapping(
      target = "countries",
      expression = "java(countriesListToArray(transactionLimitDto.getCountries()))")
  @Mapping(
      target = "customerTags",
      expression = "java(customerTagsListToArray(transactionLimitDto.getCustomerTags()))")
  TransactionLimit toTransactionLimit(TransactionLimitDto transactionLimitDto, Integer version);

  @Mapping(
      target = "transactionLimitId",
      expression =
          "java(createEmbeddableTransactionLimitId(existingTransactionLimit.getTransactionLimitId().getId(), version))")
  @Mapping(target = "countries", source = "existingTransactionLimit.countries")
  @Mapping(target = "customerTags", source = "existingTransactionLimit.customerTags")
  TransactionLimit createUpdatedTransactionLimit(
      TransactionLimit existingTransactionLimit, Integer version);

  default EmbeddableTransactionLimitId createEmbeddableTransactionLimitId(
      String id, Integer version) {
    return new EmbeddableTransactionLimitId(id, version);
  }

  default List<String> countriesArrayToList(String[] countries) {
    return countries != null ? Arrays.asList(countries) : null;
  }

  default String[] countriesListToArray(List<String> countries) {
    return countries != null ? countries.toArray(new String[0]) : null;
  }

  default List<String> customerTagsArrayToList(String[] customerTags) {
    return customerTags != null ? Arrays.asList(customerTags) : null;
  }

  default String[] customerTagsListToArray(List<String> customerTags) {
    return customerTags != null ? customerTags.toArray(new String[0]) : null;
  }
}
