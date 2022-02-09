package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        //when
        underTest.getAllStudents();
        //then
        verify(studentRepository).findAll();

    }

    @Test
    void canAddStudent() {
        //given
        String email = "sergio@gmail.com";
        Student student = new Student(
                "Sergio",
                email,
                Gender.MALE
        );
        //when
        underTest.addStudent(student);

        //then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();

        assertThat(capturedStudent).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        //given
        String email = "sergio@gmail.com";
        Student student = new Student(
                "Sergio",
                email,
                Gender.MALE
        );

//        given(studentRepository.selectExistsEmail(student.getEmail())).willReturn(true);
        given(studentRepository.selectExistsEmail(anyString())).willReturn(true);

        //when
        //then
        assertThatThrownBy(()-> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());


    }

    @Test
    void canDeleteStudent() {
        //given
        Long id = 1L;
        Student student = new Student(
                id,
                "Sergio",
                "sergio@gmail.com",
                Gender.MALE
        );
        //when
        given(studentRepository.existsById(anyLong()));
        underTest.deleteStudent(id);
        //then
        verify(studentRepository).deleteById(anyLong());

    }
}