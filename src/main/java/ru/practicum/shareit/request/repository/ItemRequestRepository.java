package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorId(long requestorId);

    List<ItemRequest> findByRequestorIdNot(long requestorId, PageRequest page);

//    ItemRequest findById(long requestId);
//    @Override
//    public List<ItemRequest> findAll(Sort sort) {
//        return null;
//    }
//
//    @Override
//    public Page<ItemRequest> findAll(Pageable pageable) {
//        return null;
//    }
}
