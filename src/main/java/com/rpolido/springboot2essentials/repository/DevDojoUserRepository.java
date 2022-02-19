package com.rpolido.springboot2essentials.repository;

import com.rpolido.springboot2essentials.domain.DevDojoUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevDojoUserRepository extends JpaRepository<DevDojoUser, Long> {

    DevDojoUser findByUsername(String name);

}
