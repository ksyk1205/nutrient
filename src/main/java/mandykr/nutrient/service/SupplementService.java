package mandykr.nutrient.service;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.repository.SupplementRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplementService {
    private final SupplementRepository supplementRepository;



}
