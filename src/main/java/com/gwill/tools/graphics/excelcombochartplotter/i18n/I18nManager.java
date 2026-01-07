package com.gwill.tools.graphics.excelcombochartplotter.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Internationalization manager for handling multiple languages.
 * Uses singleton pattern for global access.
 */
public class I18nManager {

    private static final String BUNDLE_NAME = "messages";
    private static final String PREF_KEY_LOCALE = "app.locale";

    private static I18nManager instance;

    private Locale currentLocale;
    private ResourceBundle bundle;
    private final List<LocaleChangeListener> listeners = new ArrayList<>();
    private final Preferences prefs;

    /**
     * Supported locales
     */
    public static final Locale[] SUPPORTED_LOCALES = {
        Locale.of( "en", "US" ),
        Locale.of( "zh", "CN" )
    };

    private I18nManager() {
        prefs = Preferences.userNodeForPackage( I18nManager.class );
        loadSavedLocale();
    }

    /**
     * Get the singleton instance
     */
    public static synchronized I18nManager getInstance() {
        if( instance == null ) {
            instance = new I18nManager();
        }
        return instance;
    }

    /**
     * Load saved locale from preferences, or use default (English US)
     */
    private void loadSavedLocale() {
        String savedLocale = prefs.get( PREF_KEY_LOCALE, null );
        if( savedLocale != null ) {
            String[] parts = savedLocale.split( "_" );
            if( parts.length >= 2 ) {
                currentLocale = Locale.of( parts[0], parts[1] );
            } else {
                currentLocale = getDefaultLocale();
            }
        } else {
            currentLocale = getDefaultLocale();
        }
        loadBundle();
    }

    /**
     * Get default locale based on system locale
     */
    private Locale getDefaultLocale() {
        Locale systemLocale = Locale.getDefault();
        // Check if system locale matches any supported locale
        for( Locale supported : SUPPORTED_LOCALES ) {
            if( supported.getLanguage().equals( systemLocale.getLanguage() ) ) {
                return supported;
            }
        }
        // Default to English (US) if no match
        return SUPPORTED_LOCALES[0];
    }

    /**
     * Load resource bundle for current locale
     */
    private void loadBundle() {
        bundle = ResourceBundle.getBundle( BUNDLE_NAME, currentLocale );
    }

    /**
     * Get current locale
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Set locale and notify listeners
     */
    public void setLocale( Locale locale ) {
        if( !locale.equals( currentLocale ) ) {
            currentLocale = locale;
            loadBundle();
            // Save to preferences
            prefs.put( PREF_KEY_LOCALE, locale.getLanguage() + "_" + locale.getCountry() );
            // Notify listeners
            notifyListeners();
        }
    }

    /**
     * Get translated string by key
     */
    public String getString( String key ) {
        try {
            return bundle.getString( key );
        } catch( Exception e ) {
            return "!" + key + "!";
        }
    }

    /**
     * Get translated string with parameters
     */
    public String getString( String key, Object... params ) {
        try {
            String pattern = bundle.getString( key );
            return MessageFormat.format( pattern, params );
        } catch( Exception e ) {
            return "!" + key + "!";
        }
    }

    /**
     * Add locale change listener
     */
    public void addLocaleChangeListener( LocaleChangeListener listener ) {
        if( !listeners.contains( listener ) ) {
            listeners.add( listener );
        }
    }

    /**
     * Remove locale change listener
     */
    public void removeLocaleChangeListener( LocaleChangeListener listener ) {
        listeners.remove( listener );
    }

    /**
     * Notify all listeners of locale change
     */
    private void notifyListeners() {
        for( LocaleChangeListener listener : listeners ) {
            listener.onLocaleChanged( currentLocale );
        }
    }

    /**
     * Get display name for a locale
     */
    public String getLocaleDisplayName( Locale locale ) {
        if( Locale.of( "en", "US" ).equals( locale ) ) {
            return getString( "language.en_US" );
        } else if( Locale.of( "zh", "CN" ).equals( locale ) ) {
            return getString( "language.zh_CN" );
        }
        return locale.getDisplayName( currentLocale );
    }

    /**
     * Listener interface for locale changes
     */
    public interface LocaleChangeListener {
        void onLocaleChanged( Locale newLocale );
    }
}
