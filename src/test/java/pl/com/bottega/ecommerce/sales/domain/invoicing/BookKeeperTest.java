package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductDataBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookKeeperTest {

    private static final String TEST_CLIENT_NAME = "Owsiak";
    private static final String TEST_PRODUCT_DATA_NAME = "Test Product Data";
    private static final Money TEST_ZERO_MONEY = Money.ZERO;
    private static final String TEST_TAX_DESCRIPTION = "Tax Description";
    private static final Tax TEST_ZERO_TAX = new Tax(TEST_ZERO_MONEY, TEST_TAX_DESCRIPTION);

    @Mock
    private TaxPolicy taxPolicy;

    @Mock
    private InvoiceFactory invoiceFactory;

    private BookKeeper keeper;

    @BeforeEach
    void setUp() {
        keeper = new BookKeeper(invoiceFactory);
    }

    @Test
    void shouldReturnOneItemInvoiceWhenThereIsOnlyOneRequestItem() {
        // given
        Id clientId = Id.generate();
        Id invoiceId = Id.generate();
        ClientData clientData = new ClientData(clientId, TEST_CLIENT_NAME);
        InvoiceRequest request = new InvoiceRequest(clientData);

        ProductData productData = new ProductDataBuilder().name(TEST_PRODUCT_DATA_NAME)
                                                          .price(TEST_ZERO_MONEY)
                                                          .productId(Id.generate())
                                                          .snapshotDate(null)
                                                          .type(ProductType.STANDARD)
                                                          .build();

        request.add(new RequestItem(productData, 1, TEST_ZERO_MONEY));

        Invoice invoice = new Invoice(invoiceId, clientData);
        when(invoiceFactory.create(clientData)).thenReturn(invoice);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(TEST_ZERO_TAX);

        // when
        Invoice actualInvoice = keeper.issuance(request, taxPolicy);

        // then
        final int expectedInvoiceItemsCount = 1;
        assertEquals(expectedInvoiceItemsCount, actualInvoice.getItems()
                                     .size());
    }



}
