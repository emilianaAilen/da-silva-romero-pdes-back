package pdes.unq.com.APC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pdes.unq.com.APC.interfaces.user.UserRequest;
import pdes.unq.com.APC.interfaces.user.UserResponse;
import pdes.unq.com.APC.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {


    @Autowired
    private UserService userService;

     @PostMapping("/create")
    public ResponseEntity<UserResponse> crearUsuario(@RequestBody UserRequest userRequest) {
        UserResponse res = userService.validateAndSaveUser(userRequest);
       return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

        @GetMapping("/read/{email}")
    public ResponseEntity<UserResponse> getUsuario(@PathVariable String email) {
        UserResponse res = userService.getUserByEmail(email);
       return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteUsuario(@PathVariable String email) {
        userService.deleteUserByEmail(email);
       return new ResponseEntity<>("Usuario eliminado", HttpStatus.OK);
    }

    @PatchMapping("/update/{email}")
    public ResponseEntity<UserResponse> updateUsuario(@PathVariable String email,
    @RequestBody UserRequest updatedUser) {
       UserResponse res = userService.updateUser(email,updatedUser);
       return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
}
