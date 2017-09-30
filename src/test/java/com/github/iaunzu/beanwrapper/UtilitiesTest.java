package com.github.iaunzu.beanwrapper;

import junit.framework.Assert;

import org.junit.Test;

import com.github.iaunzu.beanwrapper.util.Utilities;

public class UtilitiesTest {

    @Test
    public void test() {
	Assert.assertEquals("asdf", Utilities.getPathFromPropertyName("asdf"));
	Assert.assertEquals("", Utilities.getSubPropertyFromPropertyName("asdf"));

	Assert.assertEquals("asdf", Utilities.getPathFromPropertyName("asdf.1234"));
	Assert.assertEquals("1234", Utilities.getSubPropertyFromPropertyName("asdf.1234"));

	Assert.assertEquals("asdf", Utilities.getPathFromPropertyName("asdf[1234]"));
	Assert.assertEquals("[1234]", Utilities.getSubPropertyFromPropertyName("asdf[1234]"));

	Assert.assertEquals("1234", Utilities.getPathFromPropertyName("[1234]"));
	Assert.assertEquals("", Utilities.getSubPropertyFromPropertyName("[1234]"));

	Assert.assertEquals("abc", Utilities.getPathFromPropertyName("[abc][1234]"));
	Assert.assertEquals("[1234]", Utilities.getSubPropertyFromPropertyName("[abc][1234]"));

	Assert.assertEquals("abc", Utilities.getSubPropertyFromPropertyName("[1234].abc"));

	Assert.assertEquals("asdf", Utilities.removeQuotes("asdf"));
	Assert.assertEquals("asdf", Utilities.removeQuotes("'asdf'"));
	Assert.assertEquals("asdf", Utilities.removeQuotes("\"asdf\""));

    }

}
