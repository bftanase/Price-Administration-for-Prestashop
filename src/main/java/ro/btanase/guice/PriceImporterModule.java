package ro.btanase.guice;

import ro.btanase.dao.ExportDao;
import ro.btanase.dao.ExportDaoImpl;
import ro.btanase.gui.MainWindow;
import ro.btanase.service.SessionFactory;
import ro.btanase.service.SessionFactoryImpl;
import ro.btanase.service.SettingsService;
import ro.btanase.service.SettingsServiceImpl;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class PriceImporterModule extends AbstractModule {

  @Override
  protected void configure() {
//    bind(JdbcService.class).to(JdbcServiceImpl.class);
    bind(ExportDao.class).to(ExportDaoImpl.class);
    bind(SettingsService.class).to(SettingsServiceImpl.class);
    bind(SessionFactory.class).to(SessionFactoryImpl.class);
   
//    bindListener(Matchers.subclassesOf(MainWindow.class), new TypeListener() {
//      
//      @Override
//      public <I> void hear(final TypeLiteral<I> type, TypeEncounter<I> encounter) {
//        encounter.register(new InjectionListener() {
//
//          @Override
//          public void afterInjection(Object injectee) {
//            MainWindow mainWindow = (MainWindow) injectee;
//            mainWindow.init();
//            
//          }
//        });
//        
//      }
//    });

  }


}
