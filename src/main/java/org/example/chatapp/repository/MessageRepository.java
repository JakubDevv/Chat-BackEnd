package org.example.chatapp.repository;

import jakarta.transaction.Transactional;
import org.example.chatapp.model.Group;
import org.example.chatapp.model.Message;
import org.example.chatapp.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findFirstByGroupIdOrderBySentAtDesc(long groupId);

    List<Message> findAllByGroup_IdOrderBySentAt(long groupId);

    Long countAllByGroup_Id(long groupId);

    @Query("SELECT COUNT(*) FROM Message m JOIN m.group g WHERE g.id = ?1 AND m.sender_id = ?2")
    Long countAllByGroup_IdAndSender_idEquals(Long groupId, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.group = ?1")
    void removeAllByGroup(Group group);
}
