package finalproject.ss3v2.service;

import finalproject.ss3v2.domain.Profile;
import finalproject.ss3v2.repository.ProfileRespository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {
    ProfileRespository profileRespository;

    public ProfileService(ProfileRespository profileRespository) {
        this.profileRespository = profileRespository;
    }

    public Profile saveProfile(Profile profile) {
        return profileRespository.save(profile);
    }
    public Profile getProfileById(Long id) {
        return profileRespository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteProfileById(Long profileId) {
        profileRespository.deleteById(profileId);
    }

    public List<Profile> getProfilesByIds(List<Long> selectedProfileIds) {
        return profileRespository.findAllById(selectedProfileIds);
    }
}
