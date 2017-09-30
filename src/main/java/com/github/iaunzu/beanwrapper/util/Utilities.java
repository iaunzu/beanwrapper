package com.github.iaunzu.beanwrapper.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.iaunzu.beanwrapper.dto.DatabaseClass;
import com.github.iaunzu.beanwrapper.exception.ReflectionException;

public enum Utilities {
    ;

    /**
     * Compara objetos por valor para las clases Boolean, Number, Date y
     * Calendar. Si los objetos no son de ninguna de estas clases, invoca al
     * método equals(Object obj).
     * 
     * @param obj1
     * @param obj2
     * @return true si obj1 y obj2 son null, sus valores como Boolean, Number,
     *         Date o Calendar son iguales o si equals(Object obj) devuelve
     *         true. En caso contrario, devuelve false.
     */
    public static boolean equalObjects(Object obj1, Object obj2) {
	if (obj1 == null && obj2 == null) {
	    return true;
	} else if (obj1 == null || obj2 == null) {
	    return false;
	}
	if (isAssignable(Number.class, obj1, obj2)) {
	    Number number1 = (Number) obj1;
	    Number number2 = (Number) obj2;
	    return number1.doubleValue() == number2.doubleValue();
	} else if (isAssignable(CharSequence.class, obj1, obj2)) {
	    CharSequence cs1 = (CharSequence) obj1;
	    CharSequence cs2 = (CharSequence) obj2;
	    return cs2.equals(cs1);
	} else if (isAssignable(Date.class, obj1, obj2)) {
	    Date date1 = (Date) obj1;
	    Date date2 = (Date) obj2;
	    return date1.getTime() == date2.getTime();
	} else if (isAssignable(Calendar.class, obj1, obj2)) {
	    Calendar date1 = (Calendar) obj1;
	    Calendar date2 = (Calendar) obj2;
	    return date1.getTime().getTime() == date2.getTime().getTime();
	} else if (isAssignable(Comparable.class, obj1, obj2)) {
	    @SuppressWarnings("unchecked")
	    Comparable<Object> comp1 = (Comparable<Object>) obj1;
	    @SuppressWarnings("unchecked")
	    Comparable<Object> comp2 = (Comparable<Object>) obj2;
	    return comp1.compareTo(comp2) == 0;
	}
	return obj1.equals(obj2);
    }

    private static boolean isAssignable(Class<?> clazz, Object obj1, Object obj2) {
	return clazz.isAssignableFrom(obj1.getClass()) && clazz.isAssignableFrom(obj2.getClass());
    }

    /**
     * Devuelve la primera enumeración de tipo enumType cuyo valor devuelto por
     * <code>getIndex()</code> coincide con value. Si no lo encuentra, devuelve
     * null.
     * 
     * @param value
     * @param enumType
     * @return primera enumeración de tipo enumType cuyo método getIndex()
     *         devuelve value. Si no existe, devuelve null.
     */
    public static <E extends Enum<E> & DatabaseClass<T>, T> E getEnumFromIndexValue(final T value, final Class<E> enumType) {
	for (final E e : enumType.getEnumConstants()) {
	    if (equalObjects(e.getDatabaseValue(), value)) {
		return e;
	    }
	}
	return null;
    }

    public static String removeQuotes(String str) {
	if ((str.startsWith("\"") && str.endsWith("\"")) || ((str.startsWith("'") && str.endsWith("'")))) {
	    return str.substring(1, str.length() - 1);
	}
	return str;
    }

    public static boolean hasSeparator(String propertyName) {
	return propertyName.indexOf(".") != -1 || propertyName.indexOf("[") != -1;
    }

    public static String getPathFromPropertyName(String propertyName) {
	int dotPosition = propertyName.indexOf(".");
	int bracketPosition = propertyName.indexOf("[");
	if (dotPosition == -1 && bracketPosition == -1) {
	    return propertyName;
	}
	if (bracketPosition == 0) {
	    return propertyName.substring(1, propertyName.indexOf("]"));
	}
	if (bracketPosition == -1 || (dotPosition != -1 && dotPosition < bracketPosition)) {
	    return propertyName.substring(0, dotPosition);
	} else {
	    return propertyName.substring(0, bracketPosition);
	}
    }

    public static String getSubPropertyFromPropertyName(String propertyName) {
	int dotPosition = propertyName.indexOf(".");
	int bracketPosition = propertyName.indexOf("[");
	if (bracketPosition == 0) {
	    String subProperty = propertyName.substring(propertyName.indexOf("]") + 1);
	    return subProperty.startsWith(".") ? subProperty.substring(1) : subProperty;
	}
	if (dotPosition == -1 && bracketPosition == -1) {
	    return "";
	}
	if (bracketPosition == -1) {
	    return propertyName.substring(dotPosition + 1);
	} else {
	    return propertyName.substring(bracketPosition);
	}
    }

    public static Object lazyListGetter(List<Object> list, int index) {
	autoPopulateList(list, index);
	return list.get(index);
    }

    public static void lazyListSetter(List<Object> list, int index, Object element) {
	autoPopulateList(list, index);
	list.set(index, element);
    }

    private static void autoPopulateList(List<Object> list, int index) {
	for (int i = list.size(); i <= index; i++) {
	    list.add(null);
	}
    }

    // ** Getters y setters **//
    public static Class<?> getPropertyType(Object obj, String propertyName) throws ReflectionException {
	Method propertyGetter = getGetter(obj.getClass(), propertyName);
	return propertyGetter.getReturnType();
    }

    public static Class<?>[] getPropertyTypedClassGenericTypes(Object obj, String propertyName) {
	Class<? extends Object> clazz = obj.getClass();
	if (List.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
	    return null;
	}
	Method propertyGetter = getGetter(clazz, propertyName);
	Class<?> returnType = propertyGetter.getReturnType();
	if (List.class.isAssignableFrom(returnType) || Map.class.isAssignableFrom(returnType)) {
	    ParameterizedType type = (ParameterizedType) propertyGetter.getGenericReturnType();
	    Type[] actualTypeArguments = type.getActualTypeArguments();
	    Class<?>[] classes = new Class<?>[actualTypeArguments.length];
	    for (int i = 0; i < actualTypeArguments.length; i++) {
		classes[i] = (Class<?>) actualTypeArguments[i];
	    }
	    return classes;
	}
	return null;

    }

    public static Method getGetter(Class<?> clazz, String propertyName) throws ReflectionException {
	try {
	    return getPropertyDescriptor(clazz, propertyName).getReadMethod();
	} catch (IntrospectionException e) {
	    throw new ReflectionException(e);
	}
    }

    public static Method getSetter(Class<?> clazz, String propertyName) throws ReflectionException {
	try {
	    return getPropertyDescriptor(clazz, propertyName).getWriteMethod();
	} catch (IntrospectionException e) {
	    throw new ReflectionException(e);
	}
    }

    private static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) throws IntrospectionException {
	// TODO: cachear propertyDescriptor
	return new PropertyDescriptor(propertyName, clazz);
    }

}
