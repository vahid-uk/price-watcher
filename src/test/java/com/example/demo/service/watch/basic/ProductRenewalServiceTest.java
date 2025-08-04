package com.example.demo.service.watch.basic;

import com.example.demo.dto.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * This is more of an outside-in Test-Driven Development approach with mocks used
 * will ensure tests still work even if underlying json or data was to change.
 */
@ExtendWith(MockitoExtension.class)
class ProductRenewalServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProductRenewalService productRenewalService;

    private static final String DUMMY_URL = "http://dummyurl.com";

    private static final BigDecimal DESIRED_PRICE = new BigDecimal("50.00");


    @Test
    void onRenew_WhenPriceDrops_ShouldCallNotificationService() {
        BigDecimal currentPrice = new BigDecimal("45.00");
        Product product = new Product(1L, "Test Product", DUMMY_URL, currentPrice);
        when(productService.findByUrl(DUMMY_URL)).thenReturn(Optional.of(product));

        productRenewalService.onRenew(DUMMY_URL, DESIRED_PRICE);

        verify(productService, times(1)).findByUrl(DUMMY_URL);
        verify(notificationService, times(1)).
                notifyUser(eq(DUMMY_URL), eq(DESIRED_PRICE), eq(currentPrice), any());
    }

    @Test
    void onRenew_WhenPriceEqualsDesiredPrice_ShouldCallNotificationService() {
        BigDecimal currentPrice = new BigDecimal("50.00");
        Product product = new Product(2L, "Test Product 2", DUMMY_URL, currentPrice);
        when(productService.findByUrl(DUMMY_URL)).thenReturn(Optional.of(product));

        productRenewalService.onRenew(DUMMY_URL, DESIRED_PRICE);

        verify(productService, times(1)).findByUrl(DUMMY_URL);
        verify(notificationService, times(1)).
                notifyUser(eq(DUMMY_URL), eq(DESIRED_PRICE), eq(currentPrice), any());
    }


    @Test
    void onRenew_WhenPriceIsAboveDesiredPrice_ShouldNotCallNotificationService() {
        BigDecimal currentPrice = new BigDecimal("60.00");
        Product product = new Product(3L, "Test Product 3", DUMMY_URL, currentPrice);
        when(productService.findByUrl(DUMMY_URL)).thenReturn(Optional.of(product));

        productRenewalService.onRenew(DUMMY_URL, DESIRED_PRICE);

        verify(productService, times(1)).findByUrl(DUMMY_URL);
        verify(notificationService, never()).notifyUser(any(), any(), any(), any());
    }


    @Test
    void onRenew_WhenProductNotFound_ShouldDoNothing() {
        when(productService.findByUrl(DUMMY_URL)).thenReturn(Optional.empty());

        productRenewalService.onRenew(DUMMY_URL, DESIRED_PRICE);

        verify(productService, times(1)).findByUrl(DUMMY_URL);
        verifyNoInteractions(notificationService);
    }
}