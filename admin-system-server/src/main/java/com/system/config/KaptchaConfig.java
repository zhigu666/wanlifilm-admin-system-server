package com.system.config;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {
    @Bean
    public DefaultKaptcha producer(){
        Properties p=new Properties();
        p.setProperty(Constants.KAPTCHA_BORDER,"no");
        p.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR,"blue");
        p.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE,"4");
        p.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT,"40");
        p.setProperty(Constants.KAPTCHA_IMAGE_WIDTH,"120");
        p.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE,"30");

        Config config = new Config(p);
        DefaultKaptcha defaultKaptcha=new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
