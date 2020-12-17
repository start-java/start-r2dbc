package tech.simter.start.springdatar2dbc.converter;

/**
 * A marker converter interface same with {@link org.springframework.core.convert.converter.Converter}.
 * <p>
 * Just for {@link R2dbcConfiguration} to register it automatically.
 *
 * @param <S> the source type
 * @param <T> the target type
 */
public interface Converter<S, T> extends org.springframework.core.convert.converter.Converter<S, T> {
}