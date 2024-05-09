package finalproject.ss3v2.service;

import finalproject.ss3v2.domain.Profile;
import finalproject.ss3v2.repository.ProfileRespository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    ProfileRespository profileRespository;

    public ProfileService(ProfileRespository profileRespository) {
        this.profileRespository = profileRespository;
    }

    public Profile saveProfile(Profile profile) {
        return profileRespository.save(profile);
    }
}