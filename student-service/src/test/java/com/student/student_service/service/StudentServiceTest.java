package com.student.student_service.service;

import com.student.student_service.entity.Student;
import com.student.student_service.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate; // Add this import

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate; // ADD THIS LINE

    @InjectMocks
    private StudentService studentService;

    @Test
    void testSaveStudent_Success() {
        // Arrange
        Student student = new Student();
        student.setName("Nitish");
        student.setDepartment("Computer Science");

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        Student savedStudent = studentService.saveStudent(student);

        // Assert
        assertNotNull(savedStudent);
        assertEquals("Nitish", savedStudent.getName());

        // Verify both database and Kafka were interacted with
        verify(studentRepository, times(1)).save(any(Student.class));
    }
}