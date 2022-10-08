package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserEmailAlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.Map.Entry;

@Repository
public class UserRepositoryInMemory implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    private final Set<String> emailList = new HashSet<>();
    private Long generatorId = 1L;

    @Override
    public User saveUser(User user) {
        user.setId(generatorId);
        users.put(generatorId, user);
        if (!emailList.contains(user.getEmail())) {
            emailList.add(user.getEmail());
        } else {
            throw new UserEmailAlreadyExistsException("Пользователь с таким email уже есть в базе");
        }
        generatorId++;
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> listOfUserDto = new ArrayList<>();
        for (Entry<Long, User> entry : users.entrySet()) {
            User user = entry.getValue();
            listOfUserDto.add(user);
        }
        return listOfUserDto;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public User updateUser(User user, Long id) {
        emailList.remove(getUserById(id).getEmail());
        if (!emailList.contains(user.getEmail())) {
            emailList.add(user.getEmail());
        } else {
            throw new UserEmailAlreadyExistsException("Пользователь с таким email уже есть в базе");
        }
        users.put(id, user);
        return user;
    }

    @Override
    public void removeUserById(Long id) {
        emailList.remove(getUserById(id).getEmail());
        users.remove(id);
    }
}
