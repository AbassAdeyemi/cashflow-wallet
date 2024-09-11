package com.hayba.walletapp.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig: WebMvcConfigurer {

    override fun addViewControllers(registry: ViewControllerRegistry) {
        super.addViewControllers(registry)
        registry.addViewController("/").setViewName("forward:/import.html")
        registry.addViewController("/import").setViewName("forward:/import.html")
        registry.addViewController("/dashboard").setViewName("forward:/dashboard.html")
        registry.addViewController("/offering").setViewName("forward:/offering.html")
        registry.addViewController("/payment").setViewName("forward:/payment.html")
        registry.addViewController("/quote").setViewName("forward:/quote.html")
    }
}