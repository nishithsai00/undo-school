package com.api.undo_school.service;

import com.api.undo_school.exceptions.BookingException;
import com.api.undo_school.exceptions.InvalidOfferingIdException;
import com.api.undo_school.exceptions.InvalidUserException;
import com.api.undo_school.model.*;
import com.api.undo_school.repo.BookingRepo;
import com.api.undo_school.repo.OfferingRepo;
import com.api.undo_school.repo.SessionsRepo;
import com.api.undo_school.repo.UsersRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParentService {
    @Autowired
    UsersRepo userRepo;
    @Autowired
    OfferingRepo offeringRepo;
    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    SessionsRepo sessionsRepo;

    public int register(Users user){
        return userRepo.save(user).getUserId();

    }
    public List<OfferingResponseDto> getOfferings(int id){
       Users userEntity=userRepo.findById(id).orElseThrow(()->new InvalidUserException("invalid user id : "+id));
       String timeZone=userEntity.getTimeZone();
        Instant currentTime=Instant.now();
        List<Offering> offeringList= offeringRepo.findByCurrentDate(currentTime);
        List<OfferingResponseDto> dto =new ArrayList<>();
        for(Offering f:offeringList){
            Instant dbStartTime=f.getOfferingStartDate();
            Instant dbEndTime=f.getOfferingEndDate();
            ZonedDateTime startTime=dbStartTime.atZone(ZoneId.of(timeZone));
            ZonedDateTime endTime=dbEndTime.atZone(ZoneId.of(timeZone));
            if(f.getStatus().equals(Status.conformed)){
                OfferingResponseDto off=new OfferingResponseDto();
                off.setOfferingId(f.getOfferingId());
                off.setBatchType(f.getBatchType());
                off.setCourseId(f.getCourse().getId());
                off.setStartDate(startTime.toString());
                off.setEndDate(endTime.toString());
                dto.add(off);
            }

        }
            return dto;


    }
    @Transactional
public int bookAoffering(int id ,int offeringId){
        Users user =userRepo.findByIdWithLock(id).orElseThrow(()->new InvalidUserException("user Id is invalid  : "+id));

        Offering offering=offeringRepo.findById(offeringId).orElseThrow(()->new InvalidOfferingIdException("invalid offering id"+ offeringId));
        List<Sessions> wantToBookSessions=sessionsRepo.findByOffering_offeringId(offering.getOfferingId());
        for (Sessions s : wantToBookSessions) {
            List<Sessions> conflicts = sessionsRepo.findConflictingSessionsForParent(
                    user.getUserId(),
                    s.getStartTime(),
                    s.getEndTime()
            );
            if (!conflicts.isEmpty()) {
                throw new BookingException(
                        "Session on " + s.getStartTime() + " already exists in your previous booking"
                );
            }
        }


    Bookings booking=new Bookings();
    booking.setParentId(user);
    booking.setOffering(offering);
    booking.setStatus(Status.conformed);

        return bookingRepo.save(booking).getBookingId();
}

public List<Bookings> getAllBookings(int id){
        return bookingRepo.findByParentId_userId(id);
}

    public void cancleBooking(int id, int bookingId) {
        boolean useridAvail=userRepo.existsById(id);
        if(!useridAvail){
            throw new InvalidUserException("user id not fount with the id : "+id);
        }
       Bookings booking= bookingRepo.findById(bookingId).orElseThrow(()->new BookingException("booking not found with id :" +bookingId));
        booking.setStatus(Status.cancelled);
        bookingRepo.save(booking);
    }
}
