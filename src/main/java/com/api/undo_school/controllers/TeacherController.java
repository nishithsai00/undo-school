package com.api.undo_school.controllers;

import com.api.undo_school.exceptions.EmptyEmailException;
import com.api.undo_school.exceptions.EmptyUsernameException;
import com.api.undo_school.exceptions.InvalidTimezoneException;
import com.api.undo_school.model.*;
import com.api.undo_school.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;

@RestController
public class TeacherController {
    @Autowired
    TeacherService service;

    @PostMapping("/teacher/register")
    public ResponseEntity<String> register(@RequestBody Users user, HttpServletRequest request)  {
        user.setRole(Role.teacher);
        if(user.getName()==null){
            throw new EmptyUsernameException();
        }
        if(user.getEmail()==null){
            throw new EmptyEmailException();
        }
        String timeZone= user.getTimeZone()!=null? user.getTimeZone() : request.getHeader("X-Timezone");
        if(timeZone==null){
            timeZone="UTC";
        }
        try{
            ZoneId.of(timeZone);
        }catch (DateTimeException e){
            throw  new InvalidTimezoneException("invalid Timezone :" +timeZone);
        }
        int userid= service.register(user);
        Random random =new Random();
        int rand=1000+random.nextInt(999);
        String passcode=String.valueOf(rand);
        user.setPasscode(passcode);
        return new ResponseEntity<>("Teacher Profile created with userId : "+userid +"and unique code was : "+passcode,HttpStatus.CREATED);
    }
    @PostMapping("/{id}/{code}/addcourse")
    public ResponseEntity<String> addCourse(@RequestBody Course course, @PathVariable int id,@PathVariable String code){
        int num=service.addCourse(course,id,code);
        return new ResponseEntity<>("course added done with reference id : "+num,HttpStatus.CREATED);

    }
    @PostMapping("/{id}/{code}/addoffering")
    public ResponseEntity<String> addOffering(@PathVariable int id, @PathVariable String code, @RequestBody Offering offering,@RequestBody TeacherSession dto){
        int offeringId=service.addOffering(id,code,offering,dto);
        return new ResponseEntity<>("Offering added done with ref id :"+offeringId, HttpStatus.CREATED);
    }
    @PostMapping("/{id}/{code}/session")
    public ResponseEntity<String> addSession(@PathVariable int id, @PathVariable String code, @RequestBody Sessions sessions,@RequestBody TeacherSession dto){
        int sessionId= service.addSession(sessions,dto,id,code);
        return new ResponseEntity<>("session added done with ref number : "+sessionId,HttpStatus.CREATED);
    }
    @PostMapping("/{id}/{code}/listsessions")
    public ResponseEntity<String> addListOfSessions(@PathVariable int id,@PathVariable String code ,@RequestBody List<Sessions> sessions,@RequestBody TeacherSession dto){

        List<Integer> listOfSessionIdS= service.addListOfSessions(id,code,sessions,dto);
        return new ResponseEntity<>("list of sessions added done with id's : "+listOfSessionIdS,HttpStatus.CREATED);
    }
    @GetMapping("/{id}/{code}/session")
    public ResponseEntity<List<SessionResponseDto>> listOfSessions(@PathVariable int id,@PathVariable String code){
        return new ResponseEntity<>(service.getallSessions(id,code),HttpStatus.OK);

    }
    @GetMapping("/{id}/{code}/upsessions")
    public ResponseEntity<List<SessionResponseDto>> getUpComing(@PathVariable int id,@PathVariable String code){
        return new ResponseEntity<>(service.upComingSessions(id,code),HttpStatus.OK);
    }
    @PutMapping("/{id}/{code}/session")
    public ResponseEntity<String> deleteSession(@PathVariable int id,@PathVariable String code,@RequestBody int offeringId){
        service.cancelSession(id,code,offeringId);
        return new ResponseEntity<>("offering cancellation done",HttpStatus.OK);
    }

}
