package com.furkanbegen.routes.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class TokenResolver implements Serializable {

  private ExpirationTime expirationTime;

  @Data
  public static class ExpirationTime implements Serializable {
    private Long duration;
    private ChronoUnit unit;
  }

  @Data
  public static class RefreshExpirationTime implements Serializable {
    private Long duration;
    private ChronoUnit unit;
  }

  public Duration getTokenExpirationDuration() {
    return Duration.of(expirationTime.duration, expirationTime.unit);
  }
}
