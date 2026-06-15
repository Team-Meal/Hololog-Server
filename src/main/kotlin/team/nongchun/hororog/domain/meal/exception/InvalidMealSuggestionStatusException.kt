package team.nongchun.hororog.domain.meal.exception

class InvalidMealSuggestionStatusException : RuntimeException("급식 추천 생성 시 처리 상태는 PENDING만 가능합니다.")
