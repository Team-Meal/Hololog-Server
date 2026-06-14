package team.nongchun.hororog.domain.diet.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.nongchun.hororog.domain.diet.dto.CreateDietLeftoverRequest
import team.nongchun.hororog.domain.diet.exception.DietNotFoundException
import team.nongchun.hororog.domain.diet.repository.DietRepository
import team.nongchun.hororog.domain.leftover.entity.Leftover
import team.nongchun.hororog.domain.leftover.repository.LeftoverRepository

@Service
@Transactional
class CreateDietLeftoverServiceImpl(
    private val dietRepository: DietRepository,
    private val leftoverRepository: LeftoverRepository,
) : CreateDietLeftoverService {
    override fun execute(
        dietId: Long,
        request: CreateDietLeftoverRequest,
    ) {
        val diet = dietRepository.findById(dietId).orElseThrow { DietNotFoundException() }
        leftoverRepository.save(
            Leftover(
                diet = diet,
                amount = request.amount,
                unit = request.unit,
                memo = request.memo,
            ),
        )
    }
}
