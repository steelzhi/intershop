/*
package ru.yandex.practicum.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.ItemDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Sql("classpath:test-schema.sql")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ImageRepository imageRepository;

    @AfterEach
    void clearDb() {
        itemRepository.deleteAll();
    }

*/
/*    @Test
    void testGetItemsList() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2z", "descghy", null, 12.0, 2);
        ItemDto itemDto3 = new ItemDto("itemDto3", "desczzy", null, 4.0, 5);
        itemRepository.save(itemDto1);
        itemRepository.save(itemDto2);
        itemRepository.save(itemDto3);
        List<ItemDto> itemDtos = itemRepository.findAll();
        assertTrue(itemDtos.size() == 3, "List should contain 3 items");
        assertTrue(itemDtos.get(0).getName().equals(itemDto1.getName()), "name of itemDto1 should match");
    }

    @Test
    void testGetItemDtoById() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        itemRepository.save(itemDto1);
        Optional<ItemDto> savedItemDto1 = itemRepository.findById(1);
        assertTrue(savedItemDto1.isPresent(), "itemDto1 should exist in Db");
        assertEquals(savedItemDto1.get().getAmount(), 2, "itemDto1's amount should be 2");
    }*//*


    */
/*@Test
    void testSearch() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "abcdesc1", null, 1.0, 2);
        ItemDto itemDto2 = new ItemDto("itemDto2z", "descghy", null, 12.0, 2);
        ItemDto itemDto3 = new ItemDto("itemDto3", "desczzy", null, 4.0, 5);
        itemRepository.save(itemDto1);
        itemRepository.save(itemDto2);
        itemRepository.save(itemDto3);

        List<ItemDto> foundAndSortedById =
                itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderById(
                        "dto", "dto");
        assertTrue(foundAndSortedById.size() == 3, "List should contain 3 items");
        assertTrue(foundAndSortedById.get(2).getId() == 3, "Last item in list should be itemDto3");

        List<ItemDto> foundAndSortedByName =
                itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByName(
                        "z", "z");
        assertTrue(foundAndSortedByName.size() == 2, "List should contain 3 items");
        assertTrue(foundAndSortedByName.get(0).getId() == 2, "First item in list should be itemDto2");

        List<ItemDto> foundAndSortedByPrice =
        itemRepository.findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingOrderByPrice(
                "DTO", "DTO");
        assertTrue(foundAndSortedByPrice.size() == 3, "List should contain 3 items");
        assertTrue(foundAndSortedByPrice.get(2).getId() == 2, "Last item in list should be itemDto2");
    }*//*


    @Test
    void testAddItemDto() {
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, 2);
        itemDto1.setId(1);
        Mono<ItemDto> savedItemDto1Mono = itemRepository.save(itemDto1);
        ItemDto savedItemDto1 = savedItemDto1Mono.block();

        assertTrue(savedItemDto1.equals(itemDto1), "itemDto1 should exist in Db");
        //assertEquals(savedItemDto1.getPrice(), 1.0, "itemDto1's price should be 1.0");
    }

   */
/* @Test
    void testDecreaseItemAmount() {
        int amount = 2;
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, amount);
        itemRepository.save(itemDto1);
        itemDto1.setAmount(itemDto1.getAmount() - 1);
        ItemDto savedItemDto1 = itemRepository.save(itemDto1);
        assertTrue(savedItemDto1.getAmount() == amount - 1, "Amount was decreased incorrectly");
    }

    @Test
    void testIncreaseItemAmount() {
        int amount = 2;
        ItemDto itemDto1 = new ItemDto("itemDto1", "desc1", null, 1.0, amount);
        itemRepository.save(itemDto1);
        itemDto1.setAmount(itemDto1.getAmount() + 1);
        ItemDto savedItemDto1 = itemRepository.save(itemDto1);
        assertTrue(savedItemDto1.getAmount() == amount + 1, "Amount was increased incorrectly");
    }*//*

}
*/
