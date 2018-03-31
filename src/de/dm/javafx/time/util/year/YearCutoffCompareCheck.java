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

import de.dm.javafx.time.util.year.YearCutoff.YearArgument;

/**
 * Utility class to compare a short year and a cutoff year.
 * 
 * @author David Meersteiner
 * @version 0.1.0
 */
public class YearCutoffCompareCheck {
	
	private boolean shortIsAfter;
	private boolean shortIsBefore;

	/**
	 * Creates a new compare check.
	 * @param cutoff the year cutoff class
	 * @param shortYear the short year
	 */
	public YearCutoffCompareCheck(YearCutoff cutoff, YearArgument shortYear) {
		YearArgument cutoffYear = new YearArgument(cutoff.getCutoffYear());
		shortIsBefore = shortYear.getYearAsInt() < cutoffYear.getEpochOffset(); 
		shortIsAfter = shortYear.getYearAsInt() > cutoffYear.getEpochOffset();
	}

	/**
	 * Checks if the compared short year is before the cutoff offset.
	 * @return {@code true}, if the short year is before the cutoff offset, {@code false} otherwise.
	 */
	public boolean isShortYearBeforeCutoffOffset() {
		return shortIsBefore;
	}
	
	/**
	 * Checks if the compared short year is after the cutoff offset.
	 * @return {@code true}, if the short year is after the cutoff offset, {@code false} otherwise.
	 */
	public boolean isShortYearAfterCutoffOffset() {
		return shortIsAfter;
	}
	
	/**
	 * Checks if the compared short year is on the cutoff offset.
	 * @return {@code true}, if the short year is on the cutoff offset, {@code false} otherwise.
	 */
	public boolean isShortYearOnCutoffOffset() {
		return !shortIsBefore && !shortIsAfter;
	}
	
}