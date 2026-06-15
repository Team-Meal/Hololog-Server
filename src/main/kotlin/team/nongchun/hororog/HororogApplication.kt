package team.nongchun.hororog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class HororogApplication

fun main(args: Array<String>) {
    runApplication<HororogApplication>(*args)
}
