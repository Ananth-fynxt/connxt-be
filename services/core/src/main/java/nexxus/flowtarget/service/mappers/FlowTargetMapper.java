package nexxus.flowtarget.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import nexxus.flowtarget.dto.FlowTargetDto;
import nexxus.flowtarget.entity.FlowTarget;
import nexxus.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface FlowTargetMapper {

  FlowTargetDto toFlowTargetDto(FlowTarget flowTarget);

  FlowTarget toFlowTarget(FlowTargetDto flowTargetDto);

  void toUpdateFlowTarget(FlowTargetDto flowTargetDto, @MappingTarget FlowTarget flowTarget);
}
