package ro.btanase.gui;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileMonitor;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;

import net.miginfocom.swing.MigLayout;
import ro.btanase.dao.ExportDao;
import ro.btanase.service.SessionFactory;
import ro.btanase.service.SettingsService;

import com.google.inject.Inject;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JProgressBar;

public class MainWindow extends JFrame implements FileListener {

  private JPanel contentPane;

  @Inject
  private ExportDao exportDao;

  @Inject
  private SettingsService settingsService;

  @Inject
  private SessionFactory sessionFactory;

  // private File downloadFile = new File("price_list.xls");
  private String downloadFileName = "price_list.xls";

  private boolean fileChanged = false;

  private DefaultFileMonitor fileMonitor;

  private JTextField tfHostname;
  private JTextField tfUsername;
  private JPasswordField tfPassword;
  private JPasswordField tfPasswordConfirm;
  private JTextField tfDbName;
  private Properties settings;

  public static final String PROP_KEY_HOST = "hostname";
  public static final String PROP_KEY_USER = "username";
  public static final String PROP_KEY_PASS = "password";
  public static final String PROP_KEY_DB = "dbname";

  private JButton btnConnect;

  private JButton btnDownloadPrices;

  private JButton btnUploadPrices;

  private FileObject downloadFileObject;
  private JProgressBar progressBar;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MainWindow frame = new MainWindow();
          frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public MainWindow() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        updateSettings();

        settingsService.saveSettings(settings);
      }

    });

    setTitle("Prestashop");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 298);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new MigLayout("", "[][grow][]", "[][][][][][][][][]"));

    JPanel panel = new JPanel();
    panel.setBorder(new TitledBorder(null, "Setari conexiune baza de date:", TitledBorder.LEADING, TitledBorder.TOP,
        null, null));
    contentPane.add(panel, "cell 1 2 2 1,grow");
    panel.setLayout(new MigLayout("", "[][][grow]", "[][][grow][][][][]"));

    JLabel lblHostname = new JLabel("Hostname:");
    panel.add(lblHostname, "cell 1 1,alignx trailing");

    tfHostname = new JTextField();
    panel.add(tfHostname, "cell 2 1,growx");
    tfHostname.setColumns(10);

    JLabel lblNumeBazaDate = new JLabel("Nume baza date:");
    panel.add(lblNumeBazaDate, "cell 1 2,alignx trailing");

    tfDbName = new JTextField();
    panel.add(tfDbName, "cell 2 2,growx");
    tfDbName.setColumns(10);

    JLabel lblUsername = new JLabel("Utilizator:");
    panel.add(lblUsername, "cell 1 3,alignx trailing");

    tfUsername = new JTextField();
    panel.add(tfUsername, "cell 2 3,growx");
    tfUsername.setColumns(10);

    JLabel lblPassword = new JLabel("Parola:");
    panel.add(lblPassword, "cell 1 4,alignx trailing");

    tfPassword = new JPasswordField();
    panel.add(tfPassword, "cell 2 4,growx");

    JLabel lblConfirmPassword = new JLabel("Confirmare parola:");
    panel.add(lblConfirmPassword, "cell 1 5,alignx trailing");

    tfPasswordConfirm = new JPasswordField();
    panel.add(tfPasswordConfirm, "cell 2 5,growx");

    btnConnect = new JButton("Conectare");
    btnConnect.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnConnectAction();

      }
    });
    panel.add(btnConnect, "cell 1 6 2 1,alignx center");

    btnDownloadPrices = new JButton("Descarcare preturi");
    btnDownloadPrices.setEnabled(false);
    btnDownloadPrices.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnDownloadAction();
      }
    });
    contentPane.add(btnDownloadPrices, "cell 1 3");

    btnUploadPrices = new JButton("Incarcare preturi");
    btnUploadPrices.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnUploadPricesAction();
      }
    });
    btnUploadPrices.setEnabled(false);
    contentPane.add(btnUploadPrices, "cell 2 3");

    progressBar = new JProgressBar();
    contentPane.add(progressBar, "cell 1 6 2 1,growx");

  }

  protected void btnUploadPricesAction() {
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){

      @Override
      protected Void doInBackground() throws Exception {
        exportDao.uploadPriceList();
        return null;
      }

      @Override
      protected void done() {
        progressBar.setIndeterminate(false);
        try{
          get();
          btnUploadPrices.setEnabled(false);
        }catch (Exception e) {
          btnUploadPrices.setEnabled(true);
          JOptionPane.showMessageDialog(MainWindow.this,
              "A aparut o eroare in timpul actualizarii. Eroarea a fost:\n" +
              e.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
      }
      
      
    };
    
    progressBar.setIndeterminate(true);
    worker.execute();

    

  }

  protected void btnDownloadAction() {
    // stop monitoring if there is one already started
    if (fileMonitor != null) {
      fileMonitor.stop();
    }

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){

      @Override
      protected Void doInBackground() throws Exception {
        Thread.sleep(3000);
        exportDao.downloadPriceList();
        try {
          Desktop.getDesktop().open(new File(downloadFileName));
          startFileMonitor();

        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        
        return null;
      }

      @Override
      protected void done() {
        try {
          btnDownloadPrices.setEnabled(true);          
          progressBar.setIndeterminate(false);
          get();
        } catch (Exception e) {
          if (ExceptionUtils.indexOfThrowable(e, FileNotFoundException.class) != -1) {
            JOptionPane.showMessageDialog(MainWindow.this, "Nu a putut fi creat fisierul de download.\n "
                + "E posibil ca un fisier cu acelasi nume sa fie deja deschis", "Eroare creare fisier",
                JOptionPane.ERROR_MESSAGE);
            return;
          } else {
            throw new RuntimeException(e);
          }

        }
      }
    };
    
    progressBar.setIndeterminate(true);
    btnDownloadPrices.setEnabled(false);
    worker.execute();
    

  }

  private void startFileMonitor() throws FileSystemException {
    fileMonitor = new DefaultFileMonitor(this);
    FileSystemManager fsManager = VFS.getManager();
    downloadFileObject = fsManager.toFileObject(new File(downloadFileName));
    fileMonitor.addFile(downloadFileObject);
    System.out.println("Starting monitor thread ...");
    fileMonitor.start();
  }

  protected void btnConnectAction() {
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

      @Override
      protected Void doInBackground() throws Exception {
        sessionFactory.setConnectionProperties(settings.getProperty(PROP_KEY_HOST), settings.getProperty(PROP_KEY_DB),
            settings.getProperty(PROP_KEY_USER), settings.getProperty(PROP_KEY_PASS));
        SqlSession session = null;
        // reset any potential existing connections
        sessionFactory.reset();
        try {
          session = sessionFactory.get().openSession();
          settingsService.saveSettings(settings);
        } finally {
          try {
            session.close();
          } catch (Exception e) { /* nothing to do */
          }
        }

        return null;
      }

      @Override
      protected void done() {
        progressBar.setIndeterminate(false);
        try {
          get();
          setControlsSate(true);
        } catch (Exception e) {
          btnConnect.setEnabled(true);
          JOptionPane.showMessageDialog(MainWindow.this, "Eroare de conexiune: \n" + e.getMessage(),
              "Eroare conexiune", JOptionPane.ERROR_MESSAGE);
        }
      }

    };

    progressBar.setIndeterminate(true);

    String password = new String(tfPassword.getPassword());
    String passwordConfirm = new String(tfPasswordConfirm.getPassword());

    if (!password.equals(passwordConfirm)) {
      progressBar.setIndeterminate(false);
      JOptionPane.showMessageDialog(MainWindow.this, "Parolele nu se potrivesc", "Parola invalida",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    updateSettings();

    btnConnect.setEnabled(false);
    worker.execute();
  }

  private void setControlsSate(boolean connectionSuccessful) {
    if (connectionSuccessful) {
      btnConnect.setEnabled(false);
      btnDownloadPrices.setEnabled(true);
      // btnUploadPrices.setEnabled(true);

      tfDbName.setEnabled(false);
      tfHostname.setEnabled(false);
      tfPassword.setEnabled(false);
      tfPasswordConfirm.setEnabled(false);
      tfUsername.setEnabled(false);
    }

  }

  @Inject
  public void init() {
    System.out.println("Test");
    try {
      settings = settingsService.readSettings();
      tfDbName.setText(settings.getProperty(PROP_KEY_DB));
      tfHostname.setText(settings.getProperty(PROP_KEY_HOST));
      tfUsername.setText(settings.getProperty(PROP_KEY_USER));
      tfPassword.setText(settings.getProperty(PROP_KEY_PASS));
      tfPasswordConfirm.setText(settings.getProperty(PROP_KEY_PASS));
    } catch (RuntimeException e) {
      if (ExceptionUtils.indexOfThrowable(e, FileNotFoundException.class) != -1) {
        // do nothing for now
      } else {
        throw e;
      }
    }
  }

  private void updateSettings() {
    if (settings == null) {
      settings = new Properties();
    }

    settings.put(PROP_KEY_HOST, tfHostname.getText());
    settings.put(PROP_KEY_DB, tfDbName.getText());
    settings.put(PROP_KEY_USER, tfUsername.getText());
    settings.put(PROP_KEY_PASS, new String(tfPassword.getPassword()));
  }

  @Override
  public void fileCreated(FileChangeEvent event) throws Exception {
    System.out.println("created!!!");

  }

  @Override
  public void fileDeleted(FileChangeEvent event) throws Exception {
    System.out.println("File deleted!!!");

  }

  @Override
  public void fileChanged(FileChangeEvent event) throws Exception {
    System.out.println("File changed!!!");

    btnUploadPrices.setEnabled(true);
  }

}
