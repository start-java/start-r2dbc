package tech.simter.start.springdatar2dbc;

import tech.simter.r2dbc.R2dbcCustomConverter;

public enum StatusReadConverter implements R2dbcCustomConverter<String, Status> {
  Instance;

  @Override
  public Status convert(String source) {
    return Status.valueOf(source);
  }
}