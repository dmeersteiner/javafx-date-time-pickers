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

package de.dm.javafx.time.util.year;

import java.time.LocalDate;

import de.dm.javafx.time.exception.NumberParseException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * YearCutoff interprets {@code String}s that may contain a short year
 * and returns {@code int}s as long years.
 * Usually a short year consists of two or less digits.
 * <p>
 * YearCutoff implements a cutoff year, which functions as a border.
 * Short years, that would cross that border
 * when given the epoch of the border, are modified instead.
 * <p>
 * In its default behaviour YearCutoff places the short year
 * in between the cutoff year and 99 years before it, both inclusive.
 * <p>
 * Examples
 * <code>
 * YearCutoff yc = new YearCutoff(2020);
 * yc.interpret("10"); 	   // = 2010
 * yc.interpret("20"); 	   // = 2020
 * yc.interpret("30"); 	   // = 1930
 * 
 * yc.interpret("5"); 	   // = 2005
 * yc.interpret("05"); 	   // = 2005
 * yc.interpret("005");    // = 5
 * yc.interpret("0005");   // = 5
 * 
 * yc.interpret("foobar"); // Exception thrown
 * </code>
 * @author David Meersteiner
 * @version 0.2.0
 */
public class YearCutoff implements ShortYearInterpreter {
	
	/**
	 * The maximal number of digits of a short year.
	 * <p>
	 * Current value = {@value}
	 */
	// Unless this code lives for ~8000 years, it should probably stay at 2.
	public static final int MAX_SHORTYEAR_DIGITS = 2;
	
	/**
	 * The range of the cutoff based on {@link YearCutoff#MAX_SHORTYEAR_DIGITS}.
	 * <p>
	 * Current value = {@value}
	 */
	public static final int CUTOFF_RANGE = (int) Math.min(Math.round(Math.pow(10, MAX_SHORTYEAR_DIGITS)), Integer.MAX_VALUE);
	
	/**
	 * The offset in years for the default constructor from this year.
	 * <p>
	 * Current value = {@value}
	 */
	public static final int DEFAULT_CUTOFF_OFFSET = 30;
	
	private static final YearCutoffBehaviour _default_handler = new BasicYearCutoffHandler();
	
	private YearCutoffBehaviour handler;
	
	/* PROPERTIES */

	private final IntegerProperty cutoffYearProperty = new SimpleIntegerProperty(this, "cutoffYear");
	/**
	 * Gets the cutoff year property, which wraps the year that acts as a border.
	 * @return the cutoff year property
	 */
	public IntegerProperty cutoffYearProperty() { return cutoffYearProperty; }
	/**
	 * Gets the cutoff year, the year that acts as a border.
	 * @return the cutoff year
	 */
	public int getCutoffYear() { return cutoffYearProperty().get(); }
	/**
	 * Sets the cutoff year, the year that acts as a border.
	 * @param value the year to set
	 */
	public void setCutoffYear(int value) { cutoffYearProperty().set(value); }
	
	/* CONSTRUCTORS */

	/**
	 * Creates a new instance with the given cutoff year.
	 * @param cutoffYear
	 */
	public YearCutoff(int cutoffYear) {
		setCutoffYear(cutoffYear);
	}
	
	/**
	 * Creates a new instance with a default cutoff year.
	 */
	public YearCutoff() {
		this(getDefaultCutoffYear());
	}
	
	private static int getDefaultCutoffYear() {
		LocalDate now = LocalDate.now();
		int currentYear = now.getYear();
		int cutoffYear = currentYear+DEFAULT_CUTOFF_OFFSET;
		return cutoffYear;
	}
	
	/* CLASS METHODS */
	
	/**
	 * 
	 * 
	 * @throws NumberParseException if the parameter couldn't be parsed with {@link Integer#parseInt(String)},
	 * e.g. because it didn't contain a year.
	 * @return the year parsed and interpreted from the {@code String}
	 */
	public int interpret(String shortYear) throws NumberParseException {
		YearArgument year = new YearArgument(shortYear);
		if (isShortYear(year)) {
			return handleShortYear(year);
		} else {
			return year.getYearAsInt();
		}
	}
	
	/**
	 * Checks if a given {@code YearArgument} contains a short year.
	 * @param shortYear a possible short year.
	 * @return {@code true}, if the parameter contains a short year, {@code false} otherwise.
	 */
	protected boolean isShortYear(YearArgument shortYear) {
		return isIntShortYear(shortYear.getYearAsInt())
				&& isStringShortYear(shortYear.getYearAsString());
	}
	
	/**
	 * Checks if a given {@code int} contains a short year, according to {@link YearCutoff#CUTOFF_RANGE}.
	 * @param shortYear an {@code int} containing a possible short year.
	 * @return {@code true}, if the parameter contains a short year, {@code false} otherwise. 
	 */
	protected boolean isIntShortYear(int shortYear) {
		int lowerBoundIncl = 0;
		int upperBoundExcl = YearCutoff.CUTOFF_RANGE;
		return lowerBoundIncl <= shortYear
				&& shortYear < upperBoundExcl;
	}
	
	/**
	 * Checks if a given {@code String} contains a short year, according to {@link YearCutoff#MAX_SHORTYEAR_DIGITS}.
	 * <p>
	 * This check is necessary, as the expected behaviour for the {@code String} "0005" should result in the year 5 A.D. 
	 * @param shortYear
	 * @return {@code true}, if the parameter contains a short year, {@code false} otherwise.
	 */
	protected boolean isStringShortYear(String shortYear) {
		return shortYear.length() <= MAX_SHORTYEAR_DIGITS;
	}
	
	/**
	 * Handles a short year depending on its content and the set YearCutoffHandler.
	 * @param shortYear
	 * @return the interpreted year as an {@code int}.
	 */
	protected int handleShortYear(YearArgument shortYear) {
		YearCutoffCompareCheck comparer = new YearCutoffCompareCheck(this, shortYear);
		if (comparer.isShortYearBeforeCutoffOffset()) {
			return getBehaviour().handleShortYearBeforeCutoff(this, shortYear);
		} else if (comparer.isShortYearAfterCutoffOffset()) {
			return getBehaviour().handleShortYearAfterCutoff(this, shortYear);
		} else {
			return getBehaviour().handleShortYearOnCutoff(this, shortYear);
		}
	}
	
	/* GETTER/SETTER */

	/**
	 * Returns the YearCutoffHandler, or the default handler, if none is set.
	 * @return the YearCutoffHandler, or the default handler, never {@code null}.
	 */
	public YearCutoffBehaviour getBehaviour() {
		if (handler == null) {
			return _default_handler;
		} else {
			return handler;
		}
	}
	/**
	 * Set the YearCutoffHandler
	 * @param handlerFactory the handlerFactory to set, or {@code null}
	 */
	public void setBehaviour(YearCutoffBehaviour handler) {
		this.handler = handler;
	}
	
	/* GETTER/SETTER HELPER FUNCTIONS */
	
	/**
	 * An easily understandable helper function to set the cutoff year.
	 * <p>
	 * <code>
	 * YearCutoff yearCutoff = new YearCutoff();
	 * yearCutoff.setLastValidYear(value);
	 * </code>
	 * is equal to
	 * <code>
	 * YearCutoff yearCutoff = new YearCutoff();
	 * yearCutoff.setCutoffYear(value);
	 * </code>
	 * @param value the last year that should be valid, before a cutoff happens
	 */
	public void setLastValidYear(int value) { setCutoffYear(value); }
	
	/**
	 * An easily understandable helper function to set the cutoff year.
	 * <p>
	 * <code>
	 * YearCutoff yearCutoff = new YearCutoff();
	 * yearCutoff.setFirstValidYear(value);
	 * </code>
	 * is equal to
	 * <code>
	 * YearCutoff yearCutoff = new YearCutoff();
	 * yearCutoff.setCutoffYear(value+YearCutoff.CUTOFF_RANGE-1);
	 * </code>
	 * @param value the first year that should be valid, so no cutoff happens
	 */
	public void setFirstValidYear(int value) { setLastInvalidYear(value-1); }
	
	/**
	 * An easily understandable helper function to set the cutoff year.
	 * <p>
	 * <code>
	 * YearCutoff yearCutoff = new YearCutoff();
	 * yearCutoff.setLastInvalidYear(value);
	 * </code>
	 * is equal to
	 * <code>
	 * YearCutoff yearCutoff = new YearCutoff();
	 * yearCutoff.setCutoffYear(value+YearCutoff.CUTOFF_RANGE);
	 * </code>
	 * @param value the last year that should be invalid, so a cutoff into the future happens
	 */
	public void setLastInvalidYear(int value) { setLastValidYear(value+CUTOFF_RANGE); }
	
	/**
	 * An easily understandable helper function to set the cutoff year.
	 * <p>v
	 * <code>
	 * YearCutoff yearCutoff = new YearCutoff();
	 * yearCutoff.setFirstInvalidYear(value);
	 * </code>
	 * is equal to
	 * <code>
	 * YearCutoff yearCutoff = new YearCutoff();
	 * yearCutoff.setCutoffYear(value-1);
	 * </code>
	 * @param value the first year that should be invalid, so a cutoff into the past happens
	 */
	public void setFirstInvalidYear(int value) { setLastValidYear(value-1); }
	
	/* UTILITY CLASSES */
	
	/**
	 * Utility class to hold a year as a {@code String} and as an {@code int}.
	 * 
	 * @author David Meersteiner
	 * @version 0.1.0
	 */
	public static class YearArgument {
		
		private String yearAsString;
		private int yearAsInt;
		
		/**
		 * Creates a new YearArgument with the given year.
		 * @param year the year to initialise the class with
		 */
		public YearArgument(String year) {
			this.yearAsString = year.trim();
			this.yearAsInt = parseYearOrThrowException(year);
		}
		
		/**
		 * Creates a new YearArgument with the given year.
		 * @param year the year to initialise the class with
		 */
		public YearArgument(int year) {
			this.yearAsString = String.valueOf(year);
			this.yearAsInt = year;
		}

		/**
		 * @return the year as a {@code String}.
		 */
		public String getYearAsString() {
			return yearAsString;
		}

		/**
		 * @return the year as an {@code int}.
		 */
		public int getYearAsInt() {
			return yearAsInt;
		}
		
		/**
		 * 
		 * @return the epoch of the year
		 */
		public int getEpoch() {
			return yearAsInt - getEpochOffset();
		}
		
		/**
		 * 
		 * @return the epoch offset, i.e. the year without the epoch
		 */
		public int getEpochOffset() {
			return yearAsInt % YearCutoff.CUTOFF_RANGE;
		}
		
		/* UTILITY FUNCTIONS */

		/**
		 * Parses a {@code String} for an {@code int} year, or throws a {@link RuntimeException}
		 * 
		 * @param a {@code String} containing an {@code int}. 
		 * @return the parsed year as an {@code int}
		 * @throws NumberParseException if the parameter couldn't be parsed with {@link Integer#parseInt(String)},
		 * e.g. because it didn't contain a year.
		 */
		private int parseYearOrThrowException(String year) throws NumberParseException {
			try {
				return Integer.parseInt(year);
			} catch (NumberFormatException ex) {
				throw new NumberParseException(ex);
			}
		}
	}
}
