package com.ntt.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntt.authentication.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
