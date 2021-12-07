package mandykr.nutrient;

import mandykr.nutrient.dto.SupplementDto;
import mandykr.nutrient.dto.SupplementSearchCondition;
import mandykr.nutrient.entity.Supplement;
import mandykr.nutrient.repository.SupplementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SupplementRepositoryTests {
	@Autowired
	SupplementRepository supplementRepository;

	@Test
	@Commit
	void save() {
		Supplement supplement = new Supplement();
		supplement.setName("빌베리 플러스");
		SupplementSearchCondition condition = new SupplementSearchCondition(supplement.getName());

		Supplement saveSupplement = supplementRepository.save(supplement);
		List<Supplement> supplementListSDJpa = supplementRepository.findByName(supplement.getName());
		List<SupplementDto> supplementsListQdsl = supplementRepository.search(condition);

		assertThat(supplementListSDJpa.size()).isEqualTo(supplementsListQdsl.size()).isEqualTo(1);
	}

}
