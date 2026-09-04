package org.metadatacenter.config.environment;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The one place a secret is turned into something safe to show.
 *
 * <p>Two callers need this and they need it to agree: the boot-time sandbox report in
 * {@link CedarEnvironmentVariableLookup}, which writes to the service log, and the monitoring
 * server's environment and configuration pages, which serve over HTTP on the application
 * connector that nginx proxies to a public host. A masker per caller is a masker that drifts,
 * and the one that drifts is the one nobody is reading when it stops masking.
 *
 * <p>Masking keeps the first and last two characters so that a value can be told apart from a
 * different value of the same length — enough to answer "is this the same API key as staging"
 * without answering "what is the API key". Below {@link #MIN_LENGTH_FOR_PARTIAL_REVEAL}
 * characters nothing is revealed: on a short value those four characters are most of it, and the
 * earlier report showed a three-character secret in full.
 */
public final class CedarSecretMasker {

  /** How many characters stay visible at each end of a long enough value. */
  static final int VISIBLE_CHARS_PER_END = 2;

  /**
   * Shorter than this and the value is masked whole. Four characters would leak everything from a
   * five-character value; eight leaves at least half of it hidden.
   */
  static final int MIN_LENGTH_FOR_PARTIAL_REVEAL = 8;

  private static final char STAR = '*';

  /** Fixed width for a fully masked value, so its length is not readable from the mask. */
  private static final String FULLY_MASKED = "********";

  /**
   * Substrings that make a configuration key secret wherever it appears. The environment variables
   * carry their own {@link CedarEnvironmentVariable#isSecure()} flag and do not need this; the
   * resolved configuration tree has no such flag, so its keys are judged by name.
   */
  private static final String[] SECRET_KEY_FRAGMENTS = {
      "password", "passwd", "secret", "apikey", "api_key", "token", "salt", "credential",
      "privatekey", "private_key", "clientsecret", "accesskey", "access_key"
  };

  private CedarSecretMasker() {
  }

  /**
   * The value as it may be shown. {@code null} stays {@code null} — an unset variable and a masked
   * one are different facts and the report distinguishes them.
   */
  public static String mask(String value) {
    if (value == null) {
      return null;
    }
    if (value.isEmpty()) {
      return "";
    }
    if (value.length() < MIN_LENGTH_FOR_PARTIAL_REVEAL) {
      return FULLY_MASKED;
    }
    StringBuilder masked = new StringBuilder(value.length());
    masked.append(value, 0, VISIBLE_CHARS_PER_END);
    masked.append(String.valueOf(STAR).repeat(value.length() - 2 * VISIBLE_CHARS_PER_END));
    masked.append(value, value.length() - VISIBLE_CHARS_PER_END, value.length());
    return masked.toString();
  }

  /**
   * An unresolved configuration placeholder: the literal {@code ${NAME}} a relaxed substitution
   * leaves behind when the variable is not in the component's sandbox.
   */
  private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{[^}]*}");

  /**
   * The value masked, except for any unresolved placeholder in it.
   *
   * <p>A placeholder is not a secret. It is the name of a variable that did not arrive, which is the
   * single most useful thing the resolved-configuration report can say, and masking it produced
   * {@code ${**************************D}} — a string that protects nothing and has thrown away the
   * name. The variable name is public knowledge: it is in the descriptor, in the template, and in
   * the boot log of every service that declares it.
   *
   * <p>Literal text around a placeholder is still masked, because a value can be part secret and
   * part unresolved and only the placeholder half is safe to show.
   */
  public static String maskPreservingPlaceholders(String value) {
    if (value == null) {
      return null;
    }
    Matcher matcher = PLACEHOLDER.matcher(value);
    StringBuilder masked = new StringBuilder();
    int literalStart = 0;
    boolean found = false;
    while (matcher.find()) {
      found = true;
      masked.append(maskLiteralRun(value.substring(literalStart, matcher.start())));
      masked.append(matcher.group());
      literalStart = matcher.end();
    }
    if (!found) {
      return mask(value);
    }
    masked.append(maskLiteralRun(value.substring(literalStart)));
    return masked.toString();
  }

  /**
   * One run of literal text between placeholders.
   *
   * <p>Masked, unless it holds no letter or digit. A run of pure punctuation is a separator rather
   * than a value — the colon in {@code ${CEDAR_MONGO_HOST}:${CEDAR_MONGO_PORT}} — and masking it
   * turned one character into eight stars, which hid the shape of the value while protecting nothing
   * that could be a secret.
   */
  private static String maskLiteralRun(String run) {
    if (run.isEmpty() || run.chars().noneMatch(Character::isLetterOrDigit)) {
      return run;
    }
    return mask(run);
  }

  /** The value masked when {@code secret}, and unchanged otherwise. */
  public static String maskIf(boolean secret, String value) {
    return secret ? mask(value) : value;
  }

  /**
   * Whether a configuration key names something secret. Matched on the key alone, case- and
   * separator-insensitively, so {@code userPassword}, {@code user_password} and {@code PASSWORD}
   * all match.
   */
  public static boolean isSecretKey(String key) {
    if (key == null) {
      return false;
    }
    String normalized = key.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
    for (String fragment : SECRET_KEY_FRAGMENTS) {
      if (normalized.contains(fragment.replace("_", ""))) {
        return true;
      }
    }
    return false;
  }
}
