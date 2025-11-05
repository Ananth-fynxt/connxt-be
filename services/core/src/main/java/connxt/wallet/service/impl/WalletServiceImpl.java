package connxt.wallet.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import connxt.wallet.dto.WalletDto;
import connxt.wallet.entity.Wallet;
import connxt.wallet.repository.WalletRepository;
import connxt.wallet.service.WalletService;
import connxt.wallet.service.mappers.WalletMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

  private final WalletRepository walletRepository;
  private final WalletMapper walletMapper;

  @Override
  @Transactional
  public void createWalletsForCustomer(
      String brandId, String environmentId, String brandCustomerId, String[] currencies) {
    for (String currency : currencies) {
      if (!walletRepository.existsByBrandCustomerIdAndCurrency(brandCustomerId, currency)) {
        WalletDto walletDto =
            WalletDto.builder()
                .brandId(brandId)
                .environmentId(environmentId)
                .brandCustomerId(brandCustomerId)
                .currency(currency)
                .name(currency + " Wallet")
                .build();
        createWallet(walletDto);
      }
    }
  }

  private void createWallet(WalletDto dto) {
    Wallet wallet = walletMapper.toWallet(dto);
    walletRepository.save(wallet);
  }

  @Override
  public List<WalletDto> walletDetails(String brandId, String environmentId, String customerId) {
    List<Wallet> wallets =
        walletRepository.findWalletsByBrandIdAndEnvironmentIdAndBrandCustomerId(
            brandId, environmentId, customerId);
    List<WalletDto> walletDtos = wallets.stream().map(walletMapper::toWalletDto).toList();
    return walletDtos;
  }
}
