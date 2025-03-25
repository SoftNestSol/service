package ro.unibuc.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.booking.data.Booking;
import ro.unibuc.booking.data.BookingRepository;
import ro.unibuc.booking.data.ContractStatus;
import ro.unibuc.booking.data.PaymentStatus;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @PostMapping
    @ResponseBody
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingRepository.save(booking);
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public Booking getBookingById(@PathVariable String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }
    
    @GetMapping
    @ResponseBody
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public Booking updateBooking(@PathVariable String id, @RequestBody Booking booking) {
        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found with id: " + id);
        }
        booking.setEventId(id);
        return bookingRepository.save(booking);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteBooking(@PathVariable String id) {
        bookingRepository.deleteById(id);
    }
    
    @GetMapping("/artist/{artistId}")
    @ResponseBody
    public List<Booking> getBookingsByArtistId(@PathVariable String artistId) {
        return bookingRepository.findByArtistId(artistId);
    }
    
    @GetMapping("/contract-status/{status}")
    @ResponseBody
    public List<Booking> getBookingsByContractStatus(@PathVariable ContractStatus status) {
        return bookingRepository.findByStatusContract(status);
    }
    
    @GetMapping("/payment-status/{status}")
    @ResponseBody
    public List<Booking> getBookingsByPaymentStatus(@PathVariable PaymentStatus status) {
        return bookingRepository.findByStatusPlata(status);
    }
}
