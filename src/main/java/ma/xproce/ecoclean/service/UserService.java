package ma.xproce.ecoclean.service;

import ma.xproce.ecoclean.dao.entities.User;
import java.util.List;

public interface UserService {
    User getUserById(Long id);
    List<User> getAllUsers();
    User saveUser(User user);
    boolean deleteUser(Long id);
    User findByEmail(String email);
}