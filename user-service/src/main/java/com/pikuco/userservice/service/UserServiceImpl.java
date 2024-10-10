package com.pikuco.userservice.service;

import com.pikuco.userservice.api.QuizAPIClient;
import com.pikuco.userservice.dto.UserPrivacyToUpdateDto;
import com.pikuco.userservice.entity.Token;
import com.pikuco.userservice.entity.User;
import com.pikuco.userservice.exception.ObjectNotValidException;
import com.pikuco.userservice.repository.TokenRepository;
import com.pikuco.userservice.repository.UserRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final QuizAPIClient quizAPIClient;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByToken(String token) {
        Token foundToken = tokenRepository.findByToken(token).orElseThrow(NoSuchElementException::new);
        if (foundToken.isExpired() || foundToken.isRevoked())
            throw new NoSuchElementException();
        return userRepository.findById(foundToken.getUser().getId()).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<User> getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Override
    public User getUserPrivacyByNickname(String nickname, String token) {
        User authUser = getUserByToken(token);
        User userPrivacy = getUserByNickname(nickname).orElseThrow();
        if (!Objects.equals(authUser.getId(), userPrivacy.getId()))
            throw new ObjectNotValidException(new HashSet<>(List.of("Authenticated user it is not the user whose " +
                    "privacy information was requested")));

        return userPrivacy;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateUserPublicByNickname(String token, User newUser, String nickname) {
        try {
            User authUser = getUserByToken(token);
            User userToUpdate = getUserByNickname(nickname).orElseThrow();
            if (!Objects.equals(authUser.getId(), userToUpdate.getId()))
                throw new ObjectNotValidException(new HashSet<>(List.of("Authorized user is not the user that need to update")));
            userToUpdate.setNickname(newUser.getNickname());
            userToUpdate.setDescription(newUser.getDescription());
            userRepository.save(userToUpdate);
            if (!nickname.equals(newUser.getNickname())) {
                ResponseEntity<?> changingNicknameResponse =
                        quizAPIClient.changeUserNicknameInQuizzes(userToUpdate.getId(), newUser.getNickname());
                if (changingNicknameResponse.getStatusCode().is4xxClientError())
                    throw new BadRequestException("Сталася помилка зі сторони клієнта " +
                            "при оновленні нікнейму автора у вікторинах");
                else if (changingNicknameResponse.getStatusCode().is5xxServerError())
                    throw new InternalServerErrorException("Сталася помилка зі сторони сервера " +
                            "при оновленні нікнейму автора у вікторинах");
            }
        } catch (NoSuchElementException e) {
            throw new ObjectNotValidException(new HashSet<>(List.of("User is not logged in or there is " +
                    "no information about user that need to update")));
        }
    }

    @Override
    public void updateUserPrivacyByNickname(String token, UserPrivacyToUpdateDto newUser, String nickname) {
        try {
            User authUser = getUserByToken(token);
            User userToUpdate = getUserByNickname(nickname).orElseThrow();

            if (!Objects.equals(authUser.getId(), userToUpdate.getId()))
                throw new ObjectNotValidException(new HashSet<>(List.of("Authorized user is not the user that need to update")));
            if (!Objects.equals(newUser.currentPassword(), "")) {
                System.out.println(userToUpdate.getPassword());
                System.out.println(passwordEncoder.encode(newUser.currentPassword()));
                if (passwordEncoder.matches(newUser.currentPassword(), userToUpdate.getPassword())) {
                    userToUpdate.setPassword(passwordEncoder.encode(newUser.newPassword()));
                } else {
                    throw new ObjectNotValidException(new HashSet<>(List.of("You entered a wrong current password")));
                }
            }

            userToUpdate.setEmail(newUser.email());
            userRepository.save(userToUpdate);
        } catch (NoSuchElementException e) {
            throw new ObjectNotValidException(new HashSet<>(List.of("User is not logged in or there is " +
                    "no information about user that need to update")));
        }
    }

    @Override
    @Transactional
    public void deleteUser(String token, String nickname, boolean deleteQuizzes) {
        User authUser = getUserByToken(token);
        User userToDelete = getUserByNickname(nickname).orElseThrow();

        if (!Objects.equals(authUser.getId(), userToDelete.getId()))
            throw new ObjectNotValidException(new HashSet<>(List.of("Authorized user is not the user that need to delete")));
        if (deleteQuizzes) {
            quizAPIClient.deleteQuizzesByUserId(authUser.getId());
        }
        tokenRepository.deleteAllByUser(authUser.getId());
        if (userRepository.deleteUserById(authUser.getId()) >= 1)
            return;
        throw new InternalServerErrorException("There is an error while " +
                "deleting the current user. Please try later");
    }
}
