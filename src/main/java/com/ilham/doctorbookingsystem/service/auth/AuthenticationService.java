package com.ilham.doctorbookingsystem.service.auth;

import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import com.ilham.doctorbookingsystem.entity.PatientEntity;
import com.ilham.doctorbookingsystem.entity.RefreshTokenEntity;
import com.ilham.doctorbookingsystem.entity.UserEntity;
import com.ilham.doctorbookingsystem.enums.ApprovalStatus;
import com.ilham.doctorbookingsystem.enums.RoleEnum;
import com.ilham.doctorbookingsystem.model.request.LoginRequest;
import com.ilham.doctorbookingsystem.model.request.RegisterDoctorRequest;
import com.ilham.doctorbookingsystem.model.request.RegisterPatientRequest;
import com.ilham.doctorbookingsystem.model.request.VerifyOtpRequestDto;
import com.ilham.doctorbookingsystem.model.response.AuthResponse;
import com.ilham.doctorbookingsystem.repository.DoctorRepository;
import com.ilham.doctorbookingsystem.repository.PatientRepository;
import com.ilham.doctorbookingsystem.repository.RefreshTokenRepository;
import com.ilham.doctorbookingsystem.repository.UserRepository;
import com.ilham.doctorbookingsystem.service.mail.EmailService;
import com.ilham.doctorbookingsystem.service.mail.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final OtpService otpService;

    private final EmailService emailService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenBlacklistService tokenBlacklistService;


    @Transactional
    public String registerPatient(RegisterPatientRequest request){
        log.info("ActionLog.registerPatient.start");
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException();
        }

        UserEntity userEntity = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(RoleEnum.PATIENT)
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(userEntity);

        PatientEntity patientEntity = PatientEntity.builder()
                .user(userEntity)
                .dateOfBirth(request.getDateOfBirth())
                .bloodGroup(request.getBloodGroup())
                .gender(request.getGender())
                .createdAt(LocalDateTime.now())
                .build();

        patientRepository.save(patientEntity);

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpService.saveOTP(request.getEmail(), otp);

        emailService.sendOtpEmail(request.getEmail(), otp);
        log.info("ActionLog.registerPatient.end");
        return "OTP sent to email";
    }

    @Transactional
    public String registerDoctor(RegisterDoctorRequest request){

        log.info("ActionLog.registerDoctor.start");
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException();
        }

        UserEntity userEntity = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(RoleEnum.DOCTOR)
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(userEntity);

        DoctorEntity doctorEntity = DoctorEntity.builder()
                .user(userEntity)
                .consultationFee(request.getConsultationFee())
                .experienceYears(request.getExperienceYears())
                .licenseNumber(request.getLicenseNumber())
                .specialization(request.getSpecialization())
                .status(ApprovalStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        doctorRepository.save(doctorEntity);

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpService.saveOTP(request.getEmail(), otp);

        emailService.sendOtpEmail(request.getEmail(), otp);
        log.info("ActionLog.registerDoctor.end");
        return "OTP sent to email";
    }


    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequestDto request) {

        log.info("ActionLog.verifyOtp.start");

        String savedOtp = otpService.getOTP(request.getEmail());

        if (savedOtp == null || savedOtp.isBlank()) {
            throw new RuntimeException("OTP expired or not found");
        }

        if (!savedOtp.equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userEntity.setEnabled(true);
        userRepository.save(userEntity);

        String accessToken = jwtService.generateToken(userEntity);

        RefreshTokenEntity refreshToken =
                createRefreshToken(userEntity);

        otpService.deleteOTP(request.getEmail());

        log.info("ActionLog.verifyOtp.end");

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .message("Email verified successfully")
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("ActionLog.login.start");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserEntity user = userRepository.findByEmail(request.getEmail()).
                orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("User not verified. Please verify OTP first.");
        }

        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtService.generateToken(user);

        RefreshTokenEntity refreshToken = createRefreshToken(user);

        String roleName =
                user.getRole().name().toLowerCase();

        String message =
                roleName.substring(0,1).toUpperCase()
                        + roleName.substring(1)
                        + " logged in successfully";

        log.info("ActionLog.login.end");

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .message(message)
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String requestToken) {

        log.info("ActionLog.refreshToken.start");

        RefreshTokenEntity refreshToken = refreshTokenRepository
                .findByToken(requestToken)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        UserEntity user = refreshToken.getUser();

        if (!user.isEnabled()) {
            throw new RuntimeException("User is not active");
        }

        String newAccessToken = jwtService.generateToken(user);

        log.info("ActionLog.refreshToken.end");

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .message("Access token refreshed successfully")
                .build();
    }


    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user) {

        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshTokenEntity token = RefreshTokenEntity.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        return refreshTokenRepository.save(token);
    }

    @Transactional
    public String logout(String authHeader) {

        log.info("ActionLog.logout.start");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }

        String token = authHeader.substring(7);

        long remainingTime = jwtService.getRemainingTime(token);

        if (remainingTime > 0) {
            tokenBlacklistService.blacklistToken(token, remainingTime);
        }

        UserEntity user = userRepository
                .findByEmail(jwtService.extractUsernameByToken(token))
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        log.info("ActionLog.logout.end");

        return "Logged out successfully";
    }

}