package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductDataBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookKeeperTest {

    private static final String TEST_CLIENT_NAME = "Owsiak";
    private static final String TEST_PRODUCT_DATA_NAME = "Test Product Data";
    private static final String TEST_TAX_DESCRIPTION = "Tax Description";
    private static final Tax TEST_ZERO_TAX = new Tax(Money.ZERO, TEST_TAX_DESCRIPTION);
    private static final Id TEST_CLIENT_ID = new Id("TEST_CLIENT_ID");
    private static final ClientData TEST_CLIENT = new ClientData(TEST_CLIENT_ID, TEST_CLIENT_NAME);
    private static final Id TEST_PRODUCT_ID = new Id("TEST_PRODUCT_DATA_ID");
    private static final ProductData TEST_STANDARD_PRODUCT_DATA = new ProductDataBuilder().name(TEST_PRODUCT_DATA_NAME)
                                                                                          .price(Money.ZERO)
                                                                                          .productId(TEST_PRODUCT_ID)
                                                                                          .snapshotDate(null)
                                                                                          .type(ProductType.STANDARD)
                                                                                          .build();

    @Mock
    private TaxPolicy taxPolicy;

    @Mock
    private InvoiceFactory invoiceFactory;

    private BookKeeper keeper;

    @Captor
    ArgumentCaptor<ProductType> productTypeCaptor;

    @Captor
    ArgumentCaptor<Money> moneyCaptor;

    @BeforeEach
    void setUp() {
        keeper = new BookKeeper(invoiceFactory);
    }

    @Test
    void shouldReturnOneItemInvoiceWhenThereIsOnlyOneRequestItem() {
        // given
        InvoiceRequest request = new InvoiceRequest(TEST_CLIENT);
        request.add(new RequestItem(TEST_STANDARD_PRODUCT_DATA, 1, Money.ZERO));

        Invoice invoice = new Invoice(Id.generate(), TEST_CLIENT);
        when(invoiceFactory.create(TEST_CLIENT)).thenReturn(invoice);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(TEST_ZERO_TAX);

        // when
        Invoice actualInvoice = keeper.issuance(request, taxPolicy);

        // then
        final int expectedInvoiceItemsCount = 1;
        assertEquals(expectedInvoiceItemsCount, actualInvoice.getItems()
                                                             .size());
    }

    @Test
    void shouldInvokeCalculateTaxTwiceWhenInvoiceContainsTwoItems() {
        // given
        InvoiceRequest request = new InvoiceRequest(TEST_CLIENT);
        Money money = new Money(15, Money.DEFAULT_CURRENCY);
        RequestItem firstItem = new RequestItem(TEST_STANDARD_PRODUCT_DATA, 1, Money.ZERO);
        RequestItem secondItem = new RequestItem(TEST_STANDARD_PRODUCT_DATA, 1, money);
        request.add(firstItem);
        request.add(secondItem);
        Invoice invoice = new Invoice(Id.generate(), TEST_CLIENT);
        when(invoiceFactory.create(TEST_CLIENT)).thenReturn(invoice);
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(TEST_ZERO_TAX);

        // when
        keeper.issuance(request, taxPolicy);

        // then
        verify(taxPolicy, times(2)).calculateTax(productTypeCaptor.capture(), moneyCaptor.capture());
        List<ProductType> productTypes = productTypeCaptor.getAllValues();
        List<Money> moneyList = moneyCaptor.getAllValues();

        assertEquals(ProductType.STANDARD, productTypes.get(0));
        assertEquals(ProductType.STANDARD, productTypes.get(1));

        assertEquals(Money.ZERO, moneyList.get(0));
        assertEquals(money, moneyList.get(1));
    }

}
