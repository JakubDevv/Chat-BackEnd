package org.example.chatapp.repository;

import jakarta.transaction.Transactional;
import org.example.chatapp.dto.InterestStatsDTO;
import org.example.chatapp.dto.MessageAmountDTO;
import org.example.chatapp.dto.user.UserImageDTO;
import org.example.chatapp.model.Friends;
import org.example.chatapp.model.User;
import org.example.chatapp.model.enums.FriendStatus;
import org.example.chatapp.model.id.FriendID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friends, FriendID> {

    @Query(value = "SELECT f.friendID.friend_id.id " +
                   "FROM Friends f " +
                   "WHERE f.friendID.user_id.id = ?1 and f.status='PENDING'")
    List<Long> getPending(Long id);

    @Query(value = "SELECT f.friendID.user_id.id " +
            "FROM Friends f " +
            "WHERE f.friendID.friend_id.id = ?1 and f.status='PENDING'")
    List<Long> getToAccept(Long id);

    @Query(value = "SELECT f.friendID.user_id.id " +
            "FROM Friends f " +
            "WHERE f.friendID.friend_id.id = ?1 and f.status='ACCEPTED'")
    List<Long> getFriends1(Long id);

    @Query(value = "SELECT f.friendID.friend_id.id " +
            "FROM Friends f " +
            "WHERE f.friendID.user_id.id = ?1 and f.status='ACCEPTED'")
    List<Long> getFriends2(Long id);

    @Query("SELECT " +
            "CASE WHEN f.friendID.user_id.id = ?1 THEN f.friendID.friend_id.id ELSE f.friendID.user_id.id END " +
            "FROM Friends f " +
            "WHERE (f.friendID.user_id.id = ?1 OR f.friendID.friend_id.id = ?1) and f.status = 'ACCEPTED'")
    List<Long> getFriends(Long id);

    @Query(value = "SELECT COUNT(*) " +
            "FROM ( " +
            "SELECT CASE WHEN f.friendID.user_id.id = :userId1 THEN f.friendID.friend_id.id ELSE f.friendID.user_id.id END as friendId " +
            "FROM Friends f " +
            "WHERE (f.friendID.user_id.id = :userId1 OR f.friendID.friend_id.id = :userId1) AND f.status = 'ACCEPTED' " +
            ") AS user1Friends " +
            "INNER JOIN ( " +
            "SELECT CASE WHEN f.friendID.user_id.id = :userId2 THEN f.friendID.friend_id.id ELSE f.friendID.user_id.id END as friendId " +
            "FROM Friends f " +
            "WHERE (f.friendID.user_id.id = :userId2 OR f.friendID.friend_id.id = :userId2) AND f.status = 'ACCEPTED' " +
            ") AS user2Friends " +
            "ON user1Friends.friendId = user2Friends.friendId")
    Long countCommonFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query(value = "SELECT u.id FROM User u")
    List<Long> getUsers(Long id);

    @Query(value = "SELECT f FROM Friends f JOIN f.friendID.friend_id ff JOIN f.friendID.user_id u WHERE (u.id = ?1 AND ff.id = ?2) OR (ff.id = ?1 AND u.id = ?2)")
    Optional<Friends> getFriendStatus(Long user_id1, Long user_id2);

    @Query(value = "SELECT count(*) FROM Friends f WHERE (f.friendID.friend_id.id = ?1 OR f.friendID.user_id.id = ?1) AND f.status = 'ACCEPTED'")
    Long getAllFriends(Long user_id);

    @Query(value = "SELECT new org.example.chatapp.dto.InterestStatsDTO(i.value, COUNT(i.value) * 100.0 / (SELECT COUNT(*) FROM Friends f WHERE (f.friendID.user_id.id = ?1 OR f.friendID.friend_id.id = ?1) AND f.status='ACCEPTED')) " +
                   "FROM Interest i " +
                   "JOIN i.user u " +
                   "WHERE u.id IN (" +
                   "SELECT f.friendID.user_id.id " +
                   "FROM Friends f " +
                   "WHERE f.friendID.friend_id.id = ?1 AND f.status = 'ACCEPTED') " +
                   "OR u.id IN ( " +
                   "SELECT f.friendID.friend_id.id " +
                   "FROM Friends f " +
                   "WHERE f.friendID.user_id.id = ?1 AND f.status = 'ACCEPTED') " +
                   "GROUP BY i.value")
    List<InterestStatsDTO> calculateFriendInterestStats(Long userId);

    @Modifying
    @Transactional
    @Query(value ="DELETE FROM friends WHERE (user_id = ?1 AND friend_id = ?2) OR (user_id = ?2 AND friend_id = ?1)", nativeQuery = true)
    void removeFriend(Long userId1, Long userId2);

    @Modifying
    @Transactional
    @Query(value ="INSERT INTO friends (user_id, friend_id, status, last_update) VALUES (?1, ?2, 'PENDING', NOW())", nativeQuery = true)
    void createFriendRequest(Long userId1, Long userId2);

    @Modifying
    @Transactional
    @Query(value ="UPDATE friends SET status='ACCEPTED' WHERE (user_id=?1 AND friend_id=?2) OR (user_id = ?2 AND friend_id = ?1)", nativeQuery = true)
    void acceptFriendRequest(Long userId1, Long userId2);

    @Query("SELECT CASE WHEN f.friendID.user_id.id = :id THEN f.friendID.friend_id.id ELSE f.friendID.user_id.id END " +
            "FROM Friends f " +
            "WHERE (f.friendID.friend_id.id = :id OR f.friendID.user_id.id = :id) " +
            "AND f.status = 'ACCEPTED' " +
            "AND ((f.friendID.user_id.last_activity > :activeThreshold AND f.friendID.friend_id.id = :id) OR " +
            "(f.friendID.friend_id.last_activity > :activeThreshold AND f.friendID.user_id.id = :id))")
    List<Long> getOnlineFriends(@Param("id") Long id, @Param("activeThreshold") LocalDateTime activeThreshold);

    @Query("SELECT COUNT(*)" +
            "FROM Friends f " +
            "WHERE (f.friendID.friend_id.id = :id OR f.friendID.user_id.id = :id) " +
            "AND f.status = 'ACCEPTED' " +
            "AND ((f.friendID.user_id.last_activity > :activeThreshold AND f.friendID.friend_id.id = :id) OR " +
            "(f.friendID.friend_id.last_activity > :activeThreshold AND f.friendID.user_id.id = :id))")
    Long getOnlineFriendsCount(@Param("id") Long id, @Param("activeThreshold") LocalDateTime activeThreshold);

    @Query(value = "SELECT " +
            "CASE WHEN ((f.user_id=:user_id AND f.friend_id=:friend_id) OR (f.user_id=:friend_id AND f.friend_id=:user_id)) AND f.status='ACCEPTED' THEN 'ACCEPTED' " +
            "WHEN (f.user_id=:user_id AND f.friend_id=:friend_id) AND f.status='PENDING' THEN 'PENDING' " +
            "WHEN (f.user_id=:friend_id AND f.friend_id=:user_id) AND f.status='PENDING' THEN 'TO_ACCEPT' " +
            "ELSE null END AS friendStatus " +
            "FROM friends f ORDER BY friendStatus LIMIT 1", nativeQuery = true)
    FriendStatus getUsersStatus(Long user_id, Long friend_id);

    @Query(value =
            "SELECT u2 " +
            "FROM Friends f " +
            "JOIN User u1 ON u1 = f.friendID.user_id " +
            "JOIN User u2 ON u2 = f.friendID.friend_id " +
            "WHERE f.status = 'ACCEPTED' AND u1.id = ?1 " +
            "UNION " +
            "SELECT u1 " +
            "FROM Friends f " +
            "JOIN User u1 ON u1 = f.friendID.user_id " +
            "JOIN User u2 ON u2 = f.friendID.friend_id " +
            "WHERE f.status = 'ACCEPTED' AND u2.id = ?1 ", nativeQuery = false)
    List<User> getFriendsCountries(Long user_id);

    @Query("SELECT new org.example.chatapp.dto.MessageAmountDTO(DATE_TRUNC('month', m.sentAt), COUNT(*)) " +
           "FROM Message m " +
           "JOIN User u ON u=m.sender_id " +
           "WHERE u.id=?1 GROUP BY DATE_TRUNC('month', m.sentAt)")
    List<MessageAmountDTO> getSentMessagesByTime(Long id);
}


