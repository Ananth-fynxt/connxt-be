package nexxus.wallet.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import nexxus.shared.db.mappers.MapperCoreConfig;
import nexxus.wallet.dto.WalletDto;
import nexxus.wallet.entity.Wallet;

@Mapper(config = MapperCoreConfig.class)
public interface WalletMapper {
  WalletDto toWalletDto(Wallet wallet);

  Wallet toWallet(WalletDto walletDto);

  void toUpdateWallet(WalletDto walletDto, @MappingTarget Wallet wallet);
}
