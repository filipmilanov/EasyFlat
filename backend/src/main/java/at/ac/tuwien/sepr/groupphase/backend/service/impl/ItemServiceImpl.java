package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.DigitalStorage;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.DigitalStorageService;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ItemRepository itemRepository;
    private final DigitalStorageService digitalStorageService;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository, DigitalStorageService digitalStorageService, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.digitalStorageService = digitalStorageService;
        this.itemMapper = itemMapper;
    }

    @Override
    public Optional<Item> findById(Long id) {
        LOGGER.trace("findById({})", id);
        if (id == null) {
            return Optional.empty();
        }

        return itemRepository.findById(id);
    }

    @Override
    public Item create(ItemDto itemDto) throws ConflictException {
        LOGGER.trace("create({})", itemDto);

        // check for conflict

        Optional<DigitalStorage> digitalStorage = digitalStorageService.findById(itemDto.storageId());
        if (digitalStorage.isEmpty()) {
            throw new ConflictException("Digital Storage does not exists");
        }
        Item item = itemMapper.dtoToItem(itemDto, digitalStorage.get());

        return itemRepository.save(item);
    }

    @Override
    public Item update(ItemDto item) {
        return null;
    }

    @Override
    public void remove(Long id) {

    }
}
