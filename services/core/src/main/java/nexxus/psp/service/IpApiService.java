package nexxus.psp.service;

import nexxus.psp.dto.IpApiResponse;

public interface IpApiService {

  IpApiResponse getDetailsByIp(String ipAddress);

  IpApiResponse getDetailsByIpSync(String ipAddress);

  boolean isVpnOrProxy(String ipAddress);

  boolean isHostingOrDataCenter(String ipAddress);

  boolean isMobile(String ipAddress);
}
