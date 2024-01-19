package at.ac.tuwien.sepr.groupphase.backend.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringTrimConverter implements Converter<String, String> {
    @Override
    public String convert(String s) {
        return s.trim();
    }
}
