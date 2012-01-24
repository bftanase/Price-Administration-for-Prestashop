package ro.btanase.price.dao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.ibatis.session.SqlSession;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

import ro.btanase.price.data.PriceMapper;
import ro.btanase.price.data.domain.Product;
import ro.btanase.price.service.SessionFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ExportDaoImpl implements ExportDao {

  SessionFactory sessionFactory;
  
  private static final int BATCH_SIZE = 10;

  @Inject
  public ExportDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public synchronized void downloadPriceList() {
    SqlSession session = sessionFactory.get().openSession();

    try {
      PriceMapper mapper = session.getMapper(PriceMapper.class);

      List<Product> productList = mapper.selectAllProducts();

      Workbook wb = new HSSFWorkbook();
      Sheet sheet = wb.createSheet("price_list");
      CreationHelper ch = wb.getCreationHelper();
      Font font = wb.createFont();
      font.setBoldweight(Font.BOLDWEIGHT_BOLD);

      sheet.setColumnWidth(0, 10 * 256);
      sheet.setColumnWidth(1, 30 * 256);
      sheet.setColumnWidth(2, 10 * 256);
      sheet.setColumnWidth(3, 10 * 256);
      sheet.setColumnWidth(4, 30 * 256);

      // create header
      Row header = sheet.createRow(0);

      RichTextString idProduct = ch.createRichTextString("id_product");
      idProduct.applyFont(font);
      header.createCell(0).setCellValue(idProduct);

      RichTextString name = ch.createRichTextString("name");
      name.applyFont(font);
      header.createCell(1).setCellValue(name);

      RichTextString price = ch.createRichTextString("price");
      price.applyFont(font);
      header.createCell(2).setCellValue(price);

      RichTextString idCategory = ch.createRichTextString("id_category");
      idCategory.applyFont(font);
      header.createCell(3).setCellValue(idCategory);

      RichTextString categoryName = ch.createRichTextString("category_name");
      categoryName.applyFont(font);
      header.createCell(4).setCellValue(categoryName);

      // populate cells with db data
      int i = 1;
      for (Product prod : productList) {
        Row row = sheet.createRow(i);
        row.createCell(0).setCellValue((double) prod.getProductId());
        row.createCell(1).setCellValue(prod.getProductName());
        row.createCell(2).setCellValue(prod.getPrice());
        row.createCell(3).setCellValue((double) prod.getCategoryId());
        row.createCell(4).setCellValue(prod.getCategoryName());

        i++;
      }

      sheet.setAutoFilter(new CellRangeAddress(0, i, 0, 4));

      FileOutputStream fos = null;

      // write excel file
      try {
        fos = new FileOutputStream("price_list.xls");
        wb.write(fos);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } finally {
        try {
          fos.close();
        } catch (Exception e) {/* do nothing */
        }
      }
    } finally {
      session.close();
    }
  }

  @Override
  public void uploadPriceList() {
    // read & validate file
    Workbook wb;
    try {
      InputStream inp = new FileInputStream("price_list.xls");
      wb = WorkbookFactory.create(inp);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    
    Sheet sheet = wb.getSheetAt(0);
    int size = sheet.getLastRowNum();
    
    List<Product> prodList = new LinkedList<Product>();
    
    for (int i = 1; i < size+1; i++){
      Product prod = new Product();
      
      Row row = sheet.getRow(i);
      
      Cell productIdCell = row.getCell(0);
      double productId = productIdCell.getNumericCellValue();
      
      if(!(productId > 0)){
        throw new RuntimeException("price id not a valid number");
      }
      prod.setProductId((int)productId);
      
      Cell productNameCell = row.getCell(1);
      String productName = productNameCell.getStringCellValue();
      prod.setProductName(productName);
      
      Cell priceCell = row.getCell(2);
      double price = priceCell.getNumericCellValue();
      prod.setPrice(price);
      
      Cell categoryIdCell = row.getCell(3);
      double categoryId = categoryIdCell.getNumericCellValue();
      prod.setCategoryId((int)categoryId);
      
      Cell categoryNameCell = row.getCell(4);
      String categoryName = categoryNameCell.getStringCellValue();
      prod.setCategoryName(categoryName);
      
      prodList.add(prod);
    }
    
    // update database
    SqlSession session = sessionFactory.get().openSession();
    try{
      PriceMapper mapper = session.getMapper(PriceMapper.class);
      for (Product product : prodList) {
        mapper.update(product);
      }
      session.commit();
    }catch (Exception e) {
      session.rollback();
      throw new RuntimeException(e);
    }finally{
      session.close();
    }
    
    
  }

//  private void uploadList(List<Product> batchProduct) {
//    
//    SqlSession session = sessionFactory.get().openSession();
//    try{
//      PriceMapper mapper = session.getMapper(PriceMapper.class);
//      for (Product product : batchProduct) {
//        mapper.update(product);
//      }
//      session.commit();
//    }finally{
//      session.close();
//    }
//    
//
//    
//  }

}
