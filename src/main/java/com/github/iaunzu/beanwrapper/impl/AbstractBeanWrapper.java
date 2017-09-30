package com.github.iaunzu.beanwrapper.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.iaunzu.beanwrapper.IBeanWrapper;
import com.github.iaunzu.beanwrapper.dto.DatabaseClass;
import com.github.iaunzu.beanwrapper.exception.ReflectionException;
import com.github.iaunzu.beanwrapper.propertyeditor.DatabaseEnumPropertyEditor;
import com.github.iaunzu.beanwrapper.propertyeditor.IPropertyEditor;
import com.github.iaunzu.beanwrapper.util.Utilities;

public abstract class AbstractBeanWrapper implements IBeanWrapper {

    protected final Map<PropEditorKey, IPropertyEditor> propertyEditors = new HashMap<PropEditorKey, IPropertyEditor>();

    static {
    }

    private static final Class<? extends ArrayList> defaultListClass = ArrayList.class;
    private static final Class<? extends HashMap> defaultMapClass = HashMap.class;

    @Override
    public IPropertyEditor getPropertyEditor(Class<?> clazz) {
	return getPropertyEditor(clazz, null);
    }

    @Override
    public IPropertyEditor getPropertyEditor(Class<?> clazz, String propertyName) {
	IPropertyEditor propertyEditor = propertyEditors.get(new PropEditorKey(clazz, propertyName));
	if (propertyEditor == null) {
	    if (propertyName != null) {
		// Buscar ahora genérico
		propertyEditor = propertyEditors.get(new PropEditorKey(clazz, null));
	    }
	    if (DatabaseClass.class.isAssignableFrom(clazz) && Enum.class.isAssignableFrom(clazz)) {
		propertyEditor = new DatabaseEnumPropertyEditor(clazz);
		addPropertyEditor(clazz, propertyName, propertyEditor);
	    }
	}
	return propertyEditor;
    }

    @Override
    public void addPropertyEditor(Class<?> clazz, IPropertyEditor propertyEditor) {
	addPropertyEditor(clazz, null, propertyEditor);
    }

    @Override
    public void addPropertyEditor(Class<?> clazz, String propertyName, IPropertyEditor propertyEditor) {
	propertyEditors.put(new PropEditorKey(clazz, propertyName), propertyEditor);
    }

    // ** GETVALUE **/

    protected Object getValue(Object obj, String propertyName) {
	return getValue(obj, propertyName, null, propertyName);
    }

    private Object getValue(Object obj, String propertyName, Class<?> genericListClass, String fullPath) {
	if (!Utilities.hasSeparator(propertyName)) {
	    return getPropertyNameValue(obj, propertyName);
	} else {
	    String path = Utilities.getPathFromPropertyName(propertyName);
	    String property = Utilities.getSubPropertyFromPropertyName(propertyName);
	    Class<?>[] propertyGenericTypes = Utilities.getPropertyTypedClassGenericTypes(obj, path);
	    Object pathObj = getPropertyPathValue(obj, path, false, propertyGenericTypes, fullPath);
	    return property.equals("") ? pathObj : getValue(pathObj, property);
	}
    }

    private Object getPropertyPathValue(Object obj, String path, boolean initIfNull, Class<?>[] propertyGenericTypes, String fullPath)
	    throws ReflectionException {
	Object result = null;
	path = Utilities.removeQuotes(path);
	if (List.class.isAssignableFrom(obj.getClass())) {
	    List<Object> objList = (List<Object>) obj;
	    Integer index = Integer.valueOf(path);
	    if (objList.size() > index) {
		result = objList.get(index);
	    }
	    if (initIfNull && result == null) {
		result = newInstance(propertyGenericTypes[0]);
		Utilities.lazyListSetter(objList, index, result);
	    }
	} else if (Map.class.isAssignableFrom(obj.getClass())) {
	    Map objMap = (Map) obj;
	    result = objMap.get(path);
	    if (initIfNull && result == null) {
		result = newInstance(propertyGenericTypes[1]);
		objMap.put(path, result);
	    }
	} else {
	    result = getPropertyNameValue(obj, path);
	    if (initIfNull && result == null) {
		result = newInstance(Utilities.getPropertyType(obj, path));
		setPropertyNameValue(obj, path, result, null, fullPath);
	    }
	}
	return result;
    }

    private static Object getPropertyNameValue(Object obj, String propertyName) throws ReflectionException {
	Method propertyGetter = Utilities.getGetter(obj.getClass(), propertyName);
	try {
	    return propertyGetter.invoke(obj);
	} catch (Exception e) {
	    throw new ReflectionException(e);
	}
    }

    // ** SETVALUE **//

    protected <T> void setValue(Object obj, String propertyName, T value) throws ReflectionException {
	setValue(obj, propertyName, value, null, propertyName);
    }

    private <T> void setValue(Object obj, String propertyName, T value, Class<?>[] propertyGenericTypes, String fullPath) throws ReflectionException {

	if (!Utilities.hasSeparator(propertyName)) {
	    setPropertyNameValue(obj, propertyName, value, propertyGenericTypes, fullPath);
	    return;
	} else {
	    String path = Utilities.getPathFromPropertyName(propertyName);
	    String property = Utilities.getSubPropertyFromPropertyName(propertyName);
	    if (property.equals("")) {
		setPropertyNameValue(obj, path, value, propertyGenericTypes, fullPath);
		return;
	    }
	    Object pathObj = getPropertyPathValue(obj, path, true, propertyGenericTypes, fullPath);
	    Class<?>[] propertyListGenericType = Utilities.getPropertyTypedClassGenericTypes(obj, path);
	    setValue(pathObj, property, value, propertyListGenericType, fullPath);
	    return;
	}
    }

    private void setPropertyNameValue(Object obj, String propertyName, Object value, Class<?>[] propertyGenericTypes, String fullPath)
	    throws ReflectionException {
	propertyName = Utilities.removeQuotes(propertyName);
	if (List.class.isAssignableFrom(obj.getClass())) {
	    List<Object> objList = (List<Object>) obj;
	    Integer index = null;
	    if (propertyName.matches("^\\d+$")) {
		try {
		    index = Integer.valueOf(propertyName);
		} catch (NumberFormatException e) {
		    // Nothing
		}
	    }
	    if (index != null) {
		value = convertIfNecessary(value, propertyGenericTypes[0], fullPath);
		Utilities.lazyListSetter(objList, index, value);
		return;
	    } else {
		obj = Utilities.lazyListGetter(objList, 0);
		if (obj == null) {
		    obj = newInstance(propertyGenericTypes[0]);
		    Utilities.lazyListSetter(objList, 0, obj);
		}
		value = convertIfNecessary(value, propertyGenericTypes[0], fullPath);
		setPropertyNameValue(obj, propertyName, value, null, fullPath);
		return;
	    }
	} else if (Map.class.isAssignableFrom(obj.getClass())) {
	    String prop = propertyName;
	    value = convertIfNecessary(value, propertyGenericTypes[1], fullPath);
	    ((Map) obj).put(prop, value);
	    return;
	}

	Method propertySetter = Utilities.getSetter(obj.getClass(), propertyName);
	try {
	    Class<?> propertyType = propertySetter.getParameterTypes()[0];
	    value = convertIfNecessary(value, propertyType, fullPath);
	    propertySetter.invoke(obj, value);
	} catch (Exception e) {
	    throw new ReflectionException(e);
	}
    }

    private Object convertIfNecessary(Object value, Class<?> clazz, String fullPath) {
	IPropertyEditor propertyEditor = getPropertyEditor(clazz, fullPath);
	if (propertyEditor != null) {
	    return propertyEditor.getValue(value);
	}
	return value;
    }

    private static Object newInstance(Class<?> clazz) throws ReflectionException {
	try {
	    if (List.class.isAssignableFrom(clazz)) {
		return clazz.cast(defaultListClass.newInstance());
	    } else if (Map.class.isAssignableFrom(clazz)) {
		return clazz.cast(defaultMapClass.newInstance());
	    } else {
		return clazz.newInstance();
	    }
	} catch (Exception e) {
	    throw new ReflectionException(e);
	}
    }

    private class PropEditorKey {
	private Class<?> clazz;
	private String propName;

	public PropEditorKey(Class<?> clazz, String propName) {
	    super();
	    this.clazz = clazz;
	    this.propName = propName;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + getOuterType().hashCode();
	    result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
	    result = prime * result + ((propName == null) ? 0 : propName.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    PropEditorKey other = (PropEditorKey) obj;
	    if (!getOuterType().equals(other.getOuterType()))
		return false;
	    if (clazz == null) {
		if (other.clazz != null)
		    return false;
	    } else if (!clazz.equals(other.clazz))
		return false;
	    if (propName == null) {
		if (other.propName != null)
		    return false;
	    } else if (!propName.equals(other.propName))
		return false;
	    return true;
	}

	private AbstractBeanWrapper getOuterType() {
	    return AbstractBeanWrapper.this;
	}

    }

}
