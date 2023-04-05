package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user;
        try{
            user = userRepository3.findById(userId).get();
        }
        catch (Exception e){
            throw new Exception("Cannot make reservation");
        }
        ParkingLot parkingLot;

        try {
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        }
        catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        List<Spot> spots = parkingLot.getSpotList();
        Spot minimumReservationSpot = null;
        int minimumPrice = Integer.MAX_VALUE;
        for(Spot spot : spots){
            //int totalPrice = timeInHours * spot.getPricePerHour();
            if(spot.getOccupied().equals(false)){
                if(spot.getSpotType().equals(SpotType.TWO_WHEELER)){
                    if(numberOfWheels <= 2){
                        if(minimumPrice > spot.getPricePerHour()){
                            minimumPrice = spot.getPricePerHour();
                            minimumReservationSpot = spot;
                        }
                    }
                }
                else if(spot.getSpotType().equals(SpotType.TWO_WHEELER)){
                    if(numberOfWheels <= 4){
                        if(minimumPrice > spot.getPricePerHour()){
                            minimumPrice = spot.getPricePerHour();
                            minimumReservationSpot = spot;
                        }
                    }
                }
                else {
                    if(minimumPrice > spot.getPricePerHour()){
                        minimumPrice = spot.getPricePerHour();
                        minimumReservationSpot = spot;
                    }
                }
            }
        }

        if(minimumReservationSpot == null) throw new Exception("Cannot make reservation");

        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setUser(user);
        reservation.setSpot(minimumReservationSpot);

        user.getReservationList().add(reservation);
        minimumReservationSpot.getReservationList().add(reservation);

        minimumReservationSpot.setOccupied(Boolean.TRUE);
        minimumReservationSpot.getReservationList().add(reservation);

       userRepository3.save(user);
       spotRepository3.save(minimumReservationSpot);

        return reservation;
    }
}
