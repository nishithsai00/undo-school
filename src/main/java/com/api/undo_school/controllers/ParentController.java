package com.api.undo_school.controllers;

import com.api.undo_school.exceptions.EmptyEmailException;
import com.api.undo_school.exceptions.EmptyUsernameException;
import com.api.undo_school.exceptions.InvalidTimezoneException;
import com.api.undo_school.model.*;
import com.api.undo_school.service.ParentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;

@RestController
public class ParentController {
    @Autowired
    ParentService service;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody  Users user, HttpServletRequest request)  {
        user.setRole(Role.parent);
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
       return new ResponseEntity<> ("Parent Profile created with userId : "+userid,HttpStatus.CREATED);
    }
    @GetMapping("/{id}/offerings")
    public ResponseEntity<List<OfferingResponseDto>> offeringList(@PathVariable int id){
        return new ResponseEntity<>(service.getOfferings(id),HttpStatus.OK);
    }
 @PostMapping("/{id}/booking")
    public ResponseEntity<Integer> bookAoffering(@PathVariable int id , @RequestBody int offeringId){
        return new ResponseEntity<>(service.bookAoffering(id,offeringId), HttpStatus.CREATED);
 }
 @GetMapping("/{id}/booking")
    public ResponseEntity<List<Bookings>> getBookings(@PathVariable int id){
       return new ResponseEntity<>(service.getAllBookings(id),HttpStatus.OK);
 }
 @PutMapping("/{id}/booking")
    public ResponseEntity<String> cancleBooking(@PathVariable int id ,@RequestBody int bookingId){
        service.cancleBooking(id,bookingId);
        return new ResponseEntity<>("Booking Cancelled done",HttpStatus.OK);

 }
}
