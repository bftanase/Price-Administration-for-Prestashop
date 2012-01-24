package ro.btanase.price.service;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.google.inject.Singleton;

@Singleton
public class SessionFactoryImpl implements SessionFactory {

  private SqlSessionFactory sessionFactory;
  private String username;
  private String password;
  private String hostname;
  private String dbName;

  @Override
  public SqlSessionFactory get() {
    // abort if get() was called before setConnectionProperties()
    if (username == null || password == null || hostname == null) {
      throw new IllegalStateException(
        "Connection properties are not initialized. Call setConnectionProperties before get()");
    }

    if (sessionFactory == null) {
      String resource = "mybatis/SqlMapConfig.xml";
      Reader reader;
      try {
        reader = Resources.getResourceAsReader(resource);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      Properties prop = new Properties();
      prop.put("url", "jdbc:mysql://" + hostname + "/" + dbName);
      prop.put("user", username);
      prop.put("pass", password);

      sessionFactory = new SqlSessionFactoryBuilder().build(reader, prop);
    }

    return sessionFactory;
  }

  @Override
  public void setConnectionProperties(String hostname, String dbName, String username, String password) {
    this.hostname = hostname;
    this.username = username;
    this.password = password;
    this.dbName = dbName;

  }

  @Override
  public void reset() {
    sessionFactory = null;
    
  }

}
