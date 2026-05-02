package com.ilham.doctorbookingsystem.service;

import com.ilham.doctorbookingsystem.entity.AppointmentEntity;
import com.ilham.doctorbookingsystem.entity.DoctorEntity;
import com.ilham.doctorbookingsystem.entity.UserEntity;
import com.ilham.doctorbookingsystem.enums.AppointmentStatus;
import com.ilham.doctorbookingsystem.enums.ApprovalStatus;
import com.ilham.doctorbookingsystem.enums.ErrorMessage;
import com.ilham.doctorbookingsystem.exception.CustomException;
import com.ilham.doctorbookingsystem.exception.ResourceNotFoundException;
import com.ilham.doctorbookingsystem.mapper.AppointmentMapper;
import com.ilham.doctorbookingsystem.mapper.DoctorMapper;
import com.ilham.doctorbookingsystem.model.response.AppointmentForDoctorResponseDto;
import com.ilham.doctorbookingsystem.model.response.DoctorResponseDto;
import com.ilham.doctorbookingsystem.rabbitmq.dto.AppointmentMessage;
import com.ilham.doctorbookingsystem.rabbitmq.producer.RabbitMQProducer;
import com.ilham.doctorbookingsystem.repository.AppointmentRepository;
import com.ilham.doctorbookingsystem.repository.DoctorRepository;
import com.ilham.doctorbookingsystem.repository.UserRepository;
import com.ilham.doctorbookingsystem.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;

    private final DoctorMapper doctorMapper;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AppointmentRepository appointmentRepository;

    private final AppointmentMapper appointmentMapper;

    private final RabbitMQProducer producer;

    private DoctorEntity getDoctorById(Long id) {
        return doctorRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND));
    }

    @Transactional
    public DoctorResponseDto approveDoctor(Long id) {
        log.info("ActionLog.approveDoctor.start");
        DoctorEntity doctor = getDoctorById(id);

        if (doctor.getStatus() == ApprovalStatus.APPROVED) {
            throw new CustomException(ErrorMessage.DOCTOR_ALREADY_APPROVED);
        }

            doctor.setStatus(ApprovalStatus.APPROVED);

        doctorRepository.save(doctor);

        log.info("ActionLog.approveDoctor.end");
        return doctorMapper.entityToResponse(doctor);
    }

    public Page<DoctorResponseDto> getAllPendingDoctors(Pageable pageable) {
        log.info("ActionLog.getPendingDoctors.start");

        Page<DoctorEntity> doctorEntities =
                doctorRepository.findAllByStatus(ApprovalStatus.PENDING, pageable);

        Page<DoctorResponseDto> doctorResponseDtos = doctorEntities.map(doctorMapper::entityToResponse);

        log.info("ActionLog.getPendingDoctors.end");

        return doctorResponseDtos;
    }

    @Transactional
    public DoctorResponseDto rejectDoctor(Long id) {
        log.info("ActionLog.rejectDoctor.start");
        DoctorEntity doctor = getDoctorById(id);
        if (doctor.getStatus() == ApprovalStatus.REJECTED) {
            throw new CustomException(ErrorMessage.DOCTOR_ALREADY_REJECTED);
        }
        doctor.setStatus(ApprovalStatus.REJECTED);
        doctorRepository.save(doctor);
        log.info("ActionLog.rejectDoctor.end");
        return doctorMapper.entityToResponse(doctor);
    }

    public Page<AppointmentForDoctorResponseDto> getAllMyAppointments(HttpServletRequest request, Pageable pageable) {
        log.info("ActionLog.getAllMyAppointments.start");
        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(request))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));


        DoctorEntity doctorEntity = doctorRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND));

        Page<AppointmentEntity> appointmentEntities = appointmentRepository.findByDoctorId(doctorEntity.getId(), pageable);

        log.info("ActionLog.getAllMyAppointments.end");
        return appointmentEntities.map(appointmentMapper::entityToForDto);
    }


    @Transactional
    public AppointmentForDoctorResponseDto confirmAppointmentByDoctor(Long id, HttpServletRequest request) {
        log.info("ActionLog.confirmAppointmentByDoctor.start");
        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(request))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

        DoctorEntity doctorEntity = doctorRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND));

        AppointmentEntity appointmentEntity = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.APPOINTMENT_NOT_FOUND));

        if (!appointmentEntity.getDoctor().getId().equals(doctorEntity.getId())) {
            throw new RuntimeException("You cannot confirm this appointment");
        }

        if (appointmentEntity.getStatus() == AppointmentStatus.PENDING){
            appointmentEntity.setStatus(AppointmentStatus.CONFIRMED);
            appointmentEntity.setUpdatedAt(LocalDateTime.now());
        }else{
            throw new CustomException(ErrorMessage.YOU_CANNOT_CONFIRM_APPOINTMENT);
        }
        appointmentRepository.save(appointmentEntity);

        AppointmentMessage message = new AppointmentMessage(
                appointmentEntity.getPatient().getUser().getEmail(),
                appointmentEntity.getPatient().getUser().getFirstName(),
                doctorEntity.getUser().getFirstName() + " " + doctorEntity.getUser().getLastName(),
                appointmentEntity.getAppointmentDate().toString(),
                appointmentEntity.getAppointmentTime().toString(),
                "CONFIRMED"
        );

        producer.sendAppointment(message);

        log.info("ActionLog.confirmAppointmentByDoctor.end");
        return appointmentMapper.entityToForDto(appointmentEntity);
    }


    @Transactional
    public AppointmentForDoctorResponseDto cancelAppointmentByDoctor(Long id, HttpServletRequest request) {
        log.info("ActionLog.cancelAppointmentByDoctor.start");
        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(request))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

        DoctorEntity doctorEntity = doctorRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.DOCTOR_NOT_FOUND));

        AppointmentEntity appointmentEntity = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.APPOINTMENT_NOT_FOUND));

        if (!appointmentEntity.getDoctor().getId().equals(doctorEntity.getId())) {
            throw new CustomException(ErrorMessage.CANNOT_CANCEL_APPOINTMENT);
        }

        if (appointmentEntity.getStatus() == AppointmentStatus.PENDING || appointmentEntity.getStatus() == AppointmentStatus.CONFIRMED){
            appointmentEntity.setStatus(AppointmentStatus.CANCELLED);
            appointmentEntity.setUpdatedAt(LocalDateTime.now());
        }else{
            throw new CustomException(ErrorMessage.PENDING_OR_CONFIRM_APPOINTMENT_CAN_BE_CANCELED);
        }
        appointmentRepository.save(appointmentEntity);

        AppointmentMessage message = new AppointmentMessage(
                appointmentEntity.getPatient().getUser().getEmail(),
                appointmentEntity.getPatient().getUser().getFirstName(),
                doctorEntity.getUser().getFirstName() + " " + doctorEntity.getUser().getLastName(),
                appointmentEntity.getAppointmentDate().toString(),
                appointmentEntity.getAppointmentTime().toString(),
                "CANCELLED"
        );

        producer.sendAppointment(message);

        log.info("ActionLog.cancelAppointmentByDoctor.end");
        return appointmentMapper.entityToForDto(appointmentEntity);
    }


    @Transactional
    public AppointmentForDoctorResponseDto completeAppointmentByDoctor(Long id, HttpServletRequest request) {
        log.info("ActionLog.completeAppointmentByDoctor.start");
        UserEntity userEntity = userRepository.findById(jwtService.extractUserIdFromAccessToken(request))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND));

        DoctorEntity doctorEntity = doctorRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new CustomException(ErrorMessage.DOCTOR_NOT_FOUND));

        AppointmentEntity appointmentEntity = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.APPOINTMENT_NOT_FOUND));

        if (!appointmentEntity.getDoctor().getId().equals(doctorEntity.getId())) {
            throw new CustomException(ErrorMessage.CANNOT_COMPLETE_APPOINTMENT);
        }

        if (appointmentEntity.getStatus() == AppointmentStatus.CONFIRMED){
            appointmentEntity.setStatus(AppointmentStatus.COMPLETED);
            appointmentEntity.setUpdatedAt(LocalDateTime.now());
        }else{
            throw new CustomException(ErrorMessage.YOU_CAN_COMPLETE_ONLY_CONFIRMED_APPOINTMENTS);
        }
        appointmentRepository.save(appointmentEntity);
        log.info("ActionLog.completeAppointmentByDoctor.end");
        return appointmentMapper.entityToForDto(appointmentEntity);
    }
}
