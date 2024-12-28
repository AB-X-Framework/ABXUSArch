package org.abx.ws.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Luis
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface  WSMethod {
    public String[] params();
}
