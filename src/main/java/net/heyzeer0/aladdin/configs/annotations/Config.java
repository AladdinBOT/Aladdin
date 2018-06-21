package net.heyzeer0.aladdin.configs.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by HeyZeer0 on 08/06/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

    String name();

}
