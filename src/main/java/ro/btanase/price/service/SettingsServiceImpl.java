package ro.btanase.price.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsServiceImpl implements SettingsService {

  
  @Override
  public Properties readSettings() {
    FileInputStream fis = null;
    Properties prop = new Properties();
    try{
      fis = new FileInputStream("application.properties");
      prop.load(fis);
    }catch (IOException e){
      throw new RuntimeException(e);
    }
    
    try {
      fis.close();
    } catch (IOException e) { /* do nothing */ }

    return prop;
  }

  @Override
  public void saveSettings(Properties prop) {
    
    FileOutputStream fos;
    
    try {
      fos = new FileOutputStream("application.properties");
      prop.store(fos, "Application settings");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    try {
      fos.close();
    } catch (IOException e) {/* do nothing */   }
  }

}
