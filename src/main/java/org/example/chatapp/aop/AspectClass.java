package org.example.chatapp.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.chatapp.model.User;
import org.example.chatapp.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;

@Aspect
@Component
public class AspectClass {

    private final UserRepository userRepository;

    public AspectClass(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Before("@annotation(UpdateActivity)")
    public void updateLastActivity(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Principal) {
                User user = userRepository.findUserByUsername(((Principal) arg).getName()).orElseThrow(IllegalArgumentException::new);
                user.setLast_activity(LocalDateTime.now());
                userRepository.save(user);
            }
        }

    }
}
