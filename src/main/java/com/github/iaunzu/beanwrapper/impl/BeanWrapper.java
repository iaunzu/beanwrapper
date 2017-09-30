package com.github.iaunzu.beanwrapper.impl;

import org.springframework.beans.BeansException;

import com.github.iaunzu.beanwrapper.IBeanWrapper;

public class BeanWrapper extends AbstractBeanWrapper implements IBeanWrapper {

    private Object target;

    public BeanWrapper(Object target) {
	if (target == null) {
	    throw new IllegalArgumentException("target cannot be null");
	}
	this.target = target;
    }

    @Override
    public Object getWrappedInstance() {
	return target;
    }

    @Override
    public Class<?> getWrappedClass() {
	return target.getClass();
    }

    @Override
    public Object getPropertyValue(String propertyName) {
	return getValue(target, propertyName);
    }

    @Override
    public void setPropertyValue(String propertyName, Object value) throws BeansException {
	setValue(target, propertyName, value);
    }

}