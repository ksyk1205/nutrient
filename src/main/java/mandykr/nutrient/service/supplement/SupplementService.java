package mandykr.nutrient.service.supplement;

import lombok.RequiredArgsConstructor;
import mandykr.nutrient.repository.supplement.SupplementRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplementService {
    private final SupplementRepository supplementRepository;



}
