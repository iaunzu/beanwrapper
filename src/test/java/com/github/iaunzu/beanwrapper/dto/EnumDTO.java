package com.github.iaunzu.beanwrapper.dto;

import com.github.iaunzu.beanwrapper.dto.DatabaseClass;

public enum EnumDTO implements DatabaseClass<Long> {
    UNO(1L), DOS(2L);
    private Long index;

    EnumDTO(Long index) {
	this.index = index;
    }

    public Long getDatabaseValue() {
	return index;
    }

}
