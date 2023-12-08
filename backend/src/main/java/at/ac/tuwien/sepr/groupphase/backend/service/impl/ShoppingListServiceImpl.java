package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.IngredientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingItem;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.LabelService;
import at.ac.tuwien.sepr.groupphase.backend.service.ShoppingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ShoppingRepository shoppingRepository;
    private final LabelService labelService;
    private final ItemMapper itemMapper;

    public ShoppingListServiceImpl(ShoppingRepository shoppingRepository, LabelService labelService, ItemMapper itemMapper) {
        this.shoppingRepository = shoppingRepository;
        this.labelService = labelService;
        this.itemMapper = itemMapper;
    }

    @Override
    public ShoppingItem create(ShoppingItemDto itemDto) {
        List<ItemLabel> labels = findItemLabelsAndCreateNew(itemDto.labels());

        ShoppingItem createdItem = shoppingRepository.save(itemMapper.dtoToShopping(itemDto, labels));
        createdItem.setLabels(labels);
        return createdItem;
    }

    @Override
    public Optional<ShoppingItem> getById(Long itemId) {
        LOGGER.trace("findById({})", itemId);
        if (itemId == null) {
            return Optional.empty();
        }

        return shoppingRepository.findById(itemId);
    }

    private List<ItemLabel> findItemLabelsAndCreateNew(List<ItemLabelDto> labels) {
        if (labels == null) {
            return List.of();
        }
        List<ItemLabel> ret = labelService.findByValue(
            labels.stream()
                .map(ItemLabelDto::labelValue)
                .toList()
        );
        List<ItemLabelDto> newLabels = labels.stream()
            .filter(labelDto ->
                ret.stream()
                    .noneMatch(label ->
                        label.getLabelValue().equals(labelDto.labelValue())
                    )
            ).toList();

        if (!newLabels.isEmpty()) {
            List<ItemLabel> createdLabels = labelService.createAll(newLabels);
            ret.addAll(createdLabels);
        }
        return ret;
    }
}
