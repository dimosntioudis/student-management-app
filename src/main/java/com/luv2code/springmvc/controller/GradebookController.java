package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.Gradebook;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GradebookController {

  @Autowired
  private Gradebook gradebook;

  @Autowired
  private StudentAndGradeService studentService;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String getStudents(Model m) {
    Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
    m.addAttribute("students", collegeStudents);
    return "index";
  }

  @PostMapping(value = "/")
  public String createStudent(@ModelAttribute("student") CollegeStudent student, Model m) {
    studentService.createStudent(student.getFirstname(), student.getLastname(),
        student.getEmailAddress());

    Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
    m.addAttribute("students", collegeStudents);

    return "index";
  }

  @GetMapping("/delete/student/{id}")
  public String deleteStudent(@PathVariable int id, Model m) {

    if (!studentService.checkIfStudentIsNull(id)) {
      return "error";
    }
    studentService.deleteStudent(id);
    Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
    m.addAttribute("students", collegeStudents);
    return "index";
  }

  @GetMapping("/studentInformation/{id}")
  public String studentInformation(@PathVariable int id, Model m) {
    if (!studentService.checkIfStudentIsNull(id)) {
      return "error";
    }

    studentService.configureStudentInformationModel(id, m);

    return "studentInformation";
  }

  @PostMapping(value = "/grades")
  public String createGrade(@RequestParam("grade") double grade,
      @RequestParam("gradeType") String gradeType,
      @RequestParam("studentId") int studentId,
      Model m) {

    if (!studentService.checkIfStudentIsNull(studentId)) {
      return "error";
    }

    boolean success = studentService.createGrade(grade, studentId, gradeType);

    if (!success) {
      return "error";
    }

    studentService.configureStudentInformationModel(studentId, m);

    return "studentInformation";
  }

  @GetMapping("/grades/{id}/{gradeType}")
  public String deleteGrade(@PathVariable int id, @PathVariable String gradeType, Model m) {
    int studentId = studentService.deleteGrade(id, gradeType);

    if (studentId == 0) {
      return "error";
    }

    studentService.configureStudentInformationModel(studentId, m);

    return "studentInformation";
  }
}
