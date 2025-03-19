package ru.yandex.practicum.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderItem;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql("classpath:test-schema.sql")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ItemRepository itemRepository;

    @AfterEach
    void clearDb() {
        itemRepository.deleteAll();
        orderRepository.deleteAll();
        orderItemRepository.deleteAll();
    }

    @Test
    void testCreateOrder() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2z", "descghy", null, 12.0, 2);
        ItemDto savedItemDto1 = itemRepository.save(itemDto1);
        ItemDto savedItemDto2 = itemRepository.save(itemDto2);

        Order order = new Order();

        OrderItem orderItem = new OrderItem(order, savedItemDto1, savedItemDto1.getAmount());
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        OrderItem orderItem2 = new OrderItem(order, savedItemDto2, savedItemDto2.getAmount());
        OrderItem savedOrderItem2 = orderItemRepository.save(orderItem2);

        order.setOrderItems(List.of(savedOrderItem, savedOrderItem2));
        Optional<Order> savedOrder = orderRepository.findById(1);

        assertTrue(savedOrder.isPresent(), "savedOrder should exist in Db");
        assertEquals(savedOrder.get().getOrderItems().get(0).getItemDto().getName(), itemDto1.getName(),
                "itemDto1s names should match");
    }

    @Test
    void testGetOrders() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto savedItemDto1 = itemRepository.save(itemDto1);

        Order order1 = new Order();

        OrderItem orderItem = new OrderItem(order1, savedItemDto1, savedItemDto1.getAmount());
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        List<OrderItem> orderItems1 = List.of(savedOrderItem);

        order1.setOrderItems(orderItems1);

        ItemDto itemDto2 = new ItemDto("itemDto2", "abcdesc1", null, 1.0, 2);
        ItemDto savedItemDto2 = itemRepository.save(itemDto2);

        Order order2 = new Order();
        OrderItem orderItem2 = new OrderItem(order2, savedItemDto2, savedItemDto2.getAmount());
        OrderItem savedOrderItem2 = orderItemRepository.save(orderItem2);
        List<OrderItem> orderItems2 = List.of(savedOrderItem2);
        order2.setOrderItems(orderItems2);

        List<Order> orders = orderRepository.findAll();
        assertEquals(orders.size(), 2, "Orders list should contain 2 orders");
        assertEquals(orders.get(1).getOrderItems().get(0).getItemDto().getDescription(), itemDto2.getDescription(),
                "itemDto2s descriptions should match");
    }

    @Test
    void testGetOrder() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto savedItemDto1 = itemRepository.save(itemDto1);

        Order order1 = new Order();

        OrderItem orderItem = new OrderItem(order1, savedItemDto1, savedItemDto1.getAmount());
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        List<OrderItem> orderItems1 = List.of(savedOrderItem);

        order1.setOrderItems(orderItems1);

        Optional<Order> savedOrder = orderRepository.findById(1);
        assertTrue(savedOrder.isPresent(), "Order should exist");
        assertEquals(order1.getOrderItems().get(0).getItemDto().getDescription(), itemDto1.getDescription(),
                "itemDto1s descriptions should match");
    }

    @Test
    void testGetSumOfAllOrders() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto savedItemDto1 = itemRepository.save(itemDto1);
        ItemDto itemDto2 = new ItemDto("itemDto2", "abcdesc1", null, 7.0, 2);
        ItemDto savedItemDto2 = itemRepository.save(itemDto2);

        Order order1 = new Order();

        double totalSum = 0;
        OrderItem orderItem = new OrderItem(order1, savedItemDto1, savedItemDto1.getAmount());
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        totalSum += savedOrderItem.getItemAmount() * savedOrderItem.getItemDto().getPrice();
        OrderItem orderItem2 = new OrderItem(order1, savedItemDto2, savedItemDto2.getAmount());
        OrderItem savedOrderItem2 = orderItemRepository.save(orderItem2);
        List<OrderItem> orderItems1 = List.of(savedOrderItem, savedOrderItem2);
        totalSum += savedOrderItem2.getItemAmount() * savedOrderItem2.getItemDto().getPrice();

        order1.setOrderItems(orderItems1);
        order1.setTotalSum(totalSum);

        double totalSum2 = orderRepository.getSumOfAllOrders();
        assertEquals(totalSum2, totalSum, "Sum was processed incorrectly");
    }
}
