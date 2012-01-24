package ro.btanase.price.service;

import java.util.Properties;

/**
 * Classes implementing this interface must define mechanism for saving and retrieving 
 * application specific settings
 * @author b.tanase
 *
 */

public interface SettingsService {
  public Properties readSettings();
  
  public void saveSettings(Properties settings);
}
