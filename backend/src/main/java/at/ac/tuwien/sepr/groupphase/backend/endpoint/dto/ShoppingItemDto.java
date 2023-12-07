package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;

import java.util.List;

public class ShoppingItemDto extends Item {

    private List<ItemLabel> labels;
}
