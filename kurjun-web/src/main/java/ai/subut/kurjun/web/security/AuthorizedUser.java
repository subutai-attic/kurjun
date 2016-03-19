package ai.subut.kurjun.web.security;

import ninja.params.WithArgumentExtractor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@WithArgumentExtractor( AuthorizedUserExtractor.class )
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER } )
public @interface AuthorizedUser
{
}