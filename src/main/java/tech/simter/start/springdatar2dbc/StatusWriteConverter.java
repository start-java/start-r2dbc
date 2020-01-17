package tech.simter.start.springdatar2dbc;

import tech.simter.r2dbc.R2dbcCustomConverter;

public enum StatusWriteConverter implements R2dbcCustomConverter<Status, String> {
  Instance;

  @Override
  public String convert(Status source) {
    return source.name();
  }
}