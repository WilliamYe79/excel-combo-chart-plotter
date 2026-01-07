package com.gwill.tools.graphics.excelcombochartplotter.ui;

import com.gwill.tools.graphics.excelcombochartplotter.i18n.I18nManager;
import com.gwill.tools.graphics.excelcombochartplotter.i18n.I18nManager.LocaleChangeListener;
import com.gwill.tools.graphics.excelcombochartplotter.model.ChartType;
import com.gwill.tools.graphics.excelcombochartplotter.model.YAxisSeriesConfig;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Y-axis data series configuration panel
 */
public class YAxisPanel extends JPanel implements LocaleChangeListener {

    private final I18nManager i18n;
    private final YAxisSeriesTableModel tableModel;
    private final JTable table;
    private final JButton addButton;
    private BiConsumer<String, String> onSelectionChangeListener;
    private Supplier<List<String>> unselectedColumnsSupplier;

    public YAxisPanel() {
        this.i18n = I18nManager.getInstance();

        setLayout( new BorderLayout( 5, 5 ) );
        setBorder( BorderFactory.createTitledBorder( i18n.getString( "yaxis.title" ) ) );

        // Add series button panel
        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        addButton = new JButton( i18n.getString( "yaxis.add.series" ) );
        addButton.addActionListener( e -> addSeries() );
        buttonPanel.add( addButton );
        add( buttonPanel, BorderLayout.NORTH );

        // Series table
        tableModel = new YAxisSeriesTableModel();
        table = new JTable( tableModel );
        table.setRowHeight( 28 );

        setupTableColumns();

        JScrollPane scrollPane = new JScrollPane( table );
        scrollPane.setPreferredSize( new Dimension( 0, 150 ) );
        add( scrollPane, BorderLayout.CENTER );

        // Register for locale changes
        i18n.addLocaleChangeListener( this );
    }

    @Override
    public void onLocaleChanged( Locale newLocale ) {
        ( (TitledBorder) getBorder() ).setTitle( i18n.getString( "yaxis.title" ) );
        addButton.setText( i18n.getString( "yaxis.add.series" ) );

        // Update table column headers
        tableModel.fireTableStructureChanged();
        setupTableColumns();

        repaint();
    }

    private void setupTableColumns() {
        // Column name selection column
        TableColumn columnNameColumn = table.getColumnModel().getColumn( 0 );
        columnNameColumn.setPreferredWidth( 150 );

        // Chart type column
        TableColumn chartTypeColumn = table.getColumnModel().getColumn( 1 );
        chartTypeColumn.setPreferredWidth( 120 );
        JComboBox<ChartType> chartTypeCombo = new JComboBox<>( ChartType.values() );
        chartTypeColumn.setCellEditor( new DefaultCellEditor( chartTypeCombo ) );

        // Secondary axis column
        TableColumn secondaryAxisColumn = table.getColumnModel().getColumn( 2 );
        secondaryAxisColumn.setPreferredWidth( 80 );
        secondaryAxisColumn.setCellRenderer( new CheckBoxRenderer() );
        secondaryAxisColumn.setCellEditor( new DefaultCellEditor( new JCheckBox() ) );

        // Action column
        TableColumn actionColumn = table.getColumnModel().getColumn( 3 );
        actionColumn.setPreferredWidth( 60 );
        actionColumn.setCellRenderer( new DeleteButtonRenderer() );
        actionColumn.setCellEditor( new DeleteButtonEditor() );
    }

    /**
     * Set supplier for getting unselected column names
     */
    public void setUnselectedColumnsSupplier( Supplier<List<String>> supplier ) {
        this.unselectedColumnsSupplier = supplier;
    }

    /**
     * Set selection change listener
     *
     * @param listener BiConsumer that receives (oldSelection, newSelection)
     */
    public void setOnSelectionChangeListener( BiConsumer<String, String> listener ) {
        this.onSelectionChangeListener = listener;
    }

    /**
     * Get currently selected Y-axis column names
     */
    public Set<String> getSelectedColumns() {
        Set<String> selected = new HashSet<>();
        for( SeriesRow row : tableModel.getRows() ) {
            if( row.columnName != null && !row.columnName.isBlank() ) {
                selected.add( row.columnName );
            }
        }
        return selected;
    }

    /**
     * Notify selection change
     */
    private void notifySelectionChange( String oldSelection, String newSelection ) {
        if( onSelectionChangeListener != null ) {
            onSelectionChangeListener.accept( oldSelection, newSelection );
        }
    }

    /**
     * Add new series
     */
    private void addSeries() {
        List<String> availableColumns = unselectedColumnsSupplier != null
            ? unselectedColumnsSupplier.get()
            : new ArrayList<>();

        if( availableColumns.isEmpty() ) {
            JOptionPane.showMessageDialog( this,
                i18n.getString( "yaxis.no.available.columns" ),
                i18n.getString( "dialog.info" ),
                JOptionPane.INFORMATION_MESSAGE );
            return;
        }

        String defaultColumn = availableColumns.getFirst();

        tableModel.addRow( new SeriesRow( defaultColumn, ChartType.BAR, false ) );

        updateColumnComboBoxForRow( tableModel.getRowCount() - 1, availableColumns );

        notifySelectionChange( null, defaultColumn );
    }

    /**
     * Update column name ComboBox options for specified row
     */
    private void updateColumnComboBoxForRow( int rowIndex, List<String> availableColumns ) {
        TableColumn columnNameColumn = table.getColumnModel().getColumn( 0 );
        JComboBox<String> columnCombo = new JComboBox<>( availableColumns.toArray( new String[0] ) );
        columnNameColumn.setCellEditor( new ColumnNameCellEditor( columnCombo ) );
    }

    /**
     * Clear all series
     */
    public void clear() {
        tableModel.clear();
    }

    /**
     * Get all series configurations
     */
    public List<YAxisSeriesConfig> getSeriesConfigs() {
        List<YAxisSeriesConfig> configs = new ArrayList<>();
        for( SeriesRow row : tableModel.getRows() ) {
            configs.add( new YAxisSeriesConfig( row.columnName, row.chartType, row.useSecondaryAxis ) );
        }
        return configs;
    }

    /**
     * Series row data
     */
    private static class SeriesRow {
        String columnName;
        ChartType chartType;
        boolean useSecondaryAxis;

        SeriesRow( String columnName, ChartType chartType, boolean useSecondaryAxis ) {
            this.columnName = columnName;
            this.chartType = chartType;
            this.useSecondaryAxis = useSecondaryAxis;
        }
    }

    /**
     * Table data model
     */
    private class YAxisSeriesTableModel extends AbstractTableModel {
        private final List<SeriesRow> rows = new ArrayList<>();

        private String[] getColumnNames() {
            return new String[]{
                i18n.getString( "yaxis.column.header" ),
                i18n.getString( "yaxis.charttype.header" ),
                i18n.getString( "yaxis.secondary.header" ),
                i18n.getString( "yaxis.action.header" )
            };
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName( int column ) {
            return getColumnNames()[column];
        }

        @Override
        public Class<?> getColumnClass( int columnIndex ) {
            return switch( columnIndex ) {
                case 0 -> String.class;
                case 1 -> ChartType.class;
                case 2 -> Boolean.class;
                case 3 -> JButton.class;
                default -> Object.class;
            };
        }

        @Override
        public boolean isCellEditable( int rowIndex, int columnIndex ) {
            return true;
        }

        @Override
        public Object getValueAt( int rowIndex, int columnIndex ) {
            SeriesRow row = rows.get( rowIndex );
            return switch( columnIndex ) {
                case 0 -> row.columnName;
                case 1 -> row.chartType;
                case 2 -> row.useSecondaryAxis;
                case 3 -> i18n.getString( "yaxis.action.header" );
                default -> null;
            };
        }

        @Override
        public void setValueAt( Object value, int rowIndex, int columnIndex ) {
            SeriesRow row = rows.get( rowIndex );
            switch( columnIndex ) {
                case 0 -> {
                    String oldValue = row.columnName;
                    String newValue = (String) value;
                    if( !java.util.Objects.equals( oldValue, newValue ) ) {
                        row.columnName = newValue;
                        notifySelectionChange( oldValue, newValue );
                    }
                }
                case 1 -> row.chartType = (ChartType) value;
                case 2 -> row.useSecondaryAxis = (Boolean) value;
            }
            fireTableCellUpdated( rowIndex, columnIndex );
        }

        void addRow( SeriesRow row ) {
            rows.add( row );
            fireTableRowsInserted( rows.size() - 1, rows.size() - 1 );
        }

        void removeRow( int rowIndex ) {
            if( rowIndex >= 0 && rowIndex < rows.size() ) {
                String removedColumn = rows.get( rowIndex ).columnName;
                rows.remove( rowIndex );
                fireTableRowsDeleted( rowIndex, rowIndex );
                notifySelectionChange( removedColumn, null );
            }
        }

        void clear() {
            int size = rows.size();
            if( size > 0 ) {
                rows.clear();
                fireTableRowsDeleted( 0, size - 1 );
            }
        }

        List<SeriesRow> getRows() {
            return new ArrayList<>( rows );
        }
    }

    /**
     * Column name CellEditor that dynamically updates options before editing
     */
    private class ColumnNameCellEditor extends DefaultCellEditor {
        private final JComboBox<String> comboBox;

        ColumnNameCellEditor( JComboBox<String> comboBox ) {
            super( comboBox );
            this.comboBox = comboBox;
        }

        @Override
        public Component getTableCellEditorComponent( JTable table, Object value,
                                                      boolean isSelected, int row, int column ) {
            if( unselectedColumnsSupplier != null ) {
                List<String> available = unselectedColumnsSupplier.get();
                String currentValue = (String) value;
                if( currentValue != null && !available.contains( currentValue ) ) {
                    available = new ArrayList<>( available );
                    available.addFirst( currentValue );
                }
                comboBox.removeAllItems();
                for( String item : available ) {
                    comboBox.addItem( item );
                }
            }
            return super.getTableCellEditorComponent( table, value, isSelected, row, column );
        }
    }

    /**
     * CheckBox renderer
     */
    private static class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        CheckBoxRenderer() {
            setHorizontalAlignment( CENTER );
        }

        @Override
        public Component getTableCellRendererComponent( JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus, int row, int column ) {
            setSelected( value != null && (Boolean) value );
            setBackground( isSelected ? table.getSelectionBackground() : table.getBackground() );
            return this;
        }
    }

    /**
     * Delete button renderer - uses red horizontal bar icon
     */
    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        DeleteButtonRenderer() {
            setText( "━" );
            setForeground( Color.RED );
            setFont( getFont().deriveFont( Font.BOLD, 14f ) );
            setToolTipText( i18n.getString( "yaxis.delete.tooltip" ) );
        }

        @Override
        public Component getTableCellRendererComponent( JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus, int row, int column ) {
            return this;
        }
    }

    /**
     * Delete button editor - uses red horizontal bar icon
     */
    private class DeleteButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        DeleteButtonEditor() {
            super( new JCheckBox() );
            button = new JButton( "━" );
            button.setForeground( Color.RED );
            button.setFont( button.getFont().deriveFont( Font.BOLD, 14f ) );
            button.setToolTipText( i18n.getString( "yaxis.delete.tooltip" ) );
            button.addActionListener( e -> {
                fireEditingStopped();
                tableModel.removeRow( currentRow );
            } );
        }

        @Override
        public Component getTableCellEditorComponent( JTable table, Object value,
                                                      boolean isSelected, int row, int column ) {
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return i18n.getString( "yaxis.action.header" );
        }
    }
}
