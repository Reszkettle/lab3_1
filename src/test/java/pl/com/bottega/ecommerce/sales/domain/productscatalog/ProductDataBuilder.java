package pl.com.bottega.ecommerce.sales.domain.productscatalog;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

public class ProductDataBuilder {

    private Id productId;
    private Money price;

    private String name;

    private Date snapshotDate;

    private ProductType type;

    public ProductDataBuilder productId(Id productId) {
        this.productId = productId;
        return this;
    }

    public ProductDataBuilder price(Money price) {
        this.price = price;
        return this;
    }

    public ProductDataBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProductDataBuilder snapshotDate(Date snapshotDate) {
        this.snapshotDate = snapshotDate;
        return this;
    }

    public ProductDataBuilder type(ProductType type) {
        this.type = type;
        return this;
    }

    public ProductData build() {
        return new ProductData(this.productId, this.price, this.name, this.type, this.snapshotDate);
    }
}
