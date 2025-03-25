package ro.unibuc.booking.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    
    List<Booking> findByArtistId(String artistId);
    
    List<Booking> findByStatusContract(ContractStatus statusContract);
    
    List<Booking> findByStatusPlata(PaymentStatus statusPlata);
}
