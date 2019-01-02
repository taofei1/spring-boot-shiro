package proxy.annon;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface Seven {
	
	 String value() default "Ð¡ºÚ";
	
	 String Property() default "ÎÞÊôÐÔ";
	
}
