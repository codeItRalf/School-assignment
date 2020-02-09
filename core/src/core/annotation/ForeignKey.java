package core.annotation;



import core.app.entity.NoClass;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public @interface ForeignKey {
    Class parent() default NoClass.class;
    String parentId() default "";
    Class child() default NoClass.class;
    String listOfChildren() default "";
    @Action int onUpdate() default NO_ACTION;

    @Action int onDelete() default NO_ACTION;


    int NO_ACTION = 0;
    int CASCADE = 1;

    @IntDef({NO_ACTION, CASCADE})
    @Retention(SOURCE)
    @interface Action {
    }

}
