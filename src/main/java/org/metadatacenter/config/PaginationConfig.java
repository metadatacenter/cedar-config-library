package org.metadatacenter.config;

public class PaginationConfig {

  private int defaultPageSize;

  private int maxPageSize;

  private int maxOffset;

  private int maxResultWindow;

  public int getDefaultPageSize() {
    return defaultPageSize;
  }

  public int getMaxPageSize() {
    return maxPageSize;
  }

  public int getMaxOffset() {
    return maxOffset;
  }

  public int getMaxResultWindow() {
    return maxResultWindow;
  }
}
