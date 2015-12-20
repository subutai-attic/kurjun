package ai.subut.kurjun.quota;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.inject.BindingAnnotation;


/**
 * Annotation type to mark quota specific injection bindings.
 *
 */
@BindingAnnotation
@Retention( RetentionPolicy.RUNTIME )
public @interface Quota
{

}

