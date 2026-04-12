package com.student.student_service.controller;

import com.student.student_service.entity.Student;
import com.student.student_service.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService) {
        this.studentService=studentService;
    }
    @PostMapping("/add")
    public Student addStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
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
