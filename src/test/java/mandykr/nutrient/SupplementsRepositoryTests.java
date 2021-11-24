package mandykr.nutrient;

import mandykr.nutrient.dto.SupplementsDto;
import mandykr.nutrient.dto.SupplementsSearchCondition;
import mandykr.nutrient.entity.Supplements;
import mandykr.nutrient.repository.SupplementsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SupplementsRepositoryTests {
	@Autowired
	SupplementsRepository supplementsRepository;

	@Test
	@Commit
	void save() {
		Supplements supplements = new Supplements();
		supplements.setName("빌베리 플러스");
		SupplementsSearchCondition condition = new SupplementsSearchCondition(supplements.getName());

		Supplements saveSupplements = supplementsRepository.save(supplements);
		List<Supplements> supplementsListSDJpa = supplementsRepository.findByName(supplements.getName());
		List<SupplementsDto> supplementsListQdsl = supplementsRepository.search(condition);

		assertThat(supplementsListSDJpa.size()).isEqualTo(supplementsListQdsl.size()).isEqualTo(1);
	}

}
