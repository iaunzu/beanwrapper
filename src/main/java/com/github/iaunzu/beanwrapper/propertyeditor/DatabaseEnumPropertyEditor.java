package com.github.iaunzu.beanwrapper.propertyeditor;

import com.github.iaunzu.beanwrapper.dto.DatabaseClass;

public class DatabaseEnumPropertyEditor<E extends Enum<E> & DatabaseClass<T>, T> implements IPropertyEditor {

    private Class<E> clazz;

    public DatabaseEnumPropertyEditor(Class<E> clazz) {
	this.clazz = clazz;
    }

    @Override
    public E getValue(Object value) {
	return getEnumFromDatabaseValue((T) value, clazz);
    }

    public static <E extends Enum<E> & DatabaseClass<T>, T> E getEnumFromDatabaseValue(T value, Class<E> clazz) {
	for (E e : clazz.getEnumConstants()) {
	    if (e.getDatabaseValue().equals(value)) {
		return e;
	    }
	}
	return null;
    }
}
