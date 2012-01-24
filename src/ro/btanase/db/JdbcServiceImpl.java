package ro.btanase.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class JdbcServiceImpl implements JdbcService {

  
  private String user = "agheorg_presta";
  private String password = "6Jgm9hZHQbJy";
  private Connection con;
  private static Logger log = Logger.getLogger(JdbcServiceImpl.class);
  

  
  public JdbcServiceImpl() {
  
    String url = "jdbc:mysql://prestashop.bmarket.ro/agheorg_presta";  

    try {
      con = DriverManager.getConnection(url, user, password);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see ro.btanase.chordlearning.services.JdbcService#getCon()
   */
  @Override
  public Connection getCon() {
    return con;
  }

}
