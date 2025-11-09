package connxt.flowtarget.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import connxt.flowtarget.dto.FlowTargetDto;
import connxt.flowtarget.entity.FlowTarget;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface FlowTargetMapper {

  FlowTargetDto toFlowTargetDto(FlowTarget flowTarget);

  FlowTarget toFlowTarget(FlowTargetDto flowTargetDto);

  void toUpdateFlowTarget(FlowTargetDto flowTargetDto, @MappingTarget FlowTarget flowTarget);
}
