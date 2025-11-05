package connxt.permission.services;

public interface PermissionService {

  boolean hasPermission(String module, String action);

  void requirePermission(String module, String action);
}
