package com.couree.luckycat.glacier.app.responder.annotation;

import com.couree.luckycat.glacier.app.pack.Packer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PackedBy {
    Class<? extends Packer> value();
}
