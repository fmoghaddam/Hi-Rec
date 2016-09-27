package util;

import org.apache.log4j.Logger;

import static java.lang.String.format;

/**
 * The code based is from:
 * http://stackoverflow.com/questions/11383070/pretty-print-2d-array-in-java
 * 
 */
public final class PrettyPrinter {

    private static final Logger LOG = Logger.getLogger(PrettyPrinter.class.getCanonicalName());

    private static final char BORDER_KNOT = '+';
    private static final char HORIZONTAL_BORDER = '-';
    private static final char VERTICAL_BORDER = '|';

    private static final String DEFAULT_AS_NULL = "(NULL)";

    private final String asNull;

    public PrettyPrinter() {
	this(DEFAULT_AS_NULL);
    }

    public PrettyPrinter(String asNull) {
	if (asNull == null) {
	    throw new IllegalArgumentException("No NULL-value placeholder provided");
	}
	this.asNull = asNull;
    }

    public void print(String[][] table) {
	if (table == null) {
	    throw new IllegalArgumentException("No tabular data provided");
	}
	if (table.length == 0) {
	    return;
	}
	final int[] widths = new int[getMaxColumns(table)];
	adjustColumnWidths(table, widths);
	printPreparedTable(table, widths, getHorizontalBorder(widths));
	LOG.info("");
    }

    private void printPreparedTable(String[][] table, int widths[], String horizontalBorder) {
	final int lineLength = horizontalBorder.length();
	LOG.info(horizontalBorder);
	for (final String[] row : table) {
	    if (row != null) {
		LOG.info(getRow(row, widths, lineLength));
		LOG.info(horizontalBorder);
	    }
	}
    }

    private String getRow(String[] row, int[] widths, int lineLength) {
	final StringBuilder builder = new StringBuilder(lineLength).append(VERTICAL_BORDER);
	final int maxWidths = widths.length;
	for (int i = 0; i < maxWidths; i++) {
	    builder.append(padRight(getCellValue(safeGet(row, i, null)), widths[i])).append(VERTICAL_BORDER);
	}
	return builder.toString();
    }

    private String getHorizontalBorder(int[] widths) {
	final StringBuilder builder = new StringBuilder(256);
	builder.append(BORDER_KNOT);
	for (final int w : widths) {
	    for (int i = 0; i < w; i++) {
		builder.append(HORIZONTAL_BORDER);
	    }
	    builder.append(BORDER_KNOT);
	}
	return builder.toString();
    }

    private int getMaxColumns(String[][] rows) {
	int max = 0;
	for (final String[] row : rows) {
	    if (row != null && row.length > max) {
		max = row.length;
	    }
	}
	return max;
    }

    private void adjustColumnWidths(String[][] rows, int[] widths) {
	for (final String[] row : rows) {
	    if (row != null) {
		for (int c = 0; c < widths.length; c++) {
		    final String cv = getCellValue(safeGet(row, c, asNull));
		    final int l = cv.length();
		    if (widths[c] < l) {
			widths[c] = l;
		    }
		}
	    }
	}
    }

    private static String padRight(String s, int n) {
	return format("%1$-" + n + "s", s);
    }

    private static String safeGet(String[] array, int index, String defaultValue) {
	return index < array.length ? array[index] : defaultValue;
    }

    private String getCellValue(Object value) {
	return value == null ? asNull : value.toString();
    }

}
