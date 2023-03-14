package com.smart.dao;

import com.smart.models.Contact;
import com.smart.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact,Integer>
{
    // for pagination...
    @Query("from Contact as c where c.user.id =:userId")
    public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
    // current page + contacts/page

    public List<Contact> findByNameContainingAndUser(String name, User user);  // search
}
