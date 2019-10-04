package com.github.roookeee.datus.performance;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.immutable.ConstructorParameter;
import com.github.roookeee.datus.performance.model.dto.OrderDTO;
import com.github.roookeee.datus.performance.model.dto.ProductDTO;
import com.github.roookeee.datus.performance.model.entity.Address;
import com.github.roookeee.datus.performance.model.entity.Customer;
import com.github.roookeee.datus.performance.model.entity.Order;
import com.github.roookeee.datus.performance.model.entity.OrderFactory;
import com.github.roookeee.datus.performance.model.entity.Product;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.opentest4j.TestAbortedException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
The benchmark and all related data objects are derived variations of the following benchmark suite:
https://github.com/arey/java-object-mapper-benchmark

Special thanks to https://github.com/arey for his work
*/
public class PerformanceBenchmarkTest {

    //explicit skipping via a property makes it easier to run the benchmarks through an IDE as the property is most likely not set
    private static final boolean SKIP_BENCHMARKS = Boolean.TRUE.toString().equals(System.getProperty("datus.tests.benchmarks.skip"));

    @Test
    public void benchmark() throws Exception {
        try {
            assumeFalse(SKIP_BENCHMARKS, "Benchmark tests were skipped via datus.tests.benchmarks.skip=true");
        } catch(TestAbortedException ex) {
            System.out.println("Benchmark tests were skipped via datus.tests.benchmarks.skip=true");
            throw ex;
        }

        Options opts = new OptionsBuilder()
                .include(PerformanceBenchmarkTest.Suite.class.getSimpleName())
                .warmupIterations(5)
                .warmupTime(TimeValue.seconds(2))
                .measurementIterations(5)
                .measurementTime(TimeValue.seconds(2))
                .jvmArgs("-server")
                .forks(1)
                .build();

        new Runner(opts).run();
    }

    @Test
    public void testedConversionShouldBeCorrect() {
        //given
        Order order = OrderFactory.buildOrder();
        List<Product> products = order.getProducts();

        //when
        OrderDTO result = Suite.orderMapper.convert(order);
        List<ProductDTO> productDTOs = result.getProducts();

        //then order is valid
        assertThat(result.getBillingCity()).isEqualTo(order.getCustomer().getBillingAddress().getCity());
        assertThat(result.getBillingStreetAddress()).isEqualTo(order.getCustomer().getBillingAddress().getStreet());
        assertThat(result.getShippingCity()).isEqualTo(order.getCustomer().getShippingAddress().getCity());
        assertThat(result.getShippingStreetAddress()).isEqualTo(order.getCustomer().getShippingAddress().getStreet());
        assertThat(result.getCustomerName()).isEqualTo(order.getCustomer().getName());

        //then products are valid
        assertThat(productDTOs).hasSize(products.size());
        for (int i = 0; i < productDTOs.size(); i++) {
            Product product = products.get(i);
            ProductDTO productDTO = productDTOs.get(i);

            assertThat(productDTO).matches(dto -> product.getName().equals(dto.getName()));
        }
    }

    @State(Scope.Benchmark)
    public static class Suite {
        private static final Mapper<Product, ProductDTO> productMapper = Datus.forTypes(Product.class, ProductDTO.class)
                .immutable(ProductDTO::new)
                .from(Product::getName).to(ConstructorParameter::bind)
                .build();

        private static final Mapper<Order, OrderDTO> orderMapper = Datus.forTypes(Order.class, OrderDTO.class)
                .mutable(OrderDTO::new)
                .from(Order::getCustomer).nullsafe()
                .map(Customer::getName).into(OrderDTO::setCustomerName)
                .from(Order::getCustomer).nullsafe()
                .map(Customer::getBillingAddress).map(Address::getCity).into(OrderDTO::setBillingCity)
                .from(Order::getCustomer).nullsafe()
                .map(Customer::getBillingAddress).map(Address::getStreet).into(OrderDTO::setBillingStreetAddress)
                .from(Order::getCustomer).nullsafe()
                .map(Customer::getShippingAddress).map(Address::getCity).into(OrderDTO::setShippingCity)
                .from(Order::getCustomer).nullsafe()
                .map(Customer::getShippingAddress).map(Address::getStreet).into(OrderDTO::setShippingStreetAddress)
                .from(Order::getProducts).nullsafe()
                .map(productMapper::convert).into(OrderDTO::setProducts)
                .build();

        private Order order;

        @Setup(Level.Iteration)
        public void preInit() {
            order = OrderFactory.buildOrder();
        }

        @Benchmark
        public OrderDTO convert() {
            return orderMapper.convert(order);
        }

    }
}
