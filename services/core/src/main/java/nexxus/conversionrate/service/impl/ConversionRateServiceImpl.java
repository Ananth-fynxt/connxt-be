package nexxus.conversionrate.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import nexxus.conversionrate.dto.ConversionRateDto;
import nexxus.conversionrate.dto.ConversionRateRawDataDto;
import nexxus.conversionrate.entity.ConversionRate;
import nexxus.conversionrate.entity.ConversionRateRawData;
import nexxus.conversionrate.entity.FixerApiCurrencyPair;
import nexxus.conversionrate.repository.ConversionRateRawDataRepository;
import nexxus.conversionrate.repository.ConversionRateRepository;
import nexxus.conversionrate.repository.FixerApiCurrencyPairRepository;
import nexxus.conversionrate.service.ConversionRateService;
import nexxus.conversionrate.service.mappers.ConversionRateMapper;
import nexxus.conversionrate.service.mappers.ConversionRateRawDataMapper;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.constants.Status;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversionRateServiceImpl implements ConversionRateService {

  private final ConversionRateRawDataRepository conversionRateRawDataRepository;
  private final ConversionRateRepository conversionRateRepository;
  private final FixerApiCurrencyPairRepository fixerApiCurrencyPairRepository;
  private final ConversionRateRawDataMapper conversionRateRawDataMapper;
  private final ConversionRateMapper conversionRateMapper;

  @Override
  @Transactional
  public ConversionRateRawDataDto upsertRawData(
      @Valid ConversionRateRawDataDto conversionRateRawDataDto) {
    // Check if currency pair already exists
    Optional<ConversionRateRawData> existingRawDataOpt =
        conversionRateRawDataRepository.findLatestByCurrencyPair(
            conversionRateRawDataDto.getSourceCurrency(),
            conversionRateRawDataDto.getTargetCurrency());

    if (existingRawDataOpt.isPresent()) {
      // Update existing record with new version
      ConversionRateRawData existingRawData = existingRawDataOpt.get();
      Integer newVersion = existingRawData.getRawDataId().getVersion() + 1;

      ConversionRateRawData updatedRawData =
          conversionRateRawDataMapper.createUpdatedRawData(existingRawData, newVersion);
      updatedRawData.setSourceCurrency(conversionRateRawDataDto.getSourceCurrency());
      updatedRawData.setTargetCurrency(conversionRateRawDataDto.getTargetCurrency());
      updatedRawData.setTimeRange(conversionRateRawDataDto.getTimeRange());
      updatedRawData.setAmount(conversionRateRawDataDto.getAmount());

      ConversionRateRawData savedRawData = conversionRateRawDataRepository.save(updatedRawData);
      log.info(
          "Updated existing currency pair {} -> {} to version {}",
          conversionRateRawDataDto.getSourceCurrency(),
          conversionRateRawDataDto.getTargetCurrency(),
          newVersion);
      return conversionRateRawDataMapper.toConversionRateRawDataDto(savedRawData);
    } else {
      // Create new record
      ConversionRateRawData rawData =
          conversionRateRawDataMapper.toConversionRateRawData(conversionRateRawDataDto, 1);
      ConversionRateRawData savedRawData = conversionRateRawDataRepository.save(rawData);
      log.info(
          "Created new currency pair {} -> {}",
          conversionRateRawDataDto.getSourceCurrency(),
          conversionRateRawDataDto.getTargetCurrency());
      return conversionRateRawDataMapper.toConversionRateRawDataDto(savedRawData);
    }
  }

  @Override
  public List<FixerApiCurrencyPair> getAllCurrencyPairs() {
    return fixerApiCurrencyPairRepository.findAll();
  }

  @Override
  @Transactional
  public ConversionRateDto createRate(@Valid ConversionRateDto conversionRateDto) {
    verifyRateNotExists(conversionRateDto);

    ConversionRate rate = conversionRateMapper.toConversionRate(conversionRateDto, 1);
    rate.setStatus(Status.ENABLED);

    ConversionRate savedRate = conversionRateRepository.save(rate);
    return conversionRateMapper.toConversionRateDto(savedRate);
  }

  @Override
  public ConversionRateDto readLatestRate(String id) {
    ConversionRate rate =
        conversionRateRepository
            .findLatestVersionById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.CONVERSION_RATE_NOT_FOUND.getCode()));
    return conversionRateMapper.toConversionRateDto(rate);
  }

  @Override
  public List<ConversionRateDto> readRatesByBrandAndEnvironment(
      String brandId, String environmentId) {
    List<ConversionRate> rates =
        conversionRateRepository.findByBrandIdAndEnvironmentId(brandId, environmentId);
    return rates.stream()
        .map(conversionRateMapper::toConversionRateDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ConversionRateDto updateRate(String id, @Valid ConversionRateDto conversionRateDto) {
    ConversionRate existingRate =
        conversionRateRepository
            .findLatestVersionById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.CONVERSION_RATE_NOT_FOUND.getCode()));

    Integer newVersion = existingRate.getRateId().getVersion() + 1;

    ConversionRate updatedRate = conversionRateMapper.createUpdatedRate(existingRate, newVersion);
    updatedRate.setBrandId(conversionRateDto.getBrandId());
    updatedRate.setEnvironmentId(conversionRateDto.getEnvironmentId());
    updatedRate.setStatus(Status.ENABLED);
    updatedRate.setSourceCurrency(conversionRateDto.getSourceCurrency());
    updatedRate.setTargetCurrency(conversionRateDto.getTargetCurrency());
    updatedRate.setValue(conversionRateDto.getValue());

    ConversionRate savedRate = conversionRateRepository.save(updatedRate);
    return conversionRateMapper.toConversionRateDto(savedRate);
  }

  @Override
  @Transactional
  public void deleteRate(String id) {
    if (conversionRateRepository.findLatestVersionById(id).isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.CONVERSION_RATE_NOT_FOUND.getCode());
    }

    conversionRateRepository.deleteByRateIdId(id);
  }

  private void verifyRateNotExists(ConversionRateDto conversionRateDto) {
    if (conversionRateRepository.existsBySourceCurrencyAndTargetCurrencyAndBrandIdAndEnvironmentId(
        conversionRateDto.getSourceCurrency(), conversionRateDto.getTargetCurrency(),
        conversionRateDto.getBrandId(), conversionRateDto.getEnvironmentId())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.CONVERSION_RATE_ALREADY_EXISTS.getCode());
    }
  }
}
