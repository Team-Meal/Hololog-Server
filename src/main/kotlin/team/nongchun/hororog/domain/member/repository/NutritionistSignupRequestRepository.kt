package team.nongchun.hororog.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.nongchun.hororog.domain.member.entity.NutritionistSignupRequest

interface NutritionistSignupRequestRepository : JpaRepository<NutritionistSignupRequest, Long>
