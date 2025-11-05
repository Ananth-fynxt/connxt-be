package connxt.psp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import connxt.psp.entity.Psp;
import connxt.psp.service.AccessValidationService;
import connxt.psp.service.IpApiService;
import connxt.request.dto.RequestInputDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessValidationServiceImpl implements AccessValidationService {

  private final IpApiService ipApiService;

  @Override
  public boolean isAccessValid(Psp psp, RequestInputDto request) {
    if (!Boolean.TRUE.equals(psp.getBlockVpnAccess())
        && !Boolean.TRUE.equals(psp.getBlockDataCenterAccess())) {
      return true;
    }

    String clientIpAddress = request.getClientIpAddress();
    if (!StringUtils.hasText(clientIpAddress)) {
      log.debug(
          "No client IP address provided, skipping access validation for PSP: {}", psp.getId());
      return true;
    }

    try {
      boolean isVpnOrDataCenter = false;

      if (Boolean.TRUE.equals(psp.getBlockVpnAccess())
          && ipApiService.isVpnOrProxy(clientIpAddress)) {
        isVpnOrDataCenter = true;
        log.debug(
            "Request from IP {} detected as VPN, blocking PSP: {}", clientIpAddress, psp.getId());
      }

      if (Boolean.TRUE.equals(psp.getBlockDataCenterAccess())
          && ipApiService.isHostingOrDataCenter(clientIpAddress)) {
        isVpnOrDataCenter = true;
        log.debug(
            "Request from IP {} detected as Data Center, blocking PSP: {}",
            clientIpAddress,
            psp.getId());
      }

      return !isVpnOrDataCenter;
    } catch (Exception e) {
      log.warn("Error during access validation for PSP: {}, allowing request", psp.getId(), e);
      return true;
    }
  }

  @Override
  public List<Psp> filterValidAccess(List<Psp> psps, RequestInputDto request) {
    return psps.stream().filter(psp -> isAccessValid(psp, request)).collect(Collectors.toList());
  }
}
