package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Reservation reservation = reservationRepository2.findById(reservationId).get();

        if(amountSent < (reservation.getNumberOfHours() * reservation.getSpot().getPricePerHour())){
            throw new Exception("Insufficient Amount");
        }

        Payment payment = new Payment();

        if(mode.toUpperCase().equals("CASH") || mode.toUpperCase().equals("CARD") || mode.toUpperCase().equals("UPI")){
            if(mode.toUpperCase().equals("CASH")) payment.setPaymentMode(PaymentMode.CASH);
            if(mode.toUpperCase().equals("CARD")) payment.setPaymentMode(PaymentMode.CARD);
            if(mode.toUpperCase().equals("UPI")) payment.setPaymentMode(PaymentMode.UPI);
            payment.setReserve(reservation);
            payment.setPaymentCompleted(true);

            reservation.setPayment(payment);
            reservation.getSpot().setOccupied(false);

           reservation = reservationRepository2.save(reservation);
        }
        else throw new Exception("Payment mode not detected");

        return payment;
    }
}
