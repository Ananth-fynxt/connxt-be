package nexxus.request.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import nexxus.request.dto.RequestInputDto;
import nexxus.request.entity.Request;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface RequestMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "flowActionId", source = "actionId")
  Request toRequest(RequestInputDto requestInputDto);
}
