/**
 * MIT License
 * 
 * Copyright (c) 2018 David Meersteiner
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.dm.test.javafx.time;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.dm.javafx.time.exception.NumberParseException;
import de.dm.javafx.time.util.year.YearCutoff;

public class YearCutoffTest {

	private YearCutoff yearCutoff;
	
	@Before
	public void setUp() throws Exception {
		yearCutoff = new YearCutoff();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testBeforeCutoff() {
		yearCutoff.setCutoffYear(2020);
		int value = yearCutoff.interpret("10");
		assertThat(value, is(2010));
	}
	
	@Test
	public void testOnCutoff() {
		yearCutoff.setCutoffYear(2020);
		int value = yearCutoff.interpret("20");
		assertThat(value, is(2020));
	}
	
	@Test
	public void testAfterCutoff() {
		yearCutoff.setCutoffYear(2020);
		int value = yearCutoff.interpret("30");
		assertThat(value, is(1930));
	}
	
	@Test
	public void test2k() {
		yearCutoff.setCutoffYear(2000);
		int value = yearCutoff.interpret("0");
		assertThat(value, is(2000));
	}
	
	@Test
	public void testNonShort() {
		yearCutoff.setCutoffYear(2020);
		int value = yearCutoff.interpret("1850");
		assertThat(value, is(1850));
	}
	
	@Test
	public void testFourDigitShort() {
		yearCutoff.setCutoffYear(2020);
		int value = yearCutoff.interpret("0005");
		assertThat(value, is(5));
	}
	
	@Test
	public void testThreeDigitShort() {
		yearCutoff.setCutoffYear(2020);
		int value = yearCutoff.interpret("005");
		assertThat(value, is(5));
	}
	
	@Test
	public void testTwoDigitShort() {
		yearCutoff.setCutoffYear(2020);
		int value = yearCutoff.interpret("05");
		assertThat(value, is(2005));
	}
	
	@Test
	public void testOneDigitShort() {
		yearCutoff.setCutoffYear(2020);
		int value = yearCutoff.interpret("5");
		assertThat(value, is(2005));
	}
	
	@Test
	public void testHelperLastInvalid() {
		yearCutoff.setLastInvalidYear(1920);
		int value = yearCutoff.interpret("20");
		assertThat(value, is(2020));
	}
	
	@Test
	public void testHelperFirstValid() {
		yearCutoff.setFirstValidYear(1920);
		int value = yearCutoff.interpret("20");
		assertThat(value, is(1920));
	}
	
	@Test
	public void testHelperLastValid() {
		yearCutoff.setLastValidYear(2020);
		int value = yearCutoff.interpret("20");
		assertThat(value, is(2020));
	}
	
	@Test
	public void testHelperFirstInvalid() {
		yearCutoff.setFirstInvalidYear(2020);
		int value = yearCutoff.interpret("20");
		assertThat(value, is(1920));
	}
	
	@Test(expected=NumberParseException.class)
	public void testNonIntegers() {
		yearCutoff.interpret("foobar");
	}

}
