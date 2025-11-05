package connxt.fi.service.mappers;

import org.mapstruct.Mapper;

import connxt.fi.dto.FiDto;
import connxt.fi.entity.Fi;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface FiMapper {
  FiDto toFiDto(Fi fi);

  Fi toFi(FiDto fiDto);
}
