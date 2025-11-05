package nexxus.flowtype.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import nexxus.flowtype.dto.FlowTypeDto;
import nexxus.flowtype.entity.FlowType;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface FlowTypeMapper {
  FlowTypeDto toFlowTypeDto(FlowType flowType);

  FlowType toFlowType(FlowTypeDto flowTypeDto);

  void toUpdateFlowType(FlowTypeDto flowTypeDto, @MappingTarget FlowType flowType);
}
