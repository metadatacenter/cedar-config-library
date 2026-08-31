package org.metadatacenter.config.environment;

import org.metadatacenter.model.SystemComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CedarEnvironmentVariableProvider {

  private static final Logger log = LoggerFactory.getLogger(CedarEnvironmentVariableProvider.class);

  /**
   * The environment one component's configuration is built from. A component sees the variables it
   * declares and no others: an undeclared variable is left out even when the process environment
   * sets it, so one component's configuration cannot be changed by a variable belonging to another.
   * Undeclared numeric and boolean variables still take a placeholder, because the configuration
   * template resolves every placeholder it names whether or not this component reads it.
   */
  public static Map<String, String> getFor(SystemComponent useCase) {
    Set<CedarEnvironmentVariable> neededVariables = CedarConfigEnvironmentDescriptor.getVariableNamesFor(useCase);
    Map<String, String> env = new LinkedHashMap<>();
    for (CedarEnvironmentVariable variable : CedarEnvironmentVariable.values()) {
      if (neededVariables.contains(variable)) {
        String value = CedarEnvironmentSource.get(variable.getName());
        if (value == null && variable.isBoolean()) {
          value = "false";
          log.info("{} is declared by {} but unset, so it defaults to false", variable.getName(), useCase);
        }
        env.put(variable.getName(), value);
      } else {
        if (variable.isNumeric()) {
          env.put(variable.getName(), "0");
          log.debug("{} is undeclared by {}; placeholder 0 keeps the template resolvable",
              variable.getName(), useCase);
        } else if (variable.isBoolean()) {
          env.put(variable.getName(), "false");
          log.debug("{} is undeclared by {}; placeholder false keeps the template resolvable",
              variable.getName(), useCase);
        } else {
          String value = CedarEnvironmentSource.get(variable.getName());
          if (value != null) {
            log.debug("{} is set in the environment but undeclared by {}, so its configuration is built "
                + "without it", variable.getName(), useCase);
          }
        }
      }
    }
    return env;
  }
}
