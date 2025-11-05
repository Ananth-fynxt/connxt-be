package connxt.conversionrate.service;

import java.util.List;

import connxt.conversionrate.dto.ConversionRateDto;
import connxt.conversionrate.dto.ConversionRateRawDataDto;
import connxt.conversionrate.entity.FixerApiCurrencyPair;

public interface ConversionRateService {

  ConversionRateRawDataDto upsertRawData(ConversionRateRawDataDto conversionRateRawDataDto);

  List<FixerApiCurrencyPair> getAllCurrencyPairs();

  ConversionRateDto createRate(ConversionRateDto conversionRateDto);

  ConversionRateDto readLatestRate(String id);

  List<ConversionRateDto> readRatesByBrandAndEnvironment(String brandId, String environmentId);

  ConversionRateDto updateRate(String id, ConversionRateDto conversionRateDto);

  void deleteRate(String id);
}
