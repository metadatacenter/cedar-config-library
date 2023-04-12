package org.metadatacenter.util;

import org.metadatacenter.config.CedarConfig;
import org.metadatacenter.server.security.model.user.CedarUserRepresentation;

public class CedarUserNameUtil {

  private CedarUserNameUtil() {

  }

  public static String getDisplayName(CedarConfig cedarConfig, CedarUserRepresentation ur) {
    if (ur != null) {
      String screenName = cedarConfig.getBlueprintUserProfile().getScreenNameTemplate();
      if (screenName != null) {
        screenName = replaceFirstAndLastName(screenName, ur);
      } else {
        screenName = "";
      }
      if (screenName.trim().isEmpty()) {
        screenName = ur.getEmail();
      }
      return screenName;
    } else {
      return null;
    }
  }

  public static String getHomeFolderDescription(CedarConfig cedarConfig, CedarUserRepresentation ur) {
    if (ur != null) {
      String homeDescription = cedarConfig.getBlueprintUserProfile().getHomeFolderDescriptionTemplate();
      if (homeDescription != null) {
        homeDescription = replaceFirstAndLastName(homeDescription, ur);
      } else {
        homeDescription = "";
      }
      return homeDescription;
    } else {
      return null;
    }
  }

  private static String replaceFirstAndLastName(String template, CedarUserRepresentation ur) {
    template = template.replace("{firstName}", ur.getFirstName() == null ? "" : ur.getFirstName());
    template = template.replace("{lastName}", ur.getLastName() == null ? "" : ur.getLastName());
    return template;
  }

}
