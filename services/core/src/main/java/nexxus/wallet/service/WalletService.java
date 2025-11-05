package nexxus.wallet.service;

import java.util.List;

import nexxus.wallet.dto.WalletDto;

public interface WalletService {

  void createWalletsForCustomer(
      String brandId, String environmentId, String brandCustomerId, String[] currencies);

  List<WalletDto> walletDetails(String brandId, String environmentId, String customerId);
}
