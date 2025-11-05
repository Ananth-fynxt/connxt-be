package nexxus.shared.db;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

public class PostgreSQLEnumType implements UserType<Enum<?>>, ParameterizedType {

  private Class<? extends Enum<?>> enumClass;

  @Override
  public void setParameterValues(Properties parameters) {
    String enumClassName = parameters.getProperty("enumClass");
    if (enumClassName != null) {
      try {
        @SuppressWarnings("unchecked")
        Class<? extends Enum<?>> clazz = (Class<? extends Enum<?>>) Class.forName(enumClassName);
        this.enumClass = clazz;
      } catch (ClassNotFoundException e) {
        throw new HibernateException("Enum class not found: " + enumClassName, e);
      }
    }
  }

  @Override
  public int getSqlType() {
    return Types.OTHER;
  }

  @Override
  public Class<Enum<?>> returnedClass() {
    @SuppressWarnings("unchecked")
    Class<Enum<?>> clazz = (Class<Enum<?>>) enumClass;
    return clazz;
  }

  @Override
  public boolean equals(Enum<?> x, Enum<?> y) throws HibernateException {
    return x == y || (x != null && x.equals(y));
  }

  @Override
  public int hashCode(Enum<?> x) throws HibernateException {
    return x == null ? 0 : x.hashCode();
  }

  @Override
  public Enum<?> nullSafeGet(
      ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
      throws SQLException {
    String value = rs.getString(position);
    if (rs.wasNull() || value == null) {
      return null;
    }

    if (enumClass == null) {
      throw new HibernateException("Enum class not set for PostgreSQLEnumType");
    }

    // Convert string value to enum
    for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
      // Check if the enum has a getValue() method (for enums with custom values)
      try {
        Object enumValue = enumConstant.getClass().getMethod("getValue").invoke(enumConstant);
        if (value.equals(enumValue.toString())) {
          return enumConstant;
        }
      } catch (Exception e) {
        // If getValue() method doesn't exist, fall back to name comparison
        if (value.equals(enumConstant.name())) {
          return enumConstant;
        }
      }
    }

    throw new HibernateException(
        "Unknown enum value: " + value + " for enum class: " + enumClass.getName());
  }

  @Override
  public void nullSafeSet(
      PreparedStatement st, Enum<?> value, int index, SharedSessionContractImplementor session)
      throws SQLException {
    if (value == null) {
      st.setNull(index, Types.OTHER);
    } else {
      // Check if the enum has a getValue() method
      try {
        Object enumValue = value.getClass().getMethod("getValue").invoke(value);
        st.setObject(index, enumValue.toString(), Types.OTHER);
      } catch (Exception e) {
        // If getValue() method doesn't exist, use the enum name
        st.setObject(index, value.name(), Types.OTHER);
      }
    }
  }

  @Override
  public Enum<?> deepCopy(Enum<?> value) throws HibernateException {
    return value;
  }

  @Override
  public boolean isMutable() {
    return false;
  }

  @Override
  public Serializable disassemble(Enum<?> value) throws HibernateException {
    return value;
  }

  @Override
  public Enum<?> assemble(Serializable cached, Object owner) throws HibernateException {
    return (Enum<?>) cached;
  }

  @Override
  public Enum<?> replace(Enum<?> detached, Enum<?> managed, Object owner)
      throws HibernateException {
    return detached;
  }
}
