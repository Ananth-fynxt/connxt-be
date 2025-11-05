package nexxus.conversionrate.service;

import java.util.List;

import nexxus.conversionrate.dto.ConversionRateDto;
import nexxus.conversionrate.dto.ConversionRateRawDataDto;
import nexxus.conversionrate.entity.FixerApiCurrencyPair;

public interface ConversionRateService {

  ConversionRateRawDataDto upsertRawData(ConversionRateRawDataDto conversionRateRawDataDto);

  List<FixerApiCurrencyPair> getAllCurrencyPairs();

  ConversionRateDto createRate(ConversionRateDto conversionRateDto);

  ConversionRateDto readLatestRate(String id);

  List<ConversionRateDto> readRatesByBrandAndEnvironment(String brandId, String environmentId);

  ConversionRateDto updateRate(String id, ConversionRateDto conversionRateDto);

  void deleteRate(String id);
}
