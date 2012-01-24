package ro.btanase.db;

import java.sql.Connection;

public interface JdbcService {

  public abstract Connection getCon();

}