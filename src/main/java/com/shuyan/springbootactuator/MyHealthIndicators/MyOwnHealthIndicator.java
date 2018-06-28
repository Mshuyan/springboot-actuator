package com.shuyan.springbootactuator.MyHealthIndicators;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
// HealthIndicator 的标识符为本类名去掉 HealthIndicator 后缀的首字母小写形式
public class MyOwnHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        int errorCode = check();
        if (errorCode != 0) {
            //withDetail 方法中可以添加要显示的健康状态明细
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    private int check(){
        //自定义的健康检查逻辑
        return getNum(0, 1);
    }

    private static int getNum(int start,int end) {
        return (int)(Math.random()*(end-start+1)+start);
    }
}
