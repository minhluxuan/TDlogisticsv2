package project.tdlogistics.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import project.tdlogistics.users.entities.Otp;
import java.util.Optional;


public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findOneByPhoneNumberAndEmailAndOtp(String phoneNumber, String email, String otp);
}
