package team.nongchun.hororog.global.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
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
        private val PUBLIC_ENDPOINTS = arrayOf("/auth/**", "/signin", "/reissue")
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
                it.anyRequest().authenticated()
            }.addFilterBefore(
                JwtAuthenticationFilter(jwtProvider, customUserDetailsService),
                UsernamePasswordAuthenticationFilter::class.java,
            )
        return http.build()
    }
}
