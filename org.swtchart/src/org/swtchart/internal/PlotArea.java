/*******************************************************************************
 * Copyright (c) 2008-2009 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IAxis;
import org.swtchart.IBarSeries;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries;
import org.swtchart.ISeriesSet;
import org.swtchart.internal.series.Series;
import org.swtchart.internal.series.SeriesSet;

/**
 * Plot area to draw series and grids.
 */
public class PlotArea extends Canvas implements PaintListener {

    /** the chart */
    protected Chart chart;

    /** the set of plots */
    protected SeriesSet seriesSet;

    /** the default background color */
    private static final RGB DEFAULT_COLOR = Constants.WHITE;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param style
     *            the style
     */
    public PlotArea(Chart chart, int style) {
        super(chart, style | SWT.NO_BACKGROUND);

        this.chart = chart;

        seriesSet = new SeriesSet(chart);

        setBackground(new Color(Display.getDefault(), DEFAULT_COLOR));
        addPaintListener(this);
    }

    /**
     * Gets the set of series.
     * 
     * @return the set of series
     */
    public ISeriesSet getSeriesSet() {
        return seriesSet;
    }

    /*
     * @see Control#setBounds(int, int, int, int)
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        ((SeriesSet) getSeriesSet()).compressAllSeries();
    }

    /*
     * @see Control#setBackground(Color)
     */
    @Override
    public void setBackground(Color color) {
        if (color == null) {
            super.setBackground(new Color(Display.getDefault(), DEFAULT_COLOR));
        } else {
            super.setBackground(color);
        }
    }

    /*
     * @see PaintListener#paintControl(PaintEvent)
     */
    public void paintControl(PaintEvent e) {
        Point p = getSize();
        Image bufferedImage = new Image(Display.getCurrent(), p.x, p.y);
        GC gc = new GC(bufferedImage);

        // draw the plot area background
        gc.setBackground(getBackground());
        gc.fillRectangle(0, 0, p.x, p.y);

        // draw grid
        for (IAxis axis : chart.getAxisSet().getAxes()) {
            ((Grid) axis.getGrid()).draw(gc, p.x, p.y);
        }

        // draw series. The line series should be drawn on bar series.
        for (ISeries series : chart.getSeriesSet().getSeries()) {
            if (series instanceof IBarSeries) {
                ((Series) series).draw(gc, p.x, p.y);
            }
        }
        for (ISeries series : chart.getSeriesSet().getSeries()) {
            if (series instanceof ILineSeries) {
                ((Series) series).draw(gc, p.x, p.y);
            }
        }

        e.gc.drawImage(bufferedImage, 0, 0);
        bufferedImage.dispose();
        gc.dispose();
    }
}
