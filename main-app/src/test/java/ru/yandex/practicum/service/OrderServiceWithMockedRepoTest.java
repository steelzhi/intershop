/*
package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dao.*;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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

    @MockitoBean
    ImageRepository imageRepository;

    @MockitoBean
    ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderRepository);
        Mockito.reset(orderItemRepository);
        Mockito.reset(cartRepository);
        Mockito.reset(imageRepository);
        Mockito.reset(itemRepository);
    }

    @Test
    void testCreateOrderNotEmpty() throws IOException {
        Order order = new Order("user");
        int orderId = 0;
        order.setId(orderId);
        Mono<Order> orderMono = Mono.just(order);

        when(orderRepository.save(any()))
                .thenReturn(orderMono);

        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));
        Item item1 = new Item("itemDto1", "abcdesc1", null, 1.0);
        int itemId1 = 1;
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(itemId1))
                .doOnNext(itemDto -> itemDto.setAmount(10));
        ItemDto itemDto1 = itemDtoMono1.block();

        int cartId1 = 1;
        CartItem cartItem1 = new CartItem(cartId1, itemId1, "user");

        Flux<CartItem> cartItemFlux = Flux.just(cartItem1);

        when(cartRepository.findAll())
                .thenReturn(cartItemFlux);

        when(itemRepository.findById(itemId1))
                .thenReturn(itemDtoMono1);

        OrderItem orderItem1 = new OrderItem(orderId, itemDto1.getId(), itemDto1.getPrice(), itemDto1.getAmount());
        Mono<OrderItem> orderItemMono = Mono.just(orderItem1);

        when(orderItemRepository.save(orderItem1))
                .thenReturn(orderItemMono);

        Mono<OrderItem> savedOrderItem1 = orderItemRepository.save(orderItem1);
        assertNotNull(savedOrderItem1,
                "savedOrderItem1 should exist");
        assertEquals(savedOrderItem1.block().getOrderId(), orderId,
                "orderId should be " + orderId);
        assertEquals(savedOrderItem1.block().getItemId(), itemId1,
                "itemId1 should be " + item1);

        when(orderRepository.findById(orderId))
                .thenReturn(orderMono);

        when(cartRepository.deleteAll())
                .thenReturn(Mono.empty());

        Mono<Order> savedOrderMono = orderService.createOrder("user");
        Order savedOrder = savedOrderMono.block();
        assertNotNull(savedOrder, "Order should exist");
        assertEquals(savedOrder.getId(), orderId, "orderId should be = " + orderId);

        verify(orderRepository, times(1)).save(order);
        verify(cartRepository, times(1)).findAll();
        verify(itemRepository, times(1)).findById(itemId1);
        verify(orderItemRepository, times(2)).save(orderItem1);
        verify(orderRepository, times(1)).findById(orderId);
        verify(cartRepository, times(1)).deleteAll();
    }

    @Test
    void testGetOrders() throws IOException {
        Order order1 = new Order("user");
        int orderId1 = 1;
        order1.setId(orderId1);
        Mono<Order> orderMono1 = Mono.just(order1);

        when(orderRepository.findById(1))
                .thenReturn(orderMono1);

        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));
        Item item1 = new Item("item1", "desc", null, 1.0);
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(1));
        ItemDto itemDto1 = itemDtoMono1.block();

        OrderItem orderItem1 = new OrderItem(orderId1, itemDto1.getId(), itemDto1.getPrice(), itemDto1.getAmount());
        Flux<OrderItem> orderItemMono = Flux.just(orderItem1);

        when(orderItemRepository.findAllByOrderId(orderId1))
                .thenReturn(orderItemMono);

        when(itemRepository.findById(1))
                .thenReturn(itemDtoMono1);


        Order order2 = new Order("user");
        int orderId2 = 2;
        order2.setId(orderId2);

        Mono<Order> orderMono2 = Mono.just(order1);

        when(orderRepository.findById(2))
                .thenReturn(orderMono2);

        byte[] imageBytes2 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image2 = new Image(imageBytes2);
        when(imageRepository.save(image2))
                .thenReturn(Mono.just(image2));
        Item item2 = new Item("item2", "desc", null, 1.0);
        Mono<ItemDto> itemDtoMono2 = ItemMapper.mapToItemDto(Mono.just(item2), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(2));
        ItemDto itemDto2 = itemDtoMono2.block();

        OrderItem orderItem2 = new OrderItem(orderId1, itemDto2.getId(), itemDto2.getPrice(), itemDto2.getAmount());
        Flux<OrderItem> orderItemMono2 = Flux.just(orderItem2);

        when(orderItemRepository.findAllByOrderId(orderId2))
                .thenReturn(orderItemMono2);

        when(itemRepository.findById(2))
                .thenReturn(itemDtoMono2);

        when(orderRepository.findAllByUsername("user"))
                .thenReturn(Flux.just(order1, order2));

        Flux<OrderDto> orderDtoFlux = orderService.getOrders("user");
        List<OrderDto> orderDtoList = orderDtoFlux.toStream().toList();

        assertTrue(orderDtoList.size() == 2, "2 orders should exist");
        assertTrue(orderDtoList.get(0).getId() == 1, "order1 should have id = 1");

        verify(orderRepository, times(1)).findAllByUsername("user");
    }

    @Test
    void testGetOrder() throws IOException {
        Order order1 = new Order("user");
        int orderId = 1;
        order1.setId(orderId);
        Mono<Order> orderMono = Mono.just(order1);

        when(orderRepository.findById(1))
                .thenReturn(orderMono);

        byte[] imageBytes1 = Files.readAllBytes(Paths.get("src\\main\\resources\\images-bytes\\armature.txt"));
        Image image1 = new Image(imageBytes1);
        when(imageRepository.save(image1))
                .thenReturn(Mono.just(image1));
        Item item1 = new Item("item1", "desc", null, 1.0);
        Mono<ItemDto> itemDtoMono1 = ItemMapper.mapToItemDto(Mono.just(item1), Mono.just(image1))
                .doOnNext(itemDto -> itemDto.setId(1));
        ItemDto itemDto1 = itemDtoMono1.block();

        OrderItem orderItem1 = new OrderItem(orderId, itemDto1.getId(), itemDto1.getPrice(), itemDto1.getAmount());
        Flux<OrderItem> orderItemMono = Flux.just(orderItem1);

        when(orderItemRepository.findAllByOrderId(1))
                .thenReturn(orderItemMono);

        when(itemRepository.findById(1))
                .thenReturn(itemDtoMono1);

        Mono<OrderDto> foundOrderDtoMono = orderService.getOrder(orderId);
        assertEquals(foundOrderDtoMono.block().getId(), orderId, "orderId should be = " + orderId);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemRepository, times(1)).findAllByOrderId(1);
        verify(itemRepository, times(1)).findById(1);
    }
}
*/
