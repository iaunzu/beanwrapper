package com.github.iaunzu.beanwrapper.dto;

import java.util.List;
import java.util.Map;

public class TestDTO extends BaseTestDTO {
    private String name;
    private List<String> strings;
    private SubTestDTO subTest;
    private List<SubTestDTO> subTests;
    private EnumDTO enumDTO;
    private Map<String, SubTestDTO> subTestsMap;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<String> getStrings() {
	return strings;
    }

    public void setStrings(List<String> strings) {
	this.strings = strings;
    }

    public SubTestDTO getSubTest() {
	return subTest;
    }

    public void setSubTest(SubTestDTO subTest) {
	this.subTest = subTest;
    }

    public List<SubTestDTO> getSubTests() {
	return subTests;
    }

    public void setSubTests(List<SubTestDTO> subTests) {
	this.subTests = subTests;
    }

    public EnumDTO getEnumDTO() {
	return enumDTO;
    }

    public void setEnumDTO(EnumDTO enumDTO) {
	this.enumDTO = enumDTO;
    }

    public Map<String, SubTestDTO> getSubTestsMap() {
	return subTestsMap;
    }

    public void setSubTestsMap(Map<String, SubTestDTO> subTestsMap) {
	this.subTestsMap = subTestsMap;
    }

}
