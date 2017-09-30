package com.github.iaunzu.beanwrapper;

import com.github.iaunzu.beanwrapper.propertyeditor.IPropertyEditor;

public interface IBeanWrapper {

    Object getWrappedInstance();

    Class<?> getWrappedClass();

    Object getPropertyValue(String propertyName);

    void setPropertyValue(String propertyName, Object value);

    IPropertyEditor getPropertyEditor(Class<?> clazz);

    IPropertyEditor getPropertyEditor(Class<?> clazz, String propertyName);

    void addPropertyEditor(Class<?> clazz, IPropertyEditor propertyEditor);

    void addPropertyEditor(Class<?> clazz, String propertyName, IPropertyEditor propertyEditor);

}
