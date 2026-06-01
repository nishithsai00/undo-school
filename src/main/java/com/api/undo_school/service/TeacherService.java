package com.api.undo_school.service;

import com.api.undo_school.exceptions.*;
import com.api.undo_school.model.*;
import com.api.undo_school.repo.CourseRepo;
import com.api.undo_school.repo.OfferingRepo;
import com.api.undo_school.repo.SessionsRepo;
import com.api.undo_school.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherService {
    @Autowired
    UsersRepo userRepo;
    @Autowired
    CourseRepo courseRepo;
    @Autowired
    OfferingRepo offeringRepo;
    @Autowired
    SessionsRepo sessionRepo;

 public int register(Users user){
     return userRepo.save(user).getUserId();
 }


 public int addCourse(Course course,int id,String code){
     String dbCode=userRepo.findcode(id);
     if(dbCode==null){
         throw new InvalidUserException("user id invalid : "+id);
     }
     if(!(dbCode.equals(code))){
         throw new InvalidCodeException("invalid code : "+code);
     }
     return courseRepo.save(course).getId();
 }
 public int addOffering(int id,String code,RequestOfferingDto offeringDto){
     Users user=userRepo.findById(id).orElseThrow(()->new InvalidUserException("user not found with id : "+id));
     if(!(user.getPasscode().equals(code))){
         throw new InvalidCodeException("invalid code : "+code);
     }
     Course course =courseRepo.findById(offeringDto.getCourseId()).orElseThrow(()->new InvalidCourseIdException("invalid course id"+offeringDto.getCourseId()));
     Offering offering =new Offering();

     String timeZone=user.getTimeZone();
    LocalDateTime userStartDate=LocalDateTime.parse(offeringDto.getStartDate());
     LocalDateTime userEndDate=LocalDateTime.parse(offeringDto.getEndDate());
     Instant startDate=userStartDate.atZone(ZoneId.of(timeZone)).toInstant();
     Instant endDate =userEndDate.atZone(ZoneId.of(timeZone)).toInstant();
     offering.setStatus(Status.conformed);
     offering.setOfferingStartDate(startDate);
     offering.setOfferingEndDate(endDate);
      offering.setTeacherId(user);
      offering.setCourse(course);
      offering.setBatchType(offeringDto.getBatchType());
   return offeringRepo.save(offering).getOfferingId();

 }
 public int addSession(RequestSessionListDto sessionDto,int id, String code){
     Users user=userRepo.findById(id).orElseThrow(()->new InvalidUserException("user not found with id : "+id));
     if(!(user.getPasscode().equals(code))){
         throw new InvalidCodeException("invalid code : "+code);
     }
     Offering offering=offeringRepo.findById(sessionDto.getOffering()).orElseThrow(()->new InvalidOfferingIdException("invalid offering id :"+sessionDto.getOffering()));

     Sessions session =new Sessions();
;
    LocalDateTime teacherStartTime=LocalDateTime.parse(sessionDto.getStartTime());
    LocalDateTime teacherEndTime=LocalDateTime.parse(sessionDto.getEndTime());
    String timezone=user.getTimeZone();
     Instant startTime=teacherStartTime.atZone(ZoneId.of(timezone)).toInstant();
     Instant endTime=teacherEndTime.atZone(ZoneId.of(timezone)).toInstant();
     session.setStartTime(startTime);
     session.setEndTime(endTime);
     session.setTeacherId(user);
     session.setOffering(offering);



     List<Sessions> sessionsList =sessionRepo.findOverLappingSession(id,startTime,endTime);
         if(sessionsList.isEmpty()) {
             return sessionRepo.save(session).getSessionId();
         }
         else {
             throw  new SessionAlreadyExistsException("Session already exists with times :"+teacherStartTime +"-"+teacherEndTime);
         }

 }
// @Transactional
// public List<Integer> addListOfSessions( int id, String code,List<RequestSessionListDto> sessionsList){
//     Users user=userRepo.findById(id).orElseThrow(()->new InvalidUserException("user not found with id : "+id));
//     if(!(user.getPasscode().equals(code))){
//         throw new InvalidCodeException("invalid code : "+code);
//     }
//     List<Sessions> sessions=new ArrayList<>();
//     for(RequestSessionListDto s :sessionsList){
//
//         String teacherStartTime=s.getStartTime();
//         String teacherEndTime=s.getEndTime();
//         String timezone=user.getTimeZone();
//         Instant startTime=Instant.parse(teacherStartTime);
//         Instant endTime=Instant.parse(teacherEndTime);
//         s.setStartTime(startTime);
//         s.setEndTime(endTime);
//         List<Sessions> list =sessionRepo.findOverLappingSession(id,startTime,endTime);
//         if(!(list.isEmpty())) {
//             throw  new SessionAlreadyExistsException("Session already exists with times :"+teacherStartTime +"-"+teacherEndTime);
//         }
//
//     }
//
//     return sessionRepo.saveAll(sessionsList)
//             .stream()
//             .map(Sessions::getSessionId)
//             .toList();
// }


 public List<SessionResponseDto> getallSessions(int id,String code){
     Users dbUser=userRepo.findById(id).orElseThrow(()->new InvalidUserException("invalid user id : "+id));
     if(!(dbUser.getPasscode().equals(code))){
         throw new InvalidCodeException("invalid code reference : "+code);
     }
     String userTimezone=dbUser.getTimeZone();
     List<Sessions> sessionsList=sessionRepo.findByTeacherId_userIdOrderByStartTimeAsc(id);
     List<SessionResponseDto> dto=new ArrayList<>();
            for(Sessions s:sessionsList){
                Instant dbStartTime=s.getStartTime();
                Instant dbEndTime=s.getEndTime();
                ZonedDateTime startTime=dbStartTime.atZone(ZoneId.of(userTimezone));
                ZonedDateTime endTime=dbEndTime.atZone(ZoneId.of(userTimezone));
                SessionResponseDto session=new SessionResponseDto();
                    session.setSessionId(s.getSessionId());
                    session.setOffering(s.getOffering().getOfferingId());
                    session.setTeacherId(s.getTeacherId().getUserId());
                    session.setStartTime(startTime.toString());
                    session.setEndTime(endTime.toString());
                    dto.add(session);
     }
            return dto;

 }

    public List<SessionResponseDto> upComingSessions(int id, String code) {
        Users user=userRepo.findById(id).orElseThrow(()->new InvalidUserException("user not found with id : "+id));
        if(!(user.getPasscode().equals(code))){
            throw new InvalidCodeException("invalid code : "+code);
        }
        String userTimezone=user.getTimeZone();
        List<Sessions> sessionsList=sessionRepo.findUpcomingSessionsByTeacher(id,Instant.now());
        List<SessionResponseDto> dto=new ArrayList<>();
        for(Sessions s:sessionsList){
            Instant dbStartTime=s.getStartTime();
            Instant dbEndTime=s.getEndTime();
            ZonedDateTime startTime=dbStartTime.atZone(ZoneId.of(userTimezone));
            ZonedDateTime endTime=dbEndTime.atZone(ZoneId.of(userTimezone));
                SessionResponseDto session=new SessionResponseDto();
                session.setSessionId(s.getSessionId());
                session.setOffering(s.getOffering().getOfferingId());
                session.setTeacherId(s.getTeacherId().getUserId());
                session.setStartTime(startTime.toString());
                session.setEndTime(endTime.toString());
                dto.add(session);
            }


        return dto;

    }

    public void cancelSession(int id, String code, int offeringId){
        String dbCode=userRepo.findcode(id);
        if(dbCode==null){
            throw new InvalidUserException("user id invalid : "+id);
        }
        if(!(dbCode.equals(code))){
            throw new InvalidCodeException("invalid code : "+code);
        }
        Offering offering =offeringRepo.findById(offeringId).orElseThrow(()->new InvalidOfferingIdException("invalid offering id : "+offeringId));
        offering.setStatus(Status.cancelled);
        offeringRepo.save(offering);
    }
}
