package com.student.student_service.controller;

import com.student.student_service.dto.StudentDTO;
import com.student.student_service.entity.Student;
import com.student.student_service.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * UPDATED:
     * 1. Uses StudentDTO instead of Student entity for the request body.
     * 2. Extracts the 'X-Auth-User' header passed by the API Gateway.
     */
    @PostMapping("/add")
    public Student addStudent(
            @RequestBody StudentDTO studentDto,
            @RequestHeader(value = "X-Auth-User", required = false) String loggedInUser) {

        // Log the user identity for debugging/interview proof
        System.out.println("Student creation request initiated by user: " + loggedInUser);

        return studentService.saveStudent(studentDto);
    }

    @GetMapping("/all")
    public List<Student> getAll() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }
}