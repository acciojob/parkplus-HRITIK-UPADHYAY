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
            int totalPrice = timeInHours * spot.getPricePerHour();
            if(minimumPrice > totalPrice){
                if(numberOfWheels == 2) {
                    minimumReservationSpot = spot;
                    minimumPrice = totalPrice;
                }
                else if(numberOfWheels == 4 && spot.getSpotType() == SpotType.FOUR_WHEELER){
                    minimumReservationSpot = spot;
                    minimumPrice = totalPrice;
                }
                else if(numberOfWheels > 4 && spot.getSpotType() == SpotType.OTHERS){
                    minimumReservationSpot = spot;
                    minimumPrice = totalPrice;
                }
            }
        }

        if(minimumReservationSpot == null) throw new Exception("Cannot make reservation");

        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(numberOfWheels);
        reservation.setUser(user);
        reservation.setSpot(minimumReservationSpot);

        minimumReservationSpot.setOccupied(true);
        minimumReservationSpot.getReservationList().add(reservation);

        parkingLotRepository3.save(parkingLot);

        return reservation;
    }
}
