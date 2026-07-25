package org.metadatacenter.config.environment;

import java.util.Map;

public class CedarEnvironmentUtil {

  public static void copy(CedarEnvironmentVariable variable, Map<String, String> map) {
    map.put(variable.getName(), CedarEnvironmentSource.get(variable.getName()));
  }
}
