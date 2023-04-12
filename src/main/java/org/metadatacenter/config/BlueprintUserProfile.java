package org.metadatacenter.config;

import org.metadatacenter.server.security.model.user.CedarSuperRole;
import org.metadatacenter.server.security.model.user.CedarUserRole;

import java.util.List;
import java.util.Map;

public class BlueprintUserProfile {

  private String screenNameTemplate;

  private String homeFolderDescriptionTemplate;

  private BlueprintDefaultAPIKey defaultAPIKey;

  private Map<CedarSuperRole, List<CedarUserRole>> defaultRoles;

  private BlueprintUIPreferences uiPreferences;

  public String getScreenNameTemplate() {
    return screenNameTemplate;
  }

  public String getHomeFolderDescriptionTemplate() {
    return homeFolderDescriptionTemplate;
  }

  public BlueprintDefaultAPIKey getDefaultAPIKey() {
    return defaultAPIKey;
  }

  public Map<CedarSuperRole, List<CedarUserRole>> getDefaultRoles() {
    return defaultRoles;
  }

  public BlueprintUIPreferences getUiPreferences() {
    return uiPreferences;
  }
}
