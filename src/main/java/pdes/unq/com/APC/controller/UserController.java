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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import pdes.unq.com.APC.entities.User;
import pdes.unq.com.APC.interfaces.user.UserRequest;
import pdes.unq.com.APC.interfaces.user.UserResponse;
import pdes.unq.com.APC.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully created",
                     content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{ \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"email\": \"user@example.com\", \"role\": \"common\" }"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{ \"status\": \"Internal_error\", \"message\": \"Error message\" }")))
    })
    @PostMapping("/create")
    public ResponseEntity<?> crearUsuario(@RequestBody UserRequest userRequest) {
        UserResponse savedUser = userService.validateAndSaveUser(userRequest);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Retrieve user by email (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
                     content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{ \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"email\": \"user@example.com\", \"role\": \"admin\" }"))),
        @ApiResponse(responseCode = "404", description = "User not found",
                     content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{ \"status\": \"User_not_found\", \"message\": \"User with email not found\" }"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{ \"status\": \"Internal_error\", \"message\": \"Error message\" }")))
    })
    @GetMapping("/admin/read/{email}")
    public ResponseEntity<?> getUsuario(@PathVariable String email) {
        User res = userService.getUserByEmail(email);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Delete user by email (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully deleted",
                     content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "\"Usuario eliminado\""))),
        @ApiResponse(responseCode = "404", description = "User not found",
                     content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{ \"status\": \"User_not_found\", \"message\": \"User with email not found\" }"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{ \"status\": \"Internal_error\", \"message\": \"Error message\" }")))
    })
    @DeleteMapping("/admin/delete/{email}")
    public ResponseEntity<?> deleteUsuario(@PathVariable String email) {
        userService.deleteUserByEmail(email);
       return new ResponseEntity<>("Usuario eliminado", HttpStatus.OK);
    }

    @Operation(summary = "Update user by email (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully updated",
                     content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{ \"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"email\": \"user@example.com\", \"role\": \"admin\" }"))),
        @ApiResponse(responseCode = "404", description = "User not found",
                     content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{ \"status\": \"User_not_found\", \"message\": \"User with email not found\" }"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                     content = @Content(mediaType = "application/json",
                                        examples = @ExampleObject(value = "{ \"status\": \"Internal_error\", \"message\": \"Error message\" }")))
    })
    @PatchMapping("/admin/update/{email}")
    public ResponseEntity<?> updateUsuario(@PathVariable String email,
    @RequestBody UserRequest updatedUser) {
       User res = userService.updateUser(email,updatedUser);
       return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
}
