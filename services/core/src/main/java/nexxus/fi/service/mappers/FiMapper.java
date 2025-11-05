package nexxus.fi.service.mappers;

import org.mapstruct.Mapper;

import nexxus.fi.dto.FiDto;
import nexxus.fi.entity.Fi;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface FiMapper {
  FiDto toFiDto(Fi fi);

  Fi toFi(FiDto fiDto);
}
