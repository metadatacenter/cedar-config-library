package org.metadatacenter.config.environment;

import org.metadatacenter.model.SystemComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * What one component's environment actually looks like, variable by variable.
 *
 * <p>This is the fact the boot-time log has always printed and nothing else could read. The log
 * is written once, to the file of whichever service wrote it, and answering "which of the fifteen
 * services is missing the variable" meant opening fifteen files on the box. The same report is
 * built here so that {@link CedarEnvironmentVariableLookup} can log it and the monitoring server
 * can serve it, from one implementation rather than two that agree until they don't.
 *
 * <p>Every value that leaves here is already masked. A caller cannot forget to mask, because
 * there is nothing unmasked to forget.
 */
public final class CedarEnvironmentReport {

  /**
   * Where a variable stands for one component. The distinction that matters is the middle one:
   * a variable this component reads and the environment does not supply is a misconfiguration,
   * while the same variable absent from a component that never reads it is by design.
   */
  public enum VariableState {
    /** Declared by this component, and the environment supplied a value. */
    SET,
    /** Declared by this component, and the environment supplied nothing. This breaks the boot. */
    DECLARED_BUT_UNSET,
    /**
     * Declared and optional, and the environment supplied nothing, so the component is running on
     * its own default. Distinct from {@code DECLARED_BUT_UNSET} because the two look identical from
     * the sandbox and mean opposite things: one is a service that could not start, the other is a
     * tuning knob nobody chose to turn. Reporting the worker's eleven unset log settings as the
     * first kind would put eleven faults on a page whose job is to make a real one stand out.
     */
    USING_DEFAULT,
    /** Not declared by this component, so its configuration is built without it. */
    NOT_DECLARED
  }

  /**
   * One variable as seen by one component.
   *
   * @param name                    the variable's name
   * @param state                   where it stands for this component
   * @param secure                  whether the variable is flagged secret, and so whether {@code value} is masked
   * @param type                    STRING, NUMERIC or BOOLEAN, as declared
   * @param value                   the value, masked when secure; {@code null} unless the state is {@code SET}
   * @param presentInHostEnvironment whether the host's process environment sets it at all — reported for
   *                                 every variable including undeclared ones, because "I set it and nothing
   *                                 changed" is the usual shape of an environment bug and this is the field
   *                                 that answers it. Only ever the boolean: a value undeclared by this
   *                                 component never leaves it, which is the point of the sandbox.
   */
  public record VariableEntry(
      String name,
      VariableState state,
      boolean secure,
      CedarEnvironmentVariableType type,
      String value,
      boolean presentInHostEnvironment) {
  }

  private CedarEnvironmentReport() {
  }

  /**
   * The report for a component, given the sandbox its configuration was built from.
   *
   * <p>The sandbox is not read for the state, only for the value. {@link CedarEnvironmentVariableProvider}
   * puts a placeholder {@code 0} or {@code false} into the sandbox for numeric and boolean variables the
   * component does not declare, so that the configuration template stays resolvable; a report that read
   * state from the sandbox would show those as configured, and a port the service never reads would look
   * like a port set to zero. The declaration table is the truth about what a component reads.
   */
  public static List<VariableEntry> forComponent(SystemComponent component, Map<String, String> sandbox) {
    Set<CedarEnvironmentVariable> declared = CedarConfigEnvironmentDescriptor.getVariableNamesFor(component);
    Map<String, String> hostEnvironment = CedarEnvironmentSource.getAll();

    List<VariableEntry> entries = new ArrayList<>();
    for (CedarEnvironmentVariable variable : CedarEnvironmentVariable.values()) {
      String name = variable.getName();
      boolean isDeclared = declared != null && declared.contains(variable);
      String sandboxValue = sandbox == null ? null : sandbox.get(name);

      VariableState state;
      String reportedValue;
      if (!isDeclared) {
        state = VariableState.NOT_DECLARED;
        reportedValue = null;
      } else if (sandboxValue == null) {
        state = variable.isOptional() ? VariableState.USING_DEFAULT : VariableState.DECLARED_BUT_UNSET;
        reportedValue = null;
      } else {
        state = VariableState.SET;
        reportedValue = CedarSecretMasker.maskIf(variable.isSecure(), sandboxValue);
      }

      entries.add(new VariableEntry(
          name,
          state,
          variable.isSecure(),
          typeOf(variable),
          reportedValue,
          hostEnvironment.get(name) != null));
    }
    return entries;
  }

  private static CedarEnvironmentVariableType typeOf(CedarEnvironmentVariable variable) {
    if (variable.isNumeric()) {
      return CedarEnvironmentVariableType.NUMERIC;
    }
    if (variable.isBoolean()) {
      return CedarEnvironmentVariableType.BOOLEAN;
    }
    return CedarEnvironmentVariableType.STRING;
  }
}
