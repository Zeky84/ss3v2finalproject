package finalproject.ss3v2.dao.request;

import java.util.Optional;

public record SignUpRequest(String email,//This class is used to create a record for the sign up request
                            String password,
                            String firstName,
                            String lastName,
                            Optional<String> authorityOpt) {

}
