package ai.subut.kurjun.model.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate the parameter can be null.
 * See https://github.com/google/guice/wiki/UseNullable
 * 
 */

@Retention( RetentionPolicy.RUNTIME )
@Target( value =
{
    ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE
} )
public @interface Nullable
{

}
