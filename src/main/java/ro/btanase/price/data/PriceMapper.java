package ro.btanase.price.data;

import java.util.List;
import java.util.Map;

import ro.btanase.price.data.domain.Product;

public interface PriceMapper {
  public List<Product> selectAllProducts();
  public void update(Product product);
}
