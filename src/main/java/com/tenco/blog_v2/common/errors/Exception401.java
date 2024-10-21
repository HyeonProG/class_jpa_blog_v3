package com.tenco.blog_v2.common.errors;

public class Exception401 extends RuntimeException {

    // throw new Exception401("너 잘못 던졌어"); <-- 사용하는 시점에 호출 모습
    public Exception401(String msg) {
        super(msg);
    }

}
