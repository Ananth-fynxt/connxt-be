package connxt.transaction.dto;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Sort;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TransactionSearchCriteria {

  @Min(0)
  private Integer page;

  @Min(1)
  private Integer size;

  private String sortBy;

  private Sort.Direction sortDirection;

  private Map<String, Object> filters = new HashMap<>();
}
