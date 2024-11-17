package Freeing.user_service.service;

import Freeing.user_service.dto.UserDto;
import Freeing.user_service.error.NotFoundException;
import Freeing.user_service.error.UserNotFoundException;
import Freeing.user_service.repository.RefreshToken;
import Freeing.user_service.repository.RefreshTokenRepository;
import Freeing.user_service.repository.UserEntity;
import Freeing.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService{
    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }




    @Override
    public UserDto createUser(UserDto userDto) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);  // 여기서 STRICT 전략을 설정합니다.
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        log.info("새로운 회원 생성: " + userDto.getEmail());
        UserDto returnUserDto = mapper.map(userRepository.save(userEntity), UserDto.class);
        log.info("회원 정보 저장 완료: " + returnUserDto.getEmail());
        return returnUserDto;
    }

    @Override
    public boolean checkEmail(String email) {
        log.info("이메일 중복 체크: " + email);
        boolean result = userRepository.findByEmail(email).isPresent();
        log.info(email+"의 중복 여부: "+result);
        return result; // 이메일 존재 여부 확인
    }

    @Override
    public UserDto getUserByUserId(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return new ModelMapper().map(userEntity, UserDto.class);
    }



    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found with email: "+email));

    }


    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        // 사용자 정보 조회
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // 현재 비밀번호가 맞는지 확인
        if (!bCryptPasswordEncoder.matches(currentPassword, userEntity.getEncryptedPassword())) {
            return false; // 현재 비밀번호가 틀리면 변경하지 않음
        }

        // 새로운 비밀번호로 변경
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(userEntity);
        log.info("비밀번호 변경 완료: " + userEntity.getEmail());
        return true;
    }

    @Override
    public boolean setNewPassword(String email, String newPassword) {

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found with email: "+email));

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(userEntity);
        log.info("비밀번호 재설정 완료: "+userEntity.getEmail());

        return true;
    }


    @Override
    public void deleteUser(Long userId) {

        refreshTokenRepository.deleteByUserUserId(userId);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(
                        ()-> new NotFoundException(userId+": 해당 사용자를 찾을 수 없습니다.")
                        );
        userRepository.delete(user);
    }
}
