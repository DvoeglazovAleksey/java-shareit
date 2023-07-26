package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository repository;

    private final User owner = new User(null, "Павел", "p@mail.com");
    private final User requestor = new User(null, "User", "a@mail.ru");
    private final ItemRequest request = new ItemRequest(null, "description", requestor, LocalDateTime.now());
    private final Item item = new Item(null, "Кусторез", "Бывалый", true, owner, request);
    private final int size = 30;
    private final PageRequest page = PageRequest.of(0, size);

    @BeforeEach
    void setUp() {
        em.persist(requestor);
        em.persist(owner);
        em.persist(request);
        em.persist(item);
    }

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findByOwnerId_thenReturnItem() {
        List<Item> actualList = repository.findByOwnerId(owner.getId(), page);

        assertNotNull(actualList);
        assertEquals(item.getDescription(), actualList.get(0).getDescription());
    }

    @Test
    void getItemsByText_thenReturnItemsList() {
        String text = "куст";

        List<Item> actualList = repository.getItemsByText(text, page);

        assertNotNull(actualList);
        assertEquals(item.getDescription(), actualList.get(0).getDescription());
    }

    @Test
    void findAllByRequestIdIn_thenReturnItemsList() {
        List<Long> requestsIds = List.of(request.getId());
        List<Item> actualList = repository.findAllByRequestIdIn(requestsIds);

        assertNotNull(actualList);
        assertEquals(item.getDescription(), actualList.get(0).getDescription());
    }

    @Test
    void findByRequestId_thenReturnItemsList() {
        List<Item> actualList = repository.findByRequestId(request.getId());

        assertNotNull(actualList);
        assertEquals(item.getDescription(), actualList.get(0).getDescription());
    }
}