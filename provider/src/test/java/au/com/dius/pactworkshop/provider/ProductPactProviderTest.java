package au.com.dius.pactworkshop.provider;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.apache.hc.core5.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;

@Provider("ProductService")
@PactFolder("src/test/resources/pacts")

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductPactProviderTest {
    @LocalServerPort
    int port;

    @MockBean
    private ProductRepository productRepository;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context, HttpRequest request) {
        context.verifyInteraction();
    }

    @State("products exist")
    void toProductsExistState() {
        when(productRepository.fetchAll()).thenReturn(
                Arrays.asList(new Product("09", "CREDIT_CARD", "Gem Visa", "v1"),
                        new Product("10", "CREDIT_CARD", "28 Degrees", "v1")));
    }

    @State({
            "no products exist",
            "product with ID 11 does not exist"
    })
    void toNoProductsExistState() {
        when(productRepository.fetchAll()).thenReturn(Collections.emptyList());
    }

    @State("product with ID 10 exists")
    Map<String, String> toProductWithIdTenExistsState() {
        String anId = "10";
		when(productRepository.getById(anId)).thenReturn(Optional.of(new Product(anId, "CREDIT_CARD", "28 Degrees", "v1")));
		return Collections.singletonMap("id", anId);
    }
}
