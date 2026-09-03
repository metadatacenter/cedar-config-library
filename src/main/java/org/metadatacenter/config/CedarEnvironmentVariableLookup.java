package org.metadatacenter.config;

import io.dropwizard.configuration.UndefinedEnvironmentVariableException;
import org.apache.commons.text.lookup.StringLookup;
import org.metadatacenter.config.environment.CedarEnvironmentVariable;
import org.metadatacenter.config.environment.CedarSecretMasker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CedarEnvironmentVariableLookup implements StringLookup {

  private enum VariableStatus {
    PRESENT_WITH_VALUE, PRESENT_WITHOUT_VALUE, NEEDED_NOT_INCLUDED, OPTIONAL_NOT_SUPPLIED
  }

  private static final Logger log = LoggerFactory.getLogger(CedarEnvironmentVariableLookup.class);

  private final boolean strict;
  private final Map<String, String> environment;
  private static final String SPACES = "                                        ";

  public CedarEnvironmentVariableLookup(Map<String, String> environment, boolean strict) {
    this.environment = environment;
    this.strict = strict;

    Map<String, VariableStatus> status = new LinkedHashMap<>();
    List<String> namesWithNullValue = new ArrayList<>();
    for (CedarEnvironmentVariable ev : CedarEnvironmentVariable.values()) {
      String name = ev.getName();
      if (!environment.containsKey(name)) {
        // An optional variable the provider left out is the component falling back to its own
        // default, not a variable that failed to arrive, so it is not reported as missing here.
        status.put(name, ev.isOptional()
            ? VariableStatus.OPTIONAL_NOT_SUPPLIED
            : VariableStatus.NEEDED_NOT_INCLUDED);
      } else {
        String v = environment.get(name);
        if (v == null) {
          status.put(name, VariableStatus.PRESENT_WITHOUT_VALUE);
          namesWithNullValue.add(name);
        } else {
          status.put(name, VariableStatus.PRESENT_WITH_VALUE);
        }
      }
    }

    log.info("----------------------------------------------------------------------------------------");
    log.info("------------------------- Environment variable sandbox ---------------------------------");
    log.info("With values: ---------------------------------------------------------------------------");
    for (String name : status.keySet()) {
      VariableStatus stat = status.get(name);
      if (stat == VariableStatus.PRESENT_WITH_VALUE) {
        StringBuilder sb = new StringBuilder();
        sb.append("---- ").append(name);
        int spacePos = name.length();
        if (spacePos > SPACES.length()) {
          spacePos = SPACES.length();
        }
        sb.append(SPACES.substring(spacePos));
        sb.append(":");
        String value = environment.get(name);
        CedarEnvironmentVariable var = CedarEnvironmentVariable.forName(name);
        if (var != null) {
          // Masked by the shared masker rather than here. This loop kept the first and last two
          // characters of a secret whatever its length, which on a value of three characters or
          // fewer is the whole value; the masker refuses to reveal anything that short.
          sb.append(CedarSecretMasker.maskIf(var.isSecure(), value));
        }
        log.info(sb.toString());
      }
    }
    log.info("Without values: ------------------------------------------------------------------------");
    for (String name : status.keySet()) {
      VariableStatus stat = status.get(name);
      if (stat == VariableStatus.PRESENT_WITHOUT_VALUE) {
        log.info("---- " + name);
      }
    }
    log.info("Optional, not supplied - the component's own default applies: --------------------------");
    for (String name : status.keySet()) {
      if (status.get(name) == VariableStatus.OPTIONAL_NOT_SUPPLIED) {
        log.info("---- " + name);
      }
    }
    log.info("Not included in this sandbox: ----------------------------------------------------------");
    for (String name : status.keySet()) {
      VariableStatus stat = status.get(name);
      if (stat == VariableStatus.NEEDED_NOT_INCLUDED) {
        log.info("---- " + name);
      }
    }
    log.info("----------------------------------------------------------------------------------------");
    if (!namesWithNullValue.isEmpty()) {
      throw new UndefinedEnvironmentVariableException("The following environment variables are expected to be present" +
          " with a non-null value in the sandbox: " +
          namesWithNullValue +
          " The application can not continue under these circumstances!");
    }
  }

  @Override
  public String lookup(String key) {
    String value = environment.get(key);
    if (value == null) {
      if (this.strict) {
        throw new UndefinedEnvironmentVariableException("The environment variable '" + key + "' is not defined; " +
            "could not substitute the expression '${" + key + "}'!");
      } else {
        log.debug("Environment variable missing, but we are in relaxed mode: '" + key + "'");
      }
    }
    return value;
  }
}
