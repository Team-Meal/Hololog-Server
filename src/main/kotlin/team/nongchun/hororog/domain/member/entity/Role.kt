package team.nongchun.hororog.domain.member.entity

enum class Role {
    ADMIN,

    /** 가입 직후 상태. 면허번호 제출(가입 요청)과 로그아웃만 가능하며, 관리자 승인 후 NUTRITIONIST로 전환된다. */
    PENDING_NUTRITIONIST,
    NUTRITIONIST,
}
