package team.nongchun.hororog.domain.diet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.exception.DietNotFoundException
import team.nongchun.hororog.domain.diet.repository.DietRepository

@Service
@Transactional
class DeleteDietServiceImpl(
    private val dietRepository: DietRepository,
) : DeleteDietService {
    override fun execute(dietId: Long) {
        if (!dietRepository.existsById(dietId)) throw DietNotFoundException()
        dietRepository.deleteById(dietId)
    }
}
