package org.swtchart.ext.internal.properties;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IBarSeries;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries;
import org.swtchart.LineStyle;
import org.swtchart.ILineSeries.PlotSymbolType;

/**
 * The series page on properties dialog.
 */
public class SeriesPage extends AbstractSelectorPage {

    /** the button for visibility */
    protected Button visibleButton;

    /** the button for stack state */
    protected Button stackedButton;

    /** the x axis id combo */
    protected Combo xAxisIdCombo;

    /** the y axis id combo */
    protected Combo yAxisIdCombo;

    /** the line color button */
    protected ColorSelector lineColorButton;

    /** the line style combo */
    protected Combo lineStyleCombo;

    /** the color selector for symbol */
    protected ColorSelector symbolColorButton;

    /** the symbol type combo */
    protected Combo symbolTypeCombo;

    /** the symbol size spinner */
    protected Spinner symbolSizeSpinner;

    /** the bar color button */
    protected ColorSelector barColorButton;

    /** the padding size spinner */
    protected Spinner paddingSizeSpinner;

    /** the series array */
    private ISeries[] series;

    /** the items for x axis id combo */
    private int[] xAxisIdItems;

    /** the items for y axis id combo */
    private int[] yAxisIdItems;

    /** the states indicating the visibility of series */
    protected boolean[] visibleStates;

    /** the states indicating the series is stacked */
    protected boolean[] stackedStates;

    /** the x axis ids */
    protected int[] xAxisIds;

    /** the y axis ids */
    protected int[] yAxisIds;

    /** the line colors */
    protected Color[] lineColors;

    /** the line styles */
    protected LineStyle[] lineStyles;

    /** the symbol colors */
    protected Color[] symbolColors;

    /** the symbol types */
    protected PlotSymbolType[] symbolTypes;

    /** the symbol sizes */
    protected int[] symbolSizes;

    /** the bar colors */
    protected Color[] barColors;

    /** the paddings */
    protected int[] paddings;

    /** the stack panel */
    private Composite stackPanel;

    /** the stack layout */
    private StackLayout stackLayout;

    /** the line series group */
    private Composite lineSeriesGroup;

    /** the bar series group */
    private Composite barSeriesGroup;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param axes
     *            the axes
     * @param title
     *            the title
     */
    public SeriesPage(Chart chart, String title) {
        super(chart, title, "Series:");

        series = chart.getSeriesSet().getSeries();
        xAxisIdItems = chart.getAxisSet().getXAxisIds();
        yAxisIdItems = chart.getAxisSet().getYAxisIds();

        visibleStates = new boolean[series.length];
        stackedStates = new boolean[series.length];
        xAxisIds = new int[series.length];
        yAxisIds = new int[series.length];
        lineColors = new Color[series.length];
        lineStyles = new LineStyle[series.length];
        symbolColors = new Color[series.length];
        symbolTypes = new PlotSymbolType[series.length];
        symbolSizes = new int[series.length];
        barColors = new Color[series.length];
        paddings = new int[series.length];
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.swtchart.ext.internal.properties.AbstractSelectorPage#getListItems()
     */
    @Override
    protected String[] getListItems() {
        String[] items = new String[series.length];
        for (int i = 0; i < items.length; i++) {
            items[i] = String.valueOf(series[i].getId());
        }
        return items;
    }

    /*
     * @see AbstractSelectorPage#selectInitialValuess()
     */
    @Override
    protected void selectInitialValues() {
        for (int i = 0; i < series.length; i++) {
            visibleStates[i] = series[i].isVisible();
            stackedStates[i] = series[i].isStackEnabled();
            if (series[i] instanceof ILineSeries) {
                lineColors[i] = ((ILineSeries) series[i]).getLineColor();
                lineStyles[i] = ((ILineSeries) series[i]).getLineStyle();
                symbolColors[i] = ((ILineSeries) series[i]).getSymbolColor();
                symbolTypes[i] = ((ILineSeries) series[i]).getSymbolType();
                symbolSizes[i] = ((ILineSeries) series[i]).getSymbolSize();
            } else if (series[i] instanceof IBarSeries) {
                barColors[i] = ((IBarSeries) series[i]).getBarColor();
                paddings[i] = ((IBarSeries) series[i]).getBarPadding();
            }
            xAxisIds[i] = series[i].getXAxisId();
            yAxisIds[i] = series[i].getYAxisId();
        }

        updateStackPanel();
    }

    /*
     * @see AbstractSelectorPage#updateControlSelections()
     */
    @Override
    protected void updateControlSelections() {
        visibleButton.setSelection(visibleStates[selectedIndex]);
        stackedButton.setSelection(stackedStates[selectedIndex]);
        if (xAxisIdCombo != null) {
            xAxisIdCombo.setText("" + xAxisIds[selectedIndex]);
        }
        if (yAxisIdCombo != null) {
            yAxisIdCombo.setText("" + yAxisIds[selectedIndex]);
        }
        if (series[selectedIndex] instanceof ILineSeries) {
            lineStyleCombo.setText(lineStyles[selectedIndex].label);
            lineStyleCombo.setEnabled(true);
            lineColorButton.setColorValue(lineColors[selectedIndex].getRGB());
            symbolColorButton.setColorValue(symbolColors[selectedIndex]
                    .getRGB());
            symbolTypeCombo.setText(symbolTypes[selectedIndex].label);
            symbolSizeSpinner.setSelection(symbolSizes[selectedIndex]);
        } else if (series[selectedIndex] instanceof IBarSeries) {
            barColorButton.setColorValue(barColors[selectedIndex].getRGB());
            paddingSizeSpinner.setSelection(paddings[selectedIndex]);
        }

        setControlsEnable(series[selectedIndex].isVisible());
        updateStackPanel();
    }

    /**
     * Updates the stack panel.
     */
    private void updateStackPanel() {
        if (series[selectedIndex] instanceof ILineSeries) {
            stackLayout.topControl = lineSeriesGroup;
        } else if (series[selectedIndex] instanceof IBarSeries) {
            stackLayout.topControl = barSeriesGroup;
        }
        stackPanel.layout();
    }

    /*
     * @see AbstractSelectorPage#addRightPanelContents(Composite)
     */
    @Override
    protected void addRightPanelContents(Composite parent) {
        addSeriesGroup(parent);

        stackPanel = new Composite(parent, SWT.NONE);
        stackLayout = new StackLayout();
        stackPanel.setLayout(stackLayout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        stackPanel.setLayoutData(gridData);

        addLineSeriesGroup(stackPanel);
        addBarSeriesGroup(stackPanel);
    }

    /**
     * Adds the series panel.
     * 
     * @param parent
     *            the parent to add the series panel
     */
    private void addSeriesGroup(Composite parent) {
        Composite group = new Composite(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, true));

        visibleButton = createCheckBoxControl(group, "Show plot");
        visibleButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean visible = visibleButton.getSelection();
                visibleStates[selectedIndex] = visible;
                setControlsEnable(visible);
            }
        });

        stackedButton = createCheckBoxControl(group, "Stacked series:");
        stackedButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                stackedStates[selectedIndex] = stackedButton.getSelection();
            }
        });

        if (xAxisIdItems.length > 1) {
            createLabelControl(group, "X Axis:");
            String[] items = new String[xAxisIdItems.length];
            for (int i = 0; i < items.length; i++) {
                items[i] = "" + xAxisIdItems[i];
            }
            xAxisIdCombo = createComboControl(group, items);
            xAxisIdCombo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    xAxisIds[selectedIndex] = Integer.parseInt(xAxisIdCombo
                            .getText());
                }
            });
        }

        if (yAxisIdItems.length > 1) {
            createLabelControl(group, "Y Axis:");
            String[] items = new String[yAxisIdItems.length];
            for (int i = 0; i < items.length; i++) {
                items[i] = "" + yAxisIdItems[i];
            }
            yAxisIdCombo = createComboControl(group, items);
            yAxisIdCombo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    yAxisIds[selectedIndex] = Integer.parseInt(yAxisIdCombo
                            .getText());
                }
            });
        }
    }

    /**
     * Adds the line series group.
     * 
     * @param parent
     *            the parent to add the line series group
     */
    private void addLineSeriesGroup(Composite parent) {
        lineSeriesGroup = createGroupControl(parent, "Line series:", true);
        stackLayout.topControl = lineSeriesGroup;

        createLabelControl(lineSeriesGroup, "Line color:");
        lineColorButton = createColorButtonControl(lineSeriesGroup);
        lineColorButton.addListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                lineColors[selectedIndex] = new Color(Display.getDefault(),
                        lineColorButton.getColorValue());
            }
        });

        createLabelControl(lineSeriesGroup, "Line style:");
        LineStyle[] styles = LineStyle.values();
        String[] labels = new String[styles.length];
        for (int i = 0; i < styles.length; i++) {
            labels[i] = styles[i].label;
        }
        lineStyleCombo = createComboControl(lineSeriesGroup, labels);
        lineStyleCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String value = lineStyleCombo.getText();
                LineStyle selectedStyle = LineStyle.NONE;
                for (LineStyle style : LineStyle.values()) {
                    if (style.label.equals(value)) {
                        selectedStyle = style;
                    }
                }
                lineStyles[selectedIndex] = selectedStyle;
            }
        });

        createLabelControl(lineSeriesGroup, "Symbol color:");
        symbolColorButton = createColorButtonControl(lineSeriesGroup);
        symbolColorButton.addListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                symbolColors[selectedIndex] = new Color(Display.getDefault(),
                        symbolColorButton.getColorValue());
            }
        });

        createLabelControl(lineSeriesGroup, "Symbol type:");
        PlotSymbolType[] types = PlotSymbolType.values();
        labels = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            labels[i] = types[i].label;
        }
        symbolTypeCombo = createComboControl(lineSeriesGroup, labels);
        symbolTypeCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String value = symbolTypeCombo.getText();
                PlotSymbolType selectedType = PlotSymbolType.CIRCLE;
                for (PlotSymbolType type : PlotSymbolType.values()) {
                    if (type.label.equals(value)) {
                        selectedType = type;
                    }
                }
                symbolTypes[selectedIndex] = selectedType;
            }
        });

        createLabelControl(lineSeriesGroup, "Symbol size:");
        symbolSizeSpinner = createSpinnerControl(lineSeriesGroup, 1, 10);
        symbolSizeSpinner.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                symbolSizes[selectedIndex] = symbolSizeSpinner.getSelection();
            }
        });
    }

    /**
     * Adds the bar series group.
     * 
     * @param parent
     *            the parent to add the bar series group
     */
    private void addBarSeriesGroup(Composite parent) {
        barSeriesGroup = new Composite(parent, SWT.NONE);
        new Text(parent, SWT.NONE);
        barSeriesGroup.setLayout(new GridLayout(1, true));
        barSeriesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Group group = createGroupControl(barSeriesGroup, "Bar series:", true);

        createLabelControl(group, "Color:");
        barColorButton = createColorButtonControl(group);
        barColorButton.addListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                barColors[selectedIndex] = new Color(Display.getDefault(),
                        barColorButton.getColorValue());
            }
        });

        createLabelControl(group, "Padding size:");
        paddingSizeSpinner = createSpinnerControl(group, 0, 100);
        paddingSizeSpinner.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                paddings[selectedIndex] = paddingSizeSpinner.getSelection();
            }
        });
    }

    /**
     * Sets the enable state of controls.
     * 
     * @param enabled
     *            true if controls are enabled
     */
    protected void setControlsEnable(boolean enabled) {
        lineColorButton.setEnabled(enabled);
        lineStyleCombo.setEnabled(enabled);
        stackedButton.setEnabled(enabled);
        if (xAxisIdCombo != null) {
            xAxisIdCombo.setEnabled(enabled);
        }
        if (yAxisIdCombo != null) {
            yAxisIdCombo.setEnabled(enabled);
        }
        barColorButton.setEnabled(enabled);
        paddingSizeSpinner.setEnabled(enabled);
    }

    /*
     * @see AbstractPreferencePage#apply()
     */
    @Override
    public void apply() {
        for (int i = 0; i < series.length; i++) {
            series[i].setVisible(visibleStates[i]);
            if (series[i] instanceof ILineSeries) {
                ((ILineSeries) series[i]).setLineColor(lineColors[i]);
                ((ILineSeries) series[i]).setLineStyle(lineStyles[i]);
                ((ILineSeries) series[i]).setSymbolColor(symbolColors[i]);
                ((ILineSeries) series[i]).setSymbolType(symbolTypes[i]);
                ((ILineSeries) series[i]).setSymbolSize(symbolSizes[i]);
            } else if (series[i] instanceof IBarSeries) {
                ((IBarSeries) series[i]).setBarColor(barColors[i]);
                ((IBarSeries) series[i]).setBarPadding(paddings[i]);
            }
            try {
                series[i].enableStack(stackedStates[i]);
            } catch (IllegalArgumentException e) {
                stackedStates[i] = false;
                stackedButton.setSelection(false);
            }
            series[i].setXAxisId(xAxisIds[i]);
            series[i].setYAxisId(yAxisIds[i]);
        }
    }

    /*
     * @see PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        visibleStates[selectedIndex] = true;
        stackedStates[selectedIndex] = false;
        if (xAxisIdCombo != null) {
            xAxisIds[selectedIndex] = 0;
        }
        if (yAxisIdCombo != null) {
            yAxisIds[selectedIndex] = 0;
        }
        if (series[selectedIndex] instanceof ILineSeries) {
            lineStyles[selectedIndex] = LineStyle.SOLID;
            lineColors[selectedIndex] = new Color(Display.getDefault(),
                    Constants.BLUE);
            symbolColors[selectedIndex] = new Color(Display.getDefault(),
                    Constants.DARK_GRAY);
            symbolTypes[selectedIndex] = PlotSymbolType.CIRCLE;
            symbolSizes[selectedIndex] = 4;
        } else if (series[selectedIndex] instanceof IBarSeries) {
            barColors[selectedIndex] = new Color(Display.getDefault(),
                    Constants.BLUE);
            paddings[selectedIndex] = 20;
        }

        updateControlSelections();

        super.performDefaults();
    }
}
