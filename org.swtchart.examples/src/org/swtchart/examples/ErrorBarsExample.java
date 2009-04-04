package org.swtchart.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;

/**
 * An example for error bars.
 */
public class ErrorBarsExample {

	private static final double[] ySeries = { 0.0, 0.38, 0.71, 0.92, 1.0, 0.92,
			0.71, 0.38, 0.0, -0.38, -0.71, -0.92, -1.0, -0.92, -0.71, -0.38 };

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Error Bars Example");
		shell.setSize(500, 400);
		shell.setLayout(new FillLayout());

		// create a chart
		Chart chart = new Chart(shell, SWT.NONE);
		
		// create line series
		ILineSeries lineSeries = (ILineSeries) chart.getSeriesSet()
				.createSeries(SeriesType.LINE, "line series");
		lineSeries.setYSeries(ySeries);
		
		// set error bars
		lineSeries.getYErrorBar().setVisible(true);
		lineSeries.getYErrorBar().setError(0.1);

		// adjust the axis range
		chart.getAxisSet().adjustRange();

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}