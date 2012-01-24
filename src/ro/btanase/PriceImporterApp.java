package ro.btanase;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import ro.btanase.dao.ExportDao;
import ro.btanase.data.PriceMapper;
import ro.btanase.gui.MainWindow;
import ro.btanase.guice.PriceImporterModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class PriceImporterApp {

  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    
//    String resource = "ro/btanase/data/SqlMapConfig.xml";
//    Reader reader =  Resources.getResourceAsReader(resource);
//    SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
//    
//    SqlSession session = sessionFactory.openSession();
//    
//    PriceMapper pm = session.getMapper(PriceMapper.class);
//    
//    List<Map> resultList = pm.selectAllProducts();
//    
//    for (Map map : resultList) {
//      System.out.println(map.get("id_product"));
//    }
//    
//    session.close();
    
    final Injector injector = Guice.createInjector(new PriceImporterModule());

    EventQueue.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        MainWindow wnd = injector.getInstance(MainWindow.class);
        wnd.setVisible(true);
        
        
      }
    });
  }

}
