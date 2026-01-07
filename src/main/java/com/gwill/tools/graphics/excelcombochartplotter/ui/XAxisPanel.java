package com.gwill.tools.graphics.excelcombochartplotter.ui;

import com.gwill.tools.graphics.excelcombochartplotter.i18n.I18nManager;
import com.gwill.tools.graphics.excelcombochartplotter.i18n.I18nManager.LocaleChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * X-axis data selection panel
 */
public class XAxisPanel extends JPanel implements LocaleChangeListener {

    private final I18nManager i18n;
    private final JComboBox<String> columnComboBox;
    private final JLabel selectLabel;
    private String previousSelection = null;
    private BiConsumer<String, String> onSelectionChangeListener;

    public XAxisPanel() {
        this.i18n = I18nManager.getInstance();

        setLayout( new FlowLayout( FlowLayout.LEFT, 10, 5 ) );
        setBorder( BorderFactory.createTitledBorder( i18n.getString( "xaxis.title" ) ) );

        selectLabel = new JLabel( i18n.getString( "xaxis.select.label" ) );
        add( selectLabel );

        columnComboBox = new JComboBox<>();
        columnComboBox.setPrototypeDisplayValue( "XXXXXXXXXXXXXXXXXXXX" );
        columnComboBox.addItemListener( e -> {
            if( e.getStateChange() == ItemEvent.SELECTED ) {
                String newSelection = (String) e.getItem();
                if( onSelectionChangeListener != null ) {
                    onSelectionChangeListener.accept( previousSelection, newSelection );
                }
                previousSelection = newSelection;
            }
        } );
        add( columnComboBox );

        // Register for locale changes
        i18n.addLocaleChangeListener( this );
    }

    @Override
    public void onLocaleChanged( Locale newLocale ) {
        ( (TitledBorder) getBorder() ).setTitle( i18n.getString( "xaxis.title" ) );
        selectLabel.setText( i18n.getString( "xaxis.select.label" ) );
        repaint();
    }

    /**
     * Set all available column names (called when Excel is loaded)
     */
    public void setAllColumnNames( List<String> columnNames ) {
        previousSelection = null;
        columnComboBox.removeAllItems();
        for( String name : columnNames ) {
            columnComboBox.addItem( name );
        }
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
     * Get selected column name
     *
     * @return selected column name, or null if nothing selected
     */
    public String getSelectedColumn() {
        return (String) columnComboBox.getSelectedItem();
    }

    /**
     * Update available columns (preserve current selection, add unoccupied columns)
     *
     * @param unselectedColumns list of currently unselected column names
     */
    public void updateAvailableColumns( List<String> unselectedColumns ) {
        String currentSelection = (String) columnComboBox.getSelectedItem();

        // Temporarily remove listener to avoid triggering unnecessary events
        var listeners = columnComboBox.getItemListeners();
        for( var listener : listeners ) {
            columnComboBox.removeItemListener( listener );
        }

        columnComboBox.removeAllItems();

        // First add current selection
        if( currentSelection != null ) {
            columnComboBox.addItem( currentSelection );
        }

        // Then add unselected columns
        for( String column : unselectedColumns ) {
            columnComboBox.addItem( column );
        }

        // Restore selection
        if( currentSelection != null ) {
            columnComboBox.setSelectedItem( currentSelection );
        }

        // Restore listeners
        for( var listener : listeners ) {
            columnComboBox.addItemListener( listener );
        }
    }
}
