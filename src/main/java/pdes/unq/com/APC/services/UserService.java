package pdes.unq.com.APC.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.exceptions.UserNotFoundException;
import pdes.unq.com.APC.interfaces.user.UserPurchaseResponse;
import pdes.unq.com.APC.interfaces.user.UserRequest;
import pdes.unq.com.APC.interfaces.user.UserResponse;
import pdes.unq.com.APC.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Método para crear un nuevo usuario
    public UserResponse validateAndSaveUser(UserRequest userRequest) {
        System.out.println("User - response: validate");
        User userToSave = new User();
        UserResponse userResponse = new UserResponse();
        String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());

        userToSave.setEmail(userRequest.getEmail());
        userToSave.setUsername(userRequest.getUsername());
        userToSave.setPassword(encryptedPassword);
        userToSave.setRoleType(userRequest.getRoleType());
        try {
            User savedUser = userRepository.save(userToSave);
            userResponse.setId(savedUser.getId().toString());
            userResponse.setUsername(savedUser.getUsername());
            userResponse.setEmail(savedUser.getEmail());
            userResponse.setCreated_at(savedUser.getCreatedAt());
        } catch (DataAccessException e) {
            System.out.println("creating user Error: " + e.getMessage());
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
            String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());
            existingUser.setPassword(encryptedPassword);
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

        } catch (DataAccessException e) {
            System.out.println("getting user by email Error: " + e.getMessage());
            throw new RuntimeException("Error getting user by email: " + e.getMessage(), e);
        }
    }

    public User getUserById( String userID){
        try {
            UUID uuid = UUID.fromString(userID);
            Optional<User> userToReturn = userRepository.findById(uuid);
            if (userToReturn.isEmpty()) {
                throw new UserNotFoundException("User with id " + userID + " not found.");
            }
            return userToReturn.get();
        } catch (DataAccessException e) {
            System.out.println("getting user by email Error: " + e.getMessage());
            throw new RuntimeException("Error getting user by email: " + e.getMessage(), e);
        }
    }

    public List<UserResponse> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream()
            .map(this::MapUserToUserResponse)
            .collect(Collectors.toList());
    }

    public List<UserPurchaseResponse> getTopUsersWithPurchasessProducts(int limit){
        List<Object[]>  userPurchasesProducts = userRepository.findTopUsersWithMostPurchasesProducts(limit);
        return userPurchasesProducts.stream().map(this::mapObjectToUserPurchaseResponse).collect(Collectors.toList());
    }

    /**
     * Metodo que se encarga de mapear Object con los datos traidos de la base de datos a UserPurchaseResponse
     * @param object
     * @return UserPurchaseResponse
     */
    private UserPurchaseResponse mapObjectToUserPurchaseResponse(Object[] object){
        UserPurchaseResponse res = new UserPurchaseResponse();

        String dateTimeStr = object[3].toString().split("\\.")[0]; // Elimina los milisegundos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
 
        res.setId(object[0].toString());
        res.setUsername(object[1].toString());
        res.setEmail(object[2].toString());
        res.setCreated_at( localDateTime);
        res.setCantPurchasesProducts(  Integer.parseInt( object[4].toString()));

        return res;
    }

    private UserResponse MapUserToUserResponse( User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId().toString());
        userResponse.setCreated_at(user.getCreatedAt());
        userResponse.setEmail(user.getEmail());
        userResponse.setUsername(user.getUsername());
        return userResponse;
    } 
}
