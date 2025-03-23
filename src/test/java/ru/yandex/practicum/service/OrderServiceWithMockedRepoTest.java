package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.dao.CartRepository;
import ru.yandex.practicum.dao.OrderItemRepository;
import ru.yandex.practicum.dao.OrderRepository;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = OrderService.class)
public class OrderServiceWithMockedRepoTest {
    @Autowired
    OrderService orderService;

    @MockitoBean
    OrderRepository orderRepository;

    @MockitoBean
    OrderItemRepository orderItemRepository;

    @MockitoBean
    CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderRepository);
    }

    @Test
    void testCreateOrderNotEmpty() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2z", "descghy", null, 12.0, 2);
        CartItem cartItem1 = new CartItem(1, itemDto1);
        CartItem cartItem2 = new CartItem(2, itemDto2);

        when(cartRepository.findAll())
                .thenReturn(List.of(cartItem1, cartItem2));

        Order order = new Order();
        Order savedOrder = new Order();

        when(orderRepository.save(order))
                .thenReturn(savedOrder);

        List<OrderItem> orderItems = new ArrayList<>();
        double totalSum = 0;
        OrderItem orderItem1 = new OrderItem(order, cartItem1.getItemDto(), itemDto1.getAmount());
        OrderItem orderItem2 = new OrderItem(order, cartItem2.getItemDto(), itemDto2.getAmount());
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);
        totalSum += orderItem1.getItemAmount() * orderItem1.getItemDto().getPrice();
        totalSum += orderItem2.getItemAmount() * orderItem2.getItemDto().getPrice();

        when(orderItemRepository.save(orderItem1))
                .thenReturn(orderItem1);
        when(orderItemRepository.save(orderItem2))
                .thenReturn(orderItem2);

        OrderItem savedOrderItem1 = orderItemRepository.save(orderItem1);
        OrderItem savedOrderItem2 = orderItemRepository.save(orderItem2);
        assertEquals(savedOrderItem1.getItemDto(), orderItem1.getItemDto(),
                "ItemDto in savedOrderItem doesn't match to original itemDto");
        assertEquals(savedOrderItem2.getItemDto(), orderItem2.getItemDto(),
                "ItemDto in savedOrderItem doesn't match to original itemDto");

        savedOrder.setOrderItems(orderItems);
        savedOrder.setTotalSum(totalSum);

        when(orderRepository.save(order))
                .thenReturn(savedOrder);
        doNothing().when(cartRepository).deleteAll();
        Order secondTimeSavedOrder = orderService.createOrder();
        assertNotNull(secondTimeSavedOrder, "savedOrder was saved incorrectly");
        assertTrue(secondTimeSavedOrder.getOrderItems().get(0).getItemDto().getAmount()
                   == orderItem1.getItemDto().getAmount(),
                "order doesn't contain savedOrderItem1");
        assertTrue(secondTimeSavedOrder.getTotalSum() == totalSum, "saved total sum is incorrect");

        verify(cartRepository, times(1)).findAll();
        verify(orderRepository, times(1)).save(order);
        verify(orderItemRepository, times(1)).save(orderItem1);
        verify(orderItemRepository, times(1)).save(orderItem2);
        verify(orderRepository, times(1)).save(savedOrder);
        verify(cartRepository, times(1)).deleteAll();
    }

    @Test
    void testGetOrders() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2z", "descghy", null, 12.0, 2);
        Order order1 = new Order();
        Order order2 = new Order();
        List<OrderItem> orderItems1 = List.of(
                new OrderItem(order1, itemDto1, itemDto1.getAmount())
        );
        List<OrderItem> orderItems2 = List.of(
                new OrderItem(order2, itemDto2, itemDto2.getAmount())
        );
        order1.setOrderItems(orderItems1);
        order2.setOrderItems(orderItems2);
        when(orderRepository.findAll())
                .thenReturn(List.of(order1, order2));

        List<Order> orders = orderService.getOrders();
        assertTrue(orders.size() == 2, "Orders list don't contain all orders");
        assertEquals(orders.get(0).getOrderItems(), orderItems1, "Order # 1 don't contain orderItems1");

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetOrder() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2z", "descghy", null, 12.0, 2);
        Order order1 = new Order();
        Order order2 = new Order();
        List<OrderItem> orderItems1 = List.of(
                new OrderItem(order1, itemDto1, itemDto1.getAmount())
        );
        List<OrderItem> orderItems2 = List.of(
                new OrderItem(order2, itemDto2, itemDto2.getAmount())
        );
        order1.setOrderItems(orderItems1);
        order2.setOrderItems(orderItems2);
        when(orderRepository.findById(1))
                .thenReturn(Optional.of(order2));

        Order foundOrder2 = orderService.getOrder(1);
        assertTrue(foundOrder2 != null, "Order 2 was not saved to DB");
        assertEquals(foundOrder2.getOrderItems(), orderItems2, "Found order 2 doesn't contain orderItems2");

        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    void testGetOrdersTotalSum() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2z", "descghy", null, 12.0, 2);
        Order order1 = new Order();
        List<OrderItem> orderItems1 = List.of(
                new OrderItem(order1, itemDto1, itemDto1.getAmount()),
                new OrderItem(order1, itemDto2, itemDto2.getAmount())
        );

        double totalSum = 0;
        for (OrderItem orderItem : orderItems1) {
            totalSum += orderItem.getItemAmount() * orderItem.getItemDto().getPrice();
        }

        order1.setOrderItems(orderItems1);
        order1.setTotalSum(totalSum);
        when(orderRepository.findById(1))
                .thenReturn(Optional.of(order1));
        double totalSum2 = itemDto1.getPrice() * itemDto1.getAmount() + itemDto2.getPrice() * itemDto2.getAmount();
        Optional<Order> savedOrder = orderRepository.findById(1);
        assertEquals(savedOrder.get().getTotalSum(), totalSum2, "Sum was processed incorrectly");
    }
}
