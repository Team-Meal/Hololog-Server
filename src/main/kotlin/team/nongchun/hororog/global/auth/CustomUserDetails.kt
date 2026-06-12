package team.nongchun.hororog.global.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import team.nongchun.hororog.domain.member.entity.Member
import team.nongchun.hororog.domain.member.entity.Role

class CustomUserDetails(
    val userId: Long,
    private val email: String,
    private val password: String,
    val role: Role,
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_${role.name}"))

    override fun getPassword(): String = password

    override fun getUsername(): String = email

    companion object {
        fun from(member: Member): CustomUserDetails =
            CustomUserDetails(
                userId = member.id,
                email = member.email,
                password = member.password,
                role = member.role,
            )
    }
}
