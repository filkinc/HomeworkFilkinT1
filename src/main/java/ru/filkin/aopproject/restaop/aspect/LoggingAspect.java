package ru.filkin.aopproject.restaop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAspect {
    @Pointcut("execution(* ru.filkin.aopproject.restaop.service.*.*(..))")
    public void serviceMethods(){
    }

    @Before("serviceMethods()")
    public void logBeforeMethodCall(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("Service: Вызов метода {} с аргументами {}", methodName, args);
    }

    @AfterReturning(value = "serviceMethods()", returning = "result")
    public void logAfterMethodCall(JoinPoint joinPoint, Object result){
        String methodName = joinPoint.getSignature().getName();
        log.info("Service: Метод {} выполнен, результат {}", methodName, result);
    }

    @AfterThrowing(value = "serviceMethods()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Exception exception){
        String methodName = joinPoint.getSignature().getName();
        log.info("Service: Метод {}, выбросил исключение {}", methodName, exception.getMessage());
    }

    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        Long resultTime = System.currentTimeMillis() - startTime;
        log.info("Service: Метод {} выполнен за {}", methodName, resultTime);
        return result;
    }

}
