package net.heyzeer0.aladdin.interfaces.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by HeyZeer0 on 19/09/2017.
 * Copyright © HeyZeer0 - 2016
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface YamlConfig {

    String name();
    String folder() default "none";

}