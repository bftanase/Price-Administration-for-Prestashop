package ro.btanase.price.service;

import org.apache.ibatis.session.SqlSessionFactory;

public interface SessionFactory {
  /**
   * Setup basic properties for connecting to a mysql database
   * Must be called before get()
   * @param hostname
   * @param username
   * @param password
   */
  public void setConnectionProperties(String hostname, String dbName, String username, String password);

  /**
   * retrieves (and/or creates) a MyBatis Session Factory
   * @return
   */
  public SqlSessionFactory get();
  
  /**
   * Resets a session factory to allow a new one to be created. Calling this method will force get() to create a new 
   * MyBatis session factory.
   */
  public void reset();
}
