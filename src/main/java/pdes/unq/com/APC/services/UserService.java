package pdes.unq.com.APC.services;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.exceptions.UserNotFoundException;
import pdes.unq.com.APC.interfaces.user.UserRequest;
import pdes.unq.com.APC.interfaces.user.UserResponse;
import pdes.unq.com.APC.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    // Método para crear un nuevo usuario
    public UserResponse validateAndSaveUser(UserRequest userRequest) {
        System.out.println("User - response: validate");
        User userToSave = new User();
        UserResponse userResponse = new UserResponse();

        userToSave.setEmail(userRequest.getEmail());
        userToSave.setUsername(userRequest.getUsername());
        userToSave.setPassword(userRequest.getPassword());
        userToSave.setRoleType(userRequest.getRoleType());
        try {
            User savedUser = userRepository.save(userToSave);
            userResponse.setId(savedUser.getId().toString());
        }catch(DataAccessException e) {
            System.out.println("creating user Error: "+  e.getMessage());
            throw new RuntimeException("Error al guardar el usuario: " + e.getMessage(), e);
        }
        return userResponse;
    }

    // Método para eliminar un usuario por su ID
    public void deleteUserByEmail(String email) {
        User userToDelete = userRepository.findByEmail(email);

        if (userToDelete == null) {
            throw new UserNotFoundException("User with email " + email + " not found.");
        }

        try {
            userRepository.delete(userToDelete);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error while deleting user: " + e.getMessage(), e);
        }
    }

    // Método para actualizar un usuario existente
    public User updateUser(String email, UserRequest userRequest) {
      // Buscar el usuario por email
        User existingUser = userRepository.findByEmail(email);

        if (existingUser == null) {
            // Si el usuario no existe, lanzar una excepción personalizada
            throw new UserNotFoundException("User with email " + email + " not found.");
        }

        // Actualizar los campos del usuario con los datos del request
        if (userRequest.getUsername() != null) {
            existingUser.setUsername(userRequest.getUsername());
        }

        if (userRequest.getPassword() != null) {
            existingUser.setPassword(userRequest.getPassword());
        }

        if (userRequest.getRoleType() != null) {
            existingUser.setRoleType(userRequest.getRoleType());
        }

        existingUser.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(existingUser);

       return updatedUser;
    }

    public User getUserByEmail(String email) {
       try {
            User userToReturn = userRepository.findByEmail(email);
            if (userToReturn == null) {
                throw new UserNotFoundException("User with email " + email + " not found.");
            }
            return userToReturn;

       }catch(DataAccessException e){
        System.out.println("getting user by email Error: "+  e.getMessage());
        throw new RuntimeException("Error getting user by email: " + e.getMessage(), e);
       }
    }
}

