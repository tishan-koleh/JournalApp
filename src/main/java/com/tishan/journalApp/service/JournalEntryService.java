package com.tishan.journalApp.service;

import com.tishan.journalApp.entity.JournalEntry;
import com.tishan.journalApp.entity.User;
import com.tishan.journalApp.repository.JournalEntryRepository;
import com.tishan.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserRepository userRepository;

    public void saveEntry(JournalEntry journalEntry, String userName) throws Exception {
        User user = userRepository.findByUserName(userName);
        try {
            journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(journalEntry);
            userRepository.save(user);
        } catch (Exception e) {
            journalEntryRepository.deleteById(journalEntry.getId());
            throw new Exception(e.getMessage());
        }
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> getEntryById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    public void deleteEntry(ObjectId id, String userName) throws Exception {
        try {
            User user = userRepository.findByUserName(userName);
            user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            userRepository.save(user);
            journalEntryRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ResponseEntity<?> updateJournalEntry(String id, JournalEntry newEntry, String username) {
        try {
            ObjectId objectId = new ObjectId(id);
            Optional<JournalEntry> optionalEntry = journalEntryRepository.findById(objectId);
            JournalEntry oldEntry;
            if (optionalEntry.isPresent()) {
                oldEntry = optionalEntry.get();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
            }

            if (oldEntry.equals(newEntry)) return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("SAME");
            if (!newEntry.getTitle().equals(oldEntry.getTitle())) oldEntry.setTitle(newEntry.getTitle());
            if (!newEntry.getContent().equals(oldEntry.getContent())) oldEntry.setContent(newEntry.getContent());
            journalEntryRepository.save(oldEntry);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }
}
