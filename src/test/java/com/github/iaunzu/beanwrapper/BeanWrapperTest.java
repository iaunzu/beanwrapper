package com.github.iaunzu.beanwrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.github.iaunzu.beanwrapper.IBeanWrapper;
import com.github.iaunzu.beanwrapper.dto.EnumDTO;
import com.github.iaunzu.beanwrapper.dto.SubTestDTO;
import com.github.iaunzu.beanwrapper.dto.TestDTO;
import com.github.iaunzu.beanwrapper.impl.BeanWrapper;
import com.github.iaunzu.beanwrapper.propertyeditor.IPropertyEditor;

import static org.hamcrest.Matchers.is;

public class BeanWrapperTest {

    @Test
    public void testGetCachedBeanWrapper() {
	TestDTO test = new TestDTO();
	test.setId(1L);
	test.setName("test1");
	SubTestDTO subTest = new SubTestDTO();
	subTest.setProp1("test2");
	test.setSubTest(subTest);
	List<String> strings = new ArrayList<String>();
	strings.add("test3");
	strings.add("test4");
	test.setStrings(strings);
	List<SubTestDTO> subTests = new ArrayList<SubTestDTO>();
	subTests.add(subTest);
	test.setSubTests(subTests);
	Map<String, SubTestDTO> subTestsMap = new HashMap<String, SubTestDTO>();
	SubTestDTO subTest2 = new SubTestDTO();
	subTest2.setProp1("test5");
	subTestsMap.put("key", subTest2);
	test.setSubTestsMap(subTestsMap);

	IBeanWrapper beanWrapper = new BeanWrapper(test);
	Assert.assertThat(beanWrapper.getWrappedInstance(), is((Object) test));
	Assert.assertEquals(TestDTO.class, beanWrapper.getWrappedClass());

	Assert.assertEquals(test.getName(), beanWrapper.getPropertyValue("name"));
	Assert.assertEquals(test.getId(), beanWrapper.getPropertyValue("id"));
	Assert.assertEquals(test.getSubTest().getProp1(), beanWrapper.getPropertyValue("subTest.prop1"));
	Assert.assertEquals(test.getStrings(), beanWrapper.getPropertyValue("strings"));
	Assert.assertEquals(test.getStrings().get(1), beanWrapper.getPropertyValue("strings[1]"));
	Assert.assertEquals(test.getSubTests(), beanWrapper.getPropertyValue("subTests"));
	Assert.assertEquals(test.getSubTests().get(0).getProp1(), beanWrapper.getPropertyValue("subTests[0].prop1"));

	Assert.assertEquals(test.getSubTestsMap().get("key").getProp1(), beanWrapper.getPropertyValue("subTestsMap[key].prop1"));

    }

    @Test
    public void testSetCachedBeanWrapper() {
	TestDTO test = new TestDTO();

	IBeanWrapper beanWrapper = new BeanWrapper(test);
	beanWrapper.setPropertyValue("name", "test1");
	Assert.assertEquals("test1", test.getName());
	beanWrapper.setPropertyValue("id", 1L);
	Assert.assertEquals(Long.valueOf(1L), test.getId());
	beanWrapper.setPropertyValue("subTest.prop1", "test2");
	Assert.assertEquals("test2", test.getSubTest().getProp1());
	List<String> strings = new ArrayList<String>();
	beanWrapper.setPropertyValue("strings", strings);
	Assert.assertEquals(strings, test.getStrings());
	beanWrapper.setPropertyValue("strings[1]", "test4");
	Assert.assertEquals("test4", test.getStrings().get(1));

	beanWrapper.setPropertyValue("subTests.prop1", "test5");
	Assert.assertEquals("test5", test.getSubTests().get(0).getProp1());

	List<SubTestDTO> subTests = new ArrayList<SubTestDTO>();
	beanWrapper.setPropertyValue("subTests", subTests);
	Assert.assertEquals(subTests, test.getSubTests());

	beanWrapper.setPropertyValue("subTests.prop1", "test6");
	Assert.assertEquals("test6", test.getSubTests().get(0).getProp1());
	Assert.assertEquals(subTests.get(0).getProp1(), test.getSubTests().get(0).getProp1());

	beanWrapper.setPropertyValue("subTests[1].prop1", "test7");
	beanWrapper.setPropertyValue("subTests[1].prop2", "test8");
	Assert.assertEquals("test7", test.getSubTests().get(1).getProp1());
	Assert.assertEquals("test8", test.getSubTests().get(1).getProp2());

	SubTestDTO sub = new SubTestDTO();
	beanWrapper.setPropertyValue("subTests[2]", sub);
	Assert.assertEquals(sub, test.getSubTests().get(2));

	beanWrapper.setPropertyValue("subTests[1][prop2]", "test9");
	Assert.assertEquals("test9", test.getSubTests().get(1).getProp2());

	beanWrapper.setPropertyValue("subTestsMap[key].prop1", "test10");
	beanWrapper.setPropertyValue("subTestsMap[key][prop2]", "test11");

	Assert.assertEquals("test10", test.getSubTestsMap().get("key").getProp1());
	Assert.assertEquals("test11", test.getSubTestsMap().get("key").getProp2());

    }

    @Test
    public void testSetEnumeration() {
	TestDTO test = new TestDTO();
	IBeanWrapper beanWrapper = new BeanWrapper(test);

	beanWrapper.setPropertyValue("enumDTO", 1L);
	beanWrapper.setPropertyValue("enumDTO", 2L);
	Assert.assertEquals(EnumDTO.DOS, test.getEnumDTO());

    }

    @Test
    public void testPropEditor() {
	TestDTO test = new TestDTO();
	BeanWrapper beanWrapper = new BeanWrapper(test);
	beanWrapper.addPropertyEditor(Long.class, new IPropertyEditor() {
	    @Override
	    public Object getValue(Object value) {
		if (value != null && Long.class.isAssignableFrom(value.getClass())) {
		    return ((Long) value) + 1;
		}
		return value;
	    }
	});
	beanWrapper.setPropertyValue("id", 1L);
	Assert.assertEquals(Long.valueOf(2L), test.getId());

    }

}
