package com.pikuco.dbgateway.repository;

import com.pikuco.dbgateway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
