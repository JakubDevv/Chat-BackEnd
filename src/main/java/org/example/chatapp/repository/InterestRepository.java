package org.example.chatapp.repository;

import org.example.chatapp.dto.InterestDTO;
import org.example.chatapp.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {

    @Query("SELECT new org.example.chatapp.dto.InterestDTO(i.id, i.value) FROM Interest i JOIN i.user u WHERE u.username = ?1")
    List<InterestDTO> findAllByUser_Username(String userName);
}
