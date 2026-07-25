package org.metadatacenter.config.environment;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The single source of environment variables for the CEDAR configuration stack.
 *
 * <p>By default every read delegates to {@link System#getenv}. A test can install an override
 * map with {@link #setOverride(Map)}; from that point every CEDAR environment read in the JVM
 * is served from the override instead of the real process environment. This lets a test
 * redirect hosts and ports for a booted application without reflective mutation of the process
 * environment.
 *
 * <p>The override is test-only and process-global: it affects every reader in the same JVM,
 * so tests that install one share it, and production code must never call
 * {@link #setOverride(Map)}. The override replaces the process environment rather than merging
 * into it; a test that wants merge semantics copies {@code System.getenv()} into its map first.
 */
public final class CedarEnvironmentSource {

  private static volatile Map<String, String> override;

  private CedarEnvironmentSource() {
  }

  /**
   * Returns the value of the named variable from the active source: the override if one is
   * installed, the process environment otherwise.
   */
  public static String get(String name) {
    Map<String, String> active = override;
    return active != null ? active.get(name) : System.getenv(name);
  }

  /**
   * Returns the full variable map of the active source: the override if one is installed, the
   * process environment otherwise. The returned map is unmodifiable.
   */
  public static Map<String, String> getAll() {
    Map<String, String> active = override;
    return active != null ? active : System.getenv();
  }

  /**
   * Installs an override map, replacing the process environment as the source for all
   * subsequent reads. The map is copied; later changes to the argument have no effect.
   * Passing {@code null} is equivalent to {@link #clearOverride()}.
   */
  public static void setOverride(Map<String, String> environment) {
    override = environment == null ? null : Collections.unmodifiableMap(new LinkedHashMap<>(environment));
  }

  /**
   * Removes the override; subsequent reads come from the process environment again.
   */
  public static void clearOverride() {
    override = null;
  }

  public static boolean hasOverride() {
    return override != null;
  }
}
