package org.metadatacenter.server.jsonld;

import jakarta.ws.rs.BadRequestException;

/**
 * The refusal of an identifier a request supplied that cannot be resolved to a folder or an artifact.
 *
 * <p>A BadRequestException without an entity reached the client as a 400 whose body carried no
 * message. This library sits below the one that defines the shared error body, so the refusal cannot
 * carry that body itself. The shared exception mapper answers this type with a 400 whose body carries
 * the message, which must therefore say nothing a caller should not see.
 *
 * <p>It remains a BadRequestException, so code that catches one, and tests that expect one, see no
 * difference.
 */
public class UnresolvableIdentifierException extends BadRequestException {

  public UnresolvableIdentifierException(String message) {
    super(message);
  }

}
