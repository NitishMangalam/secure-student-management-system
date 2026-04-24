package com.student.student_service.service;

import com.student.student_service.dto.StudentDTO;
import com.student.student_service.entity.Student;
import com.student.student_service.repository.StudentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.observation.annotation.Observed;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public StudentService(StudentRepository studentRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.studentRepository = studentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * UPDATED: Now accepts StudentDTO to hide Database Entity from the API layer.
     */
    @Observed(name = "student.save")
    @CircuitBreaker(name = "studentService", fallbackMethod = "fallbackSaveStudent")
    public Student saveStudent(StudentDTO request) {
        // 1. Map DTO to Entity
        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setDepartment(request.getDepartment());

        // 2. Save to MySQL
        Student savedStudent = studentRepository.save(student);

        // 3. Send to Kafka (Event-Driven)
        // Format: "Created:Name" - this makes it easy for your Python AI service to parse
        String kafkaMessage = "Created:" + savedStudent.getName();
        kafkaTemplate.send("student-topic", kafkaMessage);

        System.out.println("Message successfully sent to Kafka: " + kafkaMessage);

        return savedStudent;
    }

    /**
     * UPDATED FALLBACK: Matches the new DTO parameter.
     * This triggers if Kafka is down, ensuring the DB save still happens.
     */
    public Student fallbackSaveStudent(StudentDTO request, Exception e) {
        System.err.println("--- CIRCUIT BREAKER ACTIVE ---");
        System.err.println("Reason for Fallback: " + e.getMessage());

        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setDepartment(request.getDepartment());

        Student savedStudent = studentRepository.save(student);
        System.out.println("Student saved to DB only. Kafka notification skipped (Fallback mode).");

        return savedStudent;
    }

    @Observed(name = "student.get.all")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }
}