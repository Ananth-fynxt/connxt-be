package connxt.denovm.service.mappers;

import java.util.HashMap;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import connxt.denovm.dto.DenoVMExecutionContext;
import connxt.denovm.dto.DenoVMRequest;

/** MapStruct mapper for Deno VM DTOs */
@Mapper(componentModel = "spring")
public interface DenoVMMapper {

  /**
   * Map DenoVMRequest to DenoVMExecutionContext
   *
   * @param request The VM request
   * @param scriptFile The script file path
   * @return The execution context
   */
  @Mapping(target = "file", source = "scriptFile")
  DenoVMExecutionContext toExecutionContext(DenoVMRequest request, String scriptFile);

  /**
   * Map execution context to VM request
   *
   * @param context The execution context
   * @return The VM request
   */
  @Mapping(target = "code", ignore = true) // code is not available in execution context
  @Mapping(target = "data", source = "data", qualifiedByName = "mapData")
  DenoVMRequest toRequest(DenoVMExecutionContext context);

  /** Custom mapping method for Object to Map<String, Object> */
  @org.mapstruct.Named("mapData")
  default java.util.Map<String, Object> mapData(Object data) {
    if (data instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> map = (Map<String, Object>) data;
      return map;
    }
    return new HashMap<>();
  }
}
