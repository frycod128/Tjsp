package cn.yznu.abc4321.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";           // 数据库列名
    boolean id() default false;         // 是否主键
    boolean autoIncrement() default false; // 是否自增
    boolean ignore() default false;     // 是否忽略该字段
}