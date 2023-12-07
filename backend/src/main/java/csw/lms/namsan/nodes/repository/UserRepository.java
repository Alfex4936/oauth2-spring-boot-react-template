package csw.lms.namsan.nodes.repository;

import csw.lms.namsan.nodes.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByProvider(String provider);
    Optional<User> findByProviderAndEmail(String provider, String email);
}
