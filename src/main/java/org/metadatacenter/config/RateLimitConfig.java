package org.metadatacenter.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/** Shared authenticated-user quotas. Redis holds usage, never authoritative policy. */
public class RateLimitConfig {
  @JsonProperty @NotNull @Pattern(regexp = "off|observe|enforce")
  private String mode = "observe";
  @JsonProperty @NotNull @Pattern(regexp = "[A-Za-z0-9_-]{1,64}")
  private String redisKeyPrefix = "CEDAR-RATE-LIMIT";
  @JsonProperty @Min(10) @Max(5000)
  private int redisTimeoutMillis = 100;
  @JsonProperty @Valid @NotNull private Policy total = new Policy(720, 40, "open");
  @JsonProperty @Valid @NotNull private Policy reads = new Policy(600, 30, "open");
  @JsonProperty @Valid @NotNull private Policy writes = new Policy(120, 10, "closed");

  public String getMode() { return mode; }
  public String getRedisKeyPrefix() { return redisKeyPrefix; }
  public int getRedisTimeoutMillis() { return redisTimeoutMillis; }
  public Policy getTotal() { return total; }
  public Policy getReads() { return reads; }
  public Policy getWrites() { return writes; }

  public static class Policy {
    @JsonProperty @Min(1) @Max(1000000) private int requestsPerMinute;
    @JsonProperty @Min(1) @Max(1000000) private int burst;
    @JsonProperty @NotNull @Pattern(regexp = "open|closed") private String failureMode;
    public Policy() { this(600, 30, "open"); }
    public Policy(int rate, int burst, String failureMode) {
      this.requestsPerMinute = rate;
      this.burst = burst;
      this.failureMode = failureMode;
    }
    public int getRequestsPerMinute() { return requestsPerMinute; }
    public int getBurst() { return burst; }
    public String getFailureMode() { return failureMode; }
  }
}
