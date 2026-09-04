package org.metadatacenter.config.environment;

/**
 * Whether a component can run without the variable.
 *
 * <p>A declared variable is normally required: {@link CedarEnvironmentVariableLookup} refuses to
 * build a configuration when one has no value, so a component that declares a database host and is
 * given none fails at boot instead of half-starting. That is right for coordinates and credentials
 * and wrong for a tuning knob. The worker's log aggregation jobs carry fifteen of those — batch
 * sizes, pause intervals, retention windows — each with a code default, and every one of them was
 * read with {@code System.getenv} straight past the configuration model, precisely because
 * declaring it would have made it mandatory.
 *
 * <p>{@code OPTIONAL} is what lets such a variable be declared honestly: the component reads it, the
 * environment need not supply it, and it becomes visible to the environment report instead of
 * existing only in the source of whatever reads it.
 */
public enum CedarEnvironmentVariableOptional {
  /** The component cannot run without it; an unset value fails the boot. */
  NO,
  /** The component reads it and falls back to its own default; an unset value is normal. */
  YES
}
