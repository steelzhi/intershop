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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql("classpath:test-schema.sql")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class OrderItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @AfterEach
    void clearDb() {
        itemRepository.deleteAll();
        orderRepository.deleteAll();
        orderItemRepository.deleteAll();
    }

    @Test
    void testSaveOrderItem() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto savedItemDto1 = itemRepository.save(itemDto1);

        Order order = new Order();

        OrderItem orderItem = new OrderItem(order, savedItemDto1, savedItemDto1.getAmount());
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        assertTrue(savedOrderItem != null, "savedOrderItem should exist in Db");
        assertEquals(savedOrderItem.getItemDto().getName(), itemDto1.getName(), "itemDto1s names should match");
    }
}
