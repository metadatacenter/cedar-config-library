package org.metadatacenter.config.environment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CedarSecretMaskerTest {

  @Test
  void keepsTheEndsOfALongEnoughValueSoTwoServicesCanBeCompared() {
    assertEquals("hu*******23", CedarSecretMasker.mask("hunter2xx23"));
  }

  @Test
  void revealsNothingOfAShortValue() {
    // The earlier masker kept the first and last two characters whatever the length, so a value of
    // three characters or fewer came through whole.
    assertEquals("********", CedarSecretMasker.mask("abc"));
    assertEquals("********", CedarSecretMasker.mask("a"));
  }

  @Test
  void distinguishesAnEmptyValueFromAnAbsentOne() {
    assertEquals("", CedarSecretMasker.mask(""));
    assertEquals(null, CedarSecretMasker.mask(null));
  }

  @Test
  void namesASecretKeyWhateverItsSeparatorsAndCase() {
    assertTrue(CedarSecretMasker.isSecretKey("password"));
    assertTrue(CedarSecretMasker.isSecretKey("userPassword"));
    assertTrue(CedarSecretMasker.isSecretKey("USER_PASSWORD"));
    assertTrue(CedarSecretMasker.isSecretKey("client-secret"));
    assertTrue(CedarSecretMasker.isSecretKey("apiKey"));
    assertFalse(CedarSecretMasker.isSecretKey("host"));
    assertFalse(CedarSecretMasker.isSecretKey("submissionDirectory"));
  }

  /**
   * The bug this method exists for. A service that does not declare the variable leaves the template's
   * placeholder in place, and masking it by key returned {@code ${**************************D}} — the
   * variable name gone, and nothing protected, because a placeholder holds no secret.
   */
  @Test
  void keepsAnUnresolvedPlaceholderIntact() {
    assertEquals("${CEDAR_NCBI_SRA_FTP_PASSWORD}",
        CedarSecretMasker.maskPreservingPlaceholders("${CEDAR_NCBI_SRA_FTP_PASSWORD}"));
  }

  @Test
  void masksARealValueEvenThroughThePlaceholderAwarePath() {
    assertEquals("hu*******23", CedarSecretMasker.maskPreservingPlaceholders("hunter2xx23"));
  }

  @Test
  void masksTheLiteralTextAroundAPlaceholderButNotThePlaceholder() {
    // A value can be part resolved secret and part unresolved. Only the placeholder half is safe.
    assertEquals("********${CEDAR_MONGO_APP_USER_PASSWORD}********",
        CedarSecretMasker.maskPreservingPlaceholders("secret1${CEDAR_MONGO_APP_USER_PASSWORD}secret2"));
  }

  /** The separator between two placeholders is punctuation, not a value to hide. */
  @Test
  void handlesSeveralPlaceholdersInOneValue() {
    assertEquals("${CEDAR_MONGO_HOST}:${CEDAR_MONGO_PORT}",
        CedarSecretMasker.maskPreservingPlaceholders("${CEDAR_MONGO_HOST}:${CEDAR_MONGO_PORT}"));
  }
}
