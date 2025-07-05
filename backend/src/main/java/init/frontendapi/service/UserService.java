package init.frontendapi.service;

import init.frontendapi.dto.UserDto;
import init.frontendapi.entity.User;
import init.frontendapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "frontend/src/img/";

    public UserDto createUser(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAvatar("https://via.placeholder.com/40");

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    public UserDto loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return convertToDto(user);
            }
        }
        throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    public UserDto updateUser(Long userId, String name, String email, String avatar) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(name);
            user.setEmail(email);
            if (avatar != null && !avatar.isEmpty()) {
                user.setAvatar(avatar);
            }
            User updatedUser = userRepository.save(user);
            return convertToDto(updatedUser);
        }
        throw new RuntimeException("사용자를 찾을 수 없습니다.");
    }

    public String uploadImage(MultipartFile file) throws IOException {
        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageFile(originalFilename)) {
            throw new RuntimeException("유효하지 않은 이미지 파일입니다.");
        }

        // 파일 크기 검증 (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("파일 크기는 5MB 이하여야 합니다.");
        }

        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 고유한 파일명 생성
        String fileExtension = getFileExtension(originalFilename);
        String fileName = "user_" + UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(fileName);

        // 파일 저장
        Files.copy(file.getInputStream(), filePath);

        return "/src/img/" + fileName;
    }

    private boolean isValidImageFile(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        return lowerCaseFilename.endsWith(".jpg") || 
               lowerCaseFilename.endsWith(".jpeg") || 
               lowerCaseFilename.endsWith(".png") || 
               lowerCaseFilename.endsWith(".gif");
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }

    private UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAvatar());
    }
} 