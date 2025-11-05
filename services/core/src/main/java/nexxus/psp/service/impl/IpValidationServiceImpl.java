package nexxus.psp.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import nexxus.psp.entity.Psp;
import nexxus.psp.service.IpValidationService;
import nexxus.request.dto.RequestInputDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpValidationServiceImpl implements IpValidationService {

  @Override
  public boolean isIpValid(Psp psp, RequestInputDto request) {
    String[] configuredIpAddresses = psp.getIpAddress();

    if (configuredIpAddresses == null || configuredIpAddresses.length == 0) {
      return true;
    }

    String clientIpAddress = request.getClientIpAddress();
    if (!StringUtils.hasText(clientIpAddress)) {
      log.debug("No client IP address provided, skipping IP validation for PSP: {}", psp.getId());
      return true;
    }

    try {
      InetAddress clientIp = InetAddress.getByName(clientIpAddress);
      boolean isExcluded =
          Arrays.stream(configuredIpAddresses)
              .anyMatch(configuredIp -> isIpExcluded(clientIp, configuredIp, psp.getId()));

      if (isExcluded) {
        log.debug(
            "Client IP {} matches configured exclusion IPs for PSP: {}",
            clientIpAddress,
            psp.getId());
        return false;
      }

      return true;
    } catch (UnknownHostException e) {
      log.warn(
          "Invalid client IP address format: {}, allowing PSP: {}", clientIpAddress, psp.getId());
      return true;
    }
  }

  @Override
  public List<Psp> filterValidIps(List<Psp> psps, RequestInputDto request) {
    return psps.stream().filter(psp -> isIpValid(psp, request)).collect(Collectors.toList());
  }

  private boolean isIpExcluded(InetAddress clientIp, String configuredIp, String pspId) {
    try {
      if (configuredIp.contains("/")) {
        return isIpInCidrRange(clientIp, configuredIp);
      } else {
        InetAddress configuredIpAddress = InetAddress.getByName(configuredIp);
        return clientIp.equals(configuredIpAddress);
      }
    } catch (UnknownHostException e) {
      log.warn("Invalid IP address format in PSP {} configuration: {}", pspId, configuredIp);
      return false;
    }
  }

  private boolean isIpInCidrRange(InetAddress clientIp, String cidrNotation) {
    try {
      String[] parts = cidrNotation.split("/");
      if (parts.length != 2) {
        log.warn("Invalid CIDR notation format: {}", cidrNotation);
        return false;
      }

      InetAddress networkAddress = InetAddress.getByName(parts[0]);
      int prefixLength = Integer.parseInt(parts[1]);

      byte[] networkBytes = networkAddress.getAddress();
      byte[] clientBytes = clientIp.getAddress();

      if (networkBytes.length != clientBytes.length) {
        return false;
      }

      int bytesToCheck = prefixLength / 8;
      int bitsToCheck = prefixLength % 8;

      for (int i = 0; i < bytesToCheck; i++) {
        if (networkBytes[i] != clientBytes[i]) {
          return false;
        }
      }

      if (bitsToCheck > 0 && bytesToCheck < networkBytes.length) {
        int mask = 0xFF << (8 - bitsToCheck);
        if ((networkBytes[bytesToCheck] & mask) != (clientBytes[bytesToCheck] & mask)) {
          return false;
        }
      }

      return true;
    } catch (Exception e) {
      log.warn("Error checking CIDR range {}: {}", cidrNotation, e.getMessage());
      return false;
    }
  }
}
