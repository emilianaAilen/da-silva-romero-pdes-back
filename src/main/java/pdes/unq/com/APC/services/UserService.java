package pdes.unq.com.APC.services;

import java.util.Date;

import org.springframework.stereotype.Service;

import pdes.unq.com.APC.interfaces.user.UserRequest;
import pdes.unq.com.APC.interfaces.user.UserResponse;

@Service
public class UserService {
    
    // Método para crear un nuevo usuario
    public UserResponse validateAndSaveUser(UserRequest userRequest) {
       
        UserResponse response = new UserResponse();

        response.setCreated(new Date());
        response.setId("1");
        response.setLast_login(new Date());

        return response;

    }

    // Método para eliminar un usuario por su ID
    public void deleteUserByEmail(String email) {
       //ToDo delete a user
    }

    // Método para actualizar un usuario existente
    public UserResponse updateUser(String email, UserRequest userRequest) {
        //Todo: update in database all the fields.

        UserResponse response = new UserResponse();

        response.setCreated(new Date());
        response.setId("1");
        response.setLast_login(new Date());
        response.setIsactive(true);

        return response;
    }


    public UserResponse getUserByEmail(String email) {
       //Todo get a user by email

       UserResponse response = new UserResponse();

       response.setCreated(new Date());
       response.setId("1");
       response.setLast_login(new Date());
       response.setIsactive(true);

       return response;
    }



}

