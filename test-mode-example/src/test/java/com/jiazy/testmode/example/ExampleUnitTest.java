package com.jiazy.testmode.example;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_annotations() throws Exception {
        boolean isAnnotation = Anno.class.isAnnotationPresent(TestAnnotation.class);
        if(isAnnotation) {
            TestAnnotation testAnnotation = Anno.class.getAnnotation(TestAnnotation.class);
            System.out.println(testAnnotation.id());
            System.out.println(testAnnotation.msg());
        }
    }

    @Test
    public void test_annotations2() throws Exception {
        Field field = Anno.class.getDeclaredField("id");
        if (field != null) {
            field.setAccessible(true);
            TestAnnotation testAnnotation = field.getAnnotation(TestAnnotation.class);
            if (testAnnotation != null) {
                System.out.println("fieldMeta msg:"+testAnnotation.msg());
            }
        }
    }

    @Test
    public void test_annotations3() throws Exception {
        Method method = Anno.class.getDeclaredMethod("getId");
        if (method != null) {
            TestAnnotation testAnnotation = method.getAnnotation(TestAnnotation.class);
//            TestAnnotation testAnnotation = method.getDeclaredAnnotation(TestAnnotation.class);
            if (testAnnotation != null) {
                System.out.println("fieldMeta msg:"+testAnnotation.msg());
            }
        }
    }

}