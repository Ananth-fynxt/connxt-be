package connxt.psp.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import connxt.psp.dto.IpApiResponse;
import connxt.psp.service.IpApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpApiServiceImpl implements IpApiService {

  private static final String API_URL = "http://ip-api.com/json/";
  private static final String FIELDS_PARAM = "?fields=66846719";

  private final RestTemplate restTemplate;

  @Override
  @Cacheable(value = "ipApiCache", key = "#ipAddress", unless = "#result == null")
  public IpApiResponse getDetailsByIp(String ipAddress) {
    if (ipAddress == null || ipAddress.trim().isEmpty()) {
      return null;
    }

    try {
      String url = API_URL + ipAddress + FIELDS_PARAM;
      return restTemplate.getForObject(url, IpApiResponse.class);
    } catch (RestClientException e) {
      return null;
    }
  }

  @Override
  public IpApiResponse getDetailsByIpSync(String ipAddress) {
    if (ipAddress == null || ipAddress.trim().isEmpty()) {
      return null;
    }

    try {
      String url = API_URL + ipAddress + FIELDS_PARAM;
      return restTemplate.getForObject(url, IpApiResponse.class);
    } catch (RestClientException e) {
      return null;
    }
  }

  @Override
  public boolean isVpnOrProxy(String ipAddress) {
    IpApiResponse response = getDetailsByIpSync(ipAddress);
    return response != null
        && "success".equals(response.getStatus())
        && Boolean.TRUE.equals(response.getProxy());
  }

  @Override
  public boolean isHostingOrDataCenter(String ipAddress) {
    IpApiResponse response = getDetailsByIpSync(ipAddress);
    return response != null
        && "success".equals(response.getStatus())
        && Boolean.TRUE.equals(response.getHosting());
  }

  @Override
  public boolean isMobile(String ipAddress) {
    IpApiResponse response = getDetailsByIpSync(ipAddress);
    return response != null
        && "success".equals(response.getStatus())
        && Boolean.TRUE.equals(response.getMobile());
  }
}
