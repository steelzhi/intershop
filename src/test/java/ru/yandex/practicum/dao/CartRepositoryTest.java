package ru.yandex.practicum.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.CartItem;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("classpath:test-schema.sql")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CartRepositoryTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ItemRepository itemRepository;

    @AfterEach
    void clearDb() {
        cartRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void testAddItemToCart() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto savedItemDto1 = itemRepository.save(itemDto1);
        CartItem cartItem1 = new CartItem(savedItemDto1);
        CartItem savedCartItem = cartRepository.save(cartItem1);
        assertTrue(savedCartItem != null, "cartItem should exist in Db");
        assertTrue(savedCartItem.getItemDto().getName().equals(itemDto1.getName()),
                "itemDto1 name was saved incorrectly");
    }

    @Test
    void testFindAll() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 1.0, 2);
        ItemDto savedItemDto1 = itemRepository.save(itemDto1);
        ItemDto savedItemDto2 = itemRepository.save(itemDto2);
        CartItem cartItem1 = new CartItem(savedItemDto1);
        CartItem cartItem2 = new CartItem(savedItemDto2);
        cartRepository.save(cartItem1);
        cartRepository.save(cartItem2);
        List<CartItem> cartItemList = cartRepository.findAll();
        assertTrue(cartItemList.size() == 2, "List size should be 2");
        assertTrue(cartItemList.contains(cartItem2), "Cart should contain cartItem2");
    }

    @Test
    void testFindById() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 1.0, 2);
        ItemDto savedItemDto1 = itemRepository.save(itemDto1);
        ItemDto savedItemDto2 = itemRepository.save(itemDto2);
        CartItem cartItem1 = new CartItem(savedItemDto1);
        CartItem cartItem2 = new CartItem(savedItemDto2);
        cartRepository.save(cartItem1);
        cartRepository.save(cartItem2);
        Optional<CartItem> savedCartItem2 = cartRepository.findById(2);
        assertTrue(savedCartItem2.isPresent(), "cartItem2 should exist in Db");
        assertEquals(savedCartItem2.get().getItemDto().getDescription(), itemDto2.getDescription(),
                "Description was saved incorrectly");
    }

    @Test
    void testDeleteById() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2", "desc2", null, 1.0, 2);
        ItemDto savedItemDto1 = itemRepository.save(itemDto1);
        ItemDto savedItemDto2 = itemRepository.save(itemDto2);
        CartItem cartItem1 = new CartItem(savedItemDto1);
        CartItem cartItem2 = new CartItem(savedItemDto2);
        cartRepository.save(cartItem1);
        cartRepository.save(cartItem2);
        cartRepository.deleteById(1);
        List<CartItem> cartItemList = cartRepository.findAll();
        assertTrue(cartItemList.size() == 1, "List size should be 1");
        assertFalse(cartItemList.contains(cartItem1), "Cart shouldn't contain cartItem1");
    }
}
