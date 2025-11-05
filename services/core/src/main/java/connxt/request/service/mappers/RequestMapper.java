package connxt.request.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import connxt.request.dto.RequestInputDto;
import connxt.request.entity.Request;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface RequestMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "flowActionId", source = "actionId")
  Request toRequest(RequestInputDto requestInputDto);
}
