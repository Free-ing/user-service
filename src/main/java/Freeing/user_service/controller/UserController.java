package Freeing.user_service.controller;

import Freeing.user_service.dto.UserDto;
import Freeing.user_service.error.ForbiddenException;
import Freeing.user_service.error.InvalidCredentialsException;
import Freeing.user_service.error.TokenValidationResult;
import Freeing.user_service.repository.RefreshToken;
import Freeing.user_service.repository.UserEntity;
import Freeing.user_service.security.JwtTokenProvider;
import Freeing.user_service.service.DefaultService;
import Freeing.user_service.service.DeleteService;
import Freeing.user_service.service.RefreshTokenService;
import Freeing.user_service.service.UserService;
import Freeing.user_service.vo.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user-service")
@Slf4j
public class UserController {
    private Environment environment;
    private UserService userService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private RefreshTokenService refreshTokenService;

    private DefaultService defaultService;

    private DeleteService deleteService;

    @Autowired
    public UserController(Environment environment, UserService userService,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          JwtTokenProvider jwtTokenProvider,
                          RefreshTokenService refreshTokenService,
                          DefaultService defaultService,
                          DeleteService deleteService) {
        this.environment = environment;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.defaultService = defaultService;
        this.deleteService = deleteService;
    }

    @GetMapping("/health_check")
    public String status(){
        return String.format("User service is working fine on PORT %s",
                environment.getProperty("local.server.port"));

    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseUser> createUser(@Valid @RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);

        // 사용자 생성
        ResponseUser responseUser = mapper.map(userService.createUser(userDto), ResponseUser.class);

        // 기본 루틴 생성 요청 비동기 처리
        executeRoutineCreation(responseUser.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    // 루틴 생성 요청 메서드 분리
    private void executeRoutineCreation(Long userId) {
        defaultService.addDefaultSleepRoutine(userId).subscribe(response -> log.info("수면 루틴 생성 응답: {}", response));
        defaultService.addDefaultHobbyRoutine(userId).subscribe(response -> log.info("취미 루틴 생성 응답: {}", response));
        defaultService.addDefaultSpiritRoutine(userId).subscribe(response -> log.info("마음 루틴 생성 응답: {}", response));
        defaultService.addDefaultExerciseRoutine(userId).subscribe(response -> log.info("운동 루틴 생성 응답: {}", response));
    }

    @PostMapping("/check-email")
    public boolean checkEmail(@Valid @RequestBody RequestCheckEmail requestCheckEmail) {
        return userService.checkEmail(requestCheckEmail.getEmail());
    }

    @GetMapping("/user/list")
    public ResponseEntity<List<ResponseUser>> getUserList(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader.substring(7);
        int role = jwtTokenProvider.getRoleFromToken(token);
        if (role != 0) { // 예시로 role "0"이 Admin이라고 가정
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


        Iterable<UserEntity> userList= userService.getUserByAll();
        List<ResponseUser> result = new ArrayList<>();

        userList.forEach(v->{
            result.add(new ModelMapper().map(v,ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseUser> getUserByAdmin(@PathVariable("userId")Long userId,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader.substring(7);
        int role = jwtTokenProvider.getRoleFromToken(token);
        if (role != 0) { // 예시로 role "0"이 Admin이라고 가정
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserDto userDto = userService.getUserByUserId(userId);
        ResponseUser responseUser = new ModelMapper().map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseUser> getUser(@RequestHeader(HttpHeaders.AUTHORIZATION)String authorizationHeader){
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        UserDto userDto = userService.getUserByUserId(userId);
        ResponseUser responseUser = new ModelMapper().map(userDto,ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody RequestLogin loginRequest) {
        UserEntity user = userService.findByEmail(loginRequest.getEmail());

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getEncryptedPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getUserId(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getUserId(), user.getRole());

        // updateRefreshToken 메서드가 UserEntity를 사용하도록 수정됨
        refreshTokenService.updateRefreshToken(
                user.getEmail(),
                refreshToken,
                LocalDateTime.now().plusSeconds(7 * 24 * 60 * 60), // 7일 후 만료
                user.getRole());

        log.info(loginRequest.getEmail() + " 로그인 성공");

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }



    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        TokenValidationResult validationResult = jwtTokenProvider.validateToken(refreshToken);

        switch (validationResult) {
            case VALID:
                // 토큰이 유효한 경우
                Optional<RefreshToken> storedToken = refreshTokenService.findByToken(refreshToken);
                if (storedToken.isPresent() && !refreshTokenService.isTokenExpired(storedToken.get())) {
                    UserEntity user = storedToken.get().getUser(); // UserEntity에서 유저 정보를 가져옴
                    String accessToken = jwtTokenProvider.generateAccessToken(
                            user.getEmail(),
                            user.getUserId(),
                            user.getRole());
                    return ResponseEntity.ok(new LoginResponse(accessToken, storedToken.get().getToken()));
                }
                throw new RuntimeException("Invalid or expired refresh token");

            case EXPIRED:
                // 토큰이 만료된 경우
                throw new RuntimeException("Refresh token is expired");

            case UNSUPPORTED:
            case MALFORMED:
            case INVALID_SIGNATURE:
            case ILLEGAL_ARGUMENT:
                // 기타 유효하지 않은 토큰인 경우
                throw new RuntimeException("Invalid refresh token");

            default:
                throw new RuntimeException("Unknown token validation error");
        }
    }


    @PatchMapping("/change-password/after-login")
    public ResponseEntity<String> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                 @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        // 토큰에서 사용자 ID 추출
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // 현재 비밀번호와 새로운 비밀번호를 서비스에 전달하여 변경 처리
        boolean isPasswordChanged = userService.changePassword(userId,
                changePasswordRequest.getCurrentPassword(),
                changePasswordRequest.getNewPassword());

        if (isPasswordChanged) {
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect.");
        }
    }

    @PatchMapping("/change-password/before-login")
    public ResponseEntity<String> changePasswordBeforeLogin(@Valid @RequestBody
                                                                ChangePasswordBeforeRequest
                                                                        changePasswordBeforeRequest){
        boolean isPasswordSetNew = userService.setNewPassword(
                changePasswordBeforeRequest.getEmail(),
                changePasswordBeforeRequest.getNewPassword());

        if(isPasswordSetNew){
            return ResponseEntity.status(HttpStatus.OK).body("비밀번호를 성공적으로 변경했습니다.");

        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 변경을 실패했습니다.");

        }
    }

    @DeleteMapping("/user/remove")
    public ResponseEntity<String> deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // 각 마이크로서비스에 데이터 삭제 요청 비동기 처리
        deleteService.deleteAll(userId).subscribe(response -> log.info("회원 데이터 삭제 완료 응답: {}", response));

        // 사용자 데이터 삭제
        userService.deleteUser(userId);
        log.info("사용자 데이터 삭제 완료: userId = {}", userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userId + "번 사용자의 모든 데이터가 삭제되었습니다.");
    }

    @DeleteMapping("/user/remove/{userId}")
    public ResponseEntity<String> deleteUserByAdmin(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                    @PathVariable Long userId) {
        String token = authorizationHeader.substring(7);
        int role = jwtTokenProvider.getRoleFromToken(token);

        if(role!=0){
            throw new ForbiddenException("회원 삭제 권한이 없습니다.");
        }

        // 각 마이크로서비스에 데이터 삭제 요청 비동기 처리
        deleteService.deleteAll(userId).subscribe(response -> log.info("회원 데이터 삭제 완료 응답: {}", response));

        // 사용자 데이터 삭제
        userService.deleteUser(userId);
        log.info("사용자 데이터 삭제 완료: userId = {}", userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userId + "번 사용자의 모든 데이터가 삭제되었습니다.");
    }

    @DeleteMapping("/user/reset")
    public ResponseEntity<String>resetUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        deleteService.resetAll(userId).subscribe(response -> log.info("회원 데이터 삭제 완료 응답: {}", response));


        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userId+"번 사용자의 데이터가 초기화 되었습니다.");
    }




}
