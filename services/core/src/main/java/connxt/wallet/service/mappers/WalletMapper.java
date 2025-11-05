package connxt.wallet.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import connxt.shared.db.mappers.MapperCoreConfig;
import connxt.wallet.dto.WalletDto;
import connxt.wallet.entity.Wallet;

@Mapper(config = MapperCoreConfig.class)
public interface WalletMapper {
  WalletDto toWalletDto(Wallet wallet);

  Wallet toWallet(WalletDto walletDto);

  void toUpdateWallet(WalletDto walletDto, @MappingTarget Wallet wallet);
}
