package com.victorlamp.matrixiot.service.common.validation.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IdHex24Validator.class})
public @interface IdHex24 {

    String message() default "Id为24位十六进制数";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
