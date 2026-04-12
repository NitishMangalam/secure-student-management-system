package com.student.student_service.service;

import com.student.student_service.entity.Student;
import com.student.student_service.repository.StudentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker; // Circuit Breaker
import io.micrometer.observation.annotation.Observed; // Tracing
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
     * @Observed: Creates a "Span" in Zipkin so you can see the time taken for this save operation.
     * @CircuitBreaker: Monitors this method. If Kafka fails 50% of the time, it stops trying
     * and calls the 'fallbackSaveStudent' method instead.
     */
    @Observed(name = "student.save")
    @CircuitBreaker(name = "studentService", fallbackMethod = "fallbackSaveStudent")
    public Student saveStudent(Student student) {
        // 1. Save to MySQL
        Student savedStudent = studentRepository.save(student);

        // 2. Send to Kafka (Event-Driven)
        // This is where the Circuit Breaker is watching!
        kafkaTemplate.send("student-topic", "New Student Profile Created: " + savedStudent.getName());
        System.out.println("Message successfully sent to Kafka");

        return savedStudent;
    }

    /**
     * FALLBACK METHOD: This runs ONLY if Kafka is down or the Circuit is OPEN.
     * It ensures the user still gets a success message even if the notification fails.
     */
    public Student fallbackSaveStudent(Student student, Exception e) {
        System.err.println("--- CIRCUIT BREAKER ACTIVE ---");
        System.err.println("Reason: " + e.getMessage());

        // We still save to DB so no data is lost!
        Student savedStudent = studentRepository.save(student);
        System.out.println("Student saved to DB, but Kafka notification was skipped (Fallback).");

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