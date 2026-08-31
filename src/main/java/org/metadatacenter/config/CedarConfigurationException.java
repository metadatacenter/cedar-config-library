package org.metadatacenter.config;

/**
 * Signals that a CEDAR configuration file could not be read.
 *
 * <p>Loading the configuration used to end the process: each failure logged one line and called
 * {@code System.exit}. That put the decision to terminate in a library, gave the caller no way to
 * report the failure itself, and left the JVM to die at whatever point the logging backend had
 * reached. It also made the failure untestable, since a test JVM cannot survive it.
 *
 * <p>Throwing instead changes nothing for a running service. The configuration is built from
 * {@code CedarMicroserviceApplication.initialize}, so the exception reaches Dropwizard's
 * {@code Application.run} and the process still exits non-zero, now with the failing file named and
 * the parser's own message attached.
 */
public class CedarConfigurationException extends RuntimeException {

  public CedarConfigurationException(String configFileName, Throwable cause) {
    super("Could not read the CEDAR configuration file " + configFileName, cause);
  }

}
