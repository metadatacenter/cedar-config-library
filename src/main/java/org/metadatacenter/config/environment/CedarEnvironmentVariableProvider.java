package org.metadatacenter.config.environment;

import org.metadatacenter.model.SystemComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CedarEnvironmentVariableProvider {

  private static final Logger log = LoggerFactory.getLogger(CedarEnvironmentVariableProvider.class);

  public static Map<String, String> getFor(SystemComponent useCase) {
    Set<CedarEnvironmentVariable> neededVariables = CedarConfigEnvironmentDescriptor.getVariableNamesFor(useCase);
    Map<String, String> env = new LinkedHashMap<>();
    for (CedarEnvironmentVariable variable : CedarEnvironmentVariable.values()) {
      if (neededVariables.contains(variable)) {
        String value = System.getenv(variable.getName());
        env.put(variable.getName(), value);
      } else {
        if (variable.isNumeric()) {
          env.put(variable.getName(), "0");
          log.info("Setting default numeric value 0 for                                   : " + variable.getName());
        } else if (variable.isBoolean()) {
          env.put(variable.getName(), "false");
          log.info("Setting default boolean value false for                               : " + variable.getName());
        } else {
          String value = System.getenv(variable.getName());
          if (value != null) {
            log.info("Environment contains not needed variable, holding it back from sandbox: " + variable.getName());
          }
        }
      }
    }
    return env;
  }
}
