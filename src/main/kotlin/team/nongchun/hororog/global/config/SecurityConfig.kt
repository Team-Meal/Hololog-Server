package team.nongchun.hororog.global.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import team.nongchun.hororog.domain.member.entity.Role
import team.nongchun.hororog.global.auth.CustomUserDetailsService
import team.nongchun.hororog.global.auth.JwtAuthenticationFilter
import team.nongchun.hororog.global.auth.JwtProperties
import team.nongchun.hororog.global.auth.JwtProvider

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfig(
    private val jwtProvider: JwtProvider,
    private val customUserDetailsService: CustomUserDetailsService,
) {
    companion object {
        // 와일드카드 대신 정확한 경로만 나열한다 — /auth 하위에 보호가 필요한 엔드포인트가 추가돼도 노출되지 않도록.
        private val PUBLIC_ENDPOINTS = arrayOf("/auth/signup", "/auth/signin", "/auth/reissue")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager = configuration.authenticationManager

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(*PUBLIC_ENDPOINTS).permitAll()
                it.requestMatchers("/auth/logout").authenticated()
                it.requestMatchers("/signup-requests/*/approve", "/signup-requests/*/reject").hasRole(Role.ADMIN.name)
                it.requestMatchers(HttpMethod.POST, "/signup-requests").hasRole(Role.PENDING_NUTRITIONIST.name)
                // 승인 전(PENDING_NUTRITIONIST) 회원은 그 외 API에 접근할 수 없다.
                it.anyRequest().hasAnyRole(Role.NUTRITIONIST.name, Role.ADMIN.name)
            }.addFilterBefore(
                JwtAuthenticationFilter(jwtProvider, customUserDetailsService),
                UsernamePasswordAuthenticationFilter::class.java,
            )
        return http.build()
    }
}
