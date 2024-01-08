package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AlternativeNameDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.AlternativeName;
import org.springframework.stereotype.Service;

@Service
public interface AlternativeNameService {

    public AlternativeName create(AlternativeNameDto alternativeNameDto);

    public AlternativeName creteIfNotExist(AlternativeNameDto alternativeNameDto);

    public AlternativeName findById(Long id);
}
