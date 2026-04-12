package com.student.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Name must match the spring.application.name of the student-service
@FeignClient(name = "STUDENT-SERVICE")
public interface StudentClient {

    @PostMapping("/students/add")
    void createStudentProfile(@RequestBody Object studentData);
}