package org.scouts105bentaya.service;

import org.scouts105bentaya.entity.ContactMessage;

import java.util.List;

public interface ContactMessageService {
    List<ContactMessage> findAll();
    ContactMessage save(ContactMessage contactMessage);
    void sendContactMessageEmail(ContactMessage contactMessage);
}
