package com.jiazy.testmode.example;

@TestAnnotation(id=1, msg = "aaa")
public class Anno {
    @TestAnnotation(id=2, msg = "我是属性")
    private int id;
    private String name;
    private int age;

    public String desc(){
        return "java反射获取annotation的测试";
    }

    @TestAnnotation(id=3, msg = "我是方法")
    private int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

}
