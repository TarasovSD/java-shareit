package ru.practicum.shareit.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StartBeforeEndProcessor.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StartBeforeEnd {
    String message() default "StartBeforeEnd annotation";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
