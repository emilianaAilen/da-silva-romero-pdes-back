package pdes.unq.com.APC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.interfaces.user.UserRequest;
import pdes.unq.com.APC.interfaces.user.UserResponse;
import pdes.unq.com.APC.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {


    @Autowired
    private UserService userService;

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/create")
    public ResponseEntity<?> crearUsuario(@RequestBody UserRequest userRequest) {
        UserResponse savedUser = userService.validateAndSaveUser(userRequest);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/read/{email}")
    public ResponseEntity<?> getUsuario(@PathVariable String email) {
        User res = userService.getUserByEmail(email);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<?> deleteUsuario(@PathVariable String email) {
        userService.deleteUserByEmail(email);
       return new ResponseEntity<>("Usuario eliminado", HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PatchMapping("/update/{email}")
    public ResponseEntity<?> updateUsuario(@PathVariable String email,
    @RequestBody UserRequest updatedUser) {
       User res = userService.updateUser(email,updatedUser);
       return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
}
