package org.metadatacenter.server.jsonld;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** Sets the child order a container declares, which is the list the prune reads. */
final class ArrayNodeHelper {
  private ArrayNodeHelper() {
  }

  static void setOrder(ObjectNode container, String... childNames) {
    ArrayNode order = ((ObjectNode) container.get("_ui")).putArray("order");
    for (String name : childNames) {
      order.add(name);
    }
  }
}
