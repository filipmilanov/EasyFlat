package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemLabelDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ItemLabel;

import java.util.List;

public interface LabelService {
    ItemLabel findByValueAndColour(String value, String color);

    List<ItemLabel> createAll(List<ItemLabelDto> newLabels);
}
