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

    @Pointcut("execution(* ru.filkin.aopproject.restaop.kafka.*.*(..))")
    public void kafkaMethods(){
    }

    @Pointcut("serviceMethods() || kafkaMethods()")
    public void allMethods() {
    }

    @Before("allMethods()")
    public void logBeforeMethodCall(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("Service: Вызов метода {} с аргументами {}", methodName, args);
    }

    @AfterReturning(value = "allMethods()", returning = "result")
    public void logAfterMethodCall(JoinPoint joinPoint, Object result){
        String methodName = joinPoint.getSignature().getName();
        log.info("Service: Метод {} выполнен, результат {}", methodName, result);
    }

    @AfterThrowing(value = "allMethods()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Exception exception){
        String methodName = joinPoint.getSignature().getName();
        log.error("Service: Метод {} выбросил исключение {}", methodName, exception.getMessage());
    }

    @Around("allMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Long startTime = System.currentTimeMillis();
        Object result;
        try{
            result = joinPoint.proceed();
        } catch(Exception e){
            Long resultTime = System.currentTimeMillis() - startTime;
            log.info("Service: Метод {} выполнен за {} мс с исключением", methodName, resultTime);
            throw e;
        }
        Long resultTime = System.currentTimeMillis() - startTime;
        log.info("Service: Метод {} выполнен за {} мс", methodName, resultTime);
        return result;
    }

}
