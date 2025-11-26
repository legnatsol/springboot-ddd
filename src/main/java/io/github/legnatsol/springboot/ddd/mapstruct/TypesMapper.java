package io.github.legnatsol.springboot.ddd.mapstruct;

import io.github.legnatsol.springboot.ddd.domain.EnumObject;
import io.github.legnatsol.springboot.ddd.domain.Identifier;
import io.github.legnatsol.springboot.ddd.domain.ValueObject;
import org.mapstruct.TargetType;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Optional;

/**
 * Types Mapper
 */
public interface TypesMapper {

    /**
     * Long -> Date
     * @param time  milliseconds timestamp
     * @return      Date
     */
    static Date mapToDate(Long time) {
        return Optional.ofNullable(time).map(Date::new).orElse(null);
    }

    /**
     * Date -> Long
     * @param value Date
     * @return      milliseconds timestamp
     */
    static Long mapFromDate(Date value) {
        return Optional.ofNullable(value).map(Date::getTime).orElse(null);
    }

    /**
     * Identifier -> Value
     * @param idObject      identifier
     * @return              value
     * @param <V>           identifier type
     * @param <T>           value type
     */
    static  <V extends Identifier<T>, T> T mapToId(V idObject) {
        return idObject == null ? null : idObject.getValue();
    }

    /**
     * Value -> Identifier
     * @param value                 标识值
     * @param valueObjectClass      标识类型
     * @return                      标识
     * @param <V>                   标识类型
     * @param <T>                   值类型
     */
    static  <V extends Identifier<T>, T> V mapFromId(T value, @TargetType Class<V> valueObjectClass) {
        if (value == null) {
            return null;
        }

        try {
            return valueObjectClass.getDeclaredConstructor(value.getClass()).newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ValueObject -> Value
     * @param valueObject   value object
     * @return              value
     * @param <V>           value object type
     * @param <T>           value type
     */
    static <V extends ValueObject<T>, T> T mapToValue(V valueObject) {
        return valueObject == null ? null : valueObject.getValue();
    }

    /**
     * Value -> ValueObject
     * @param value                 value
     * @param valueObjectClass      value object class
     * @return                      value object
     * @param <V>                   value object type
     * @param <T>                   value type
     */
    static <V extends ValueObject<T>, T> V mapFromValue(T value, @TargetType Class<V> valueObjectClass) {
        if (value == null) {
            return null;
        }

        try {
            return valueObjectClass.getDeclaredConstructor(value.getClass()).newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enum -> Value
     * @param enumObject    enum object
     * @return              value
     * @param <E>           enum type
     * @param <T>           value type
     */
    static <E extends EnumObject<T>, T> T mapToValue(E enumObject) {
        return enumObject == null ? null : enumObject.getValue();
    }

    /**
     * Value -> Enum
     * @param value         enum value
     * @param enumClass     enum class
     * @return              Enum
     * @param <E>           enum type
     * @param <T>           value type
     */
    static <E extends EnumObject<T>, T> E mapToEnum(T value, @TargetType Class<E> enumClass) {
        if (value == null) {
            return null;
        }

        for (E e : enumClass.getEnumConstants()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
