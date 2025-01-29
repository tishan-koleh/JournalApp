package com.tishan.journalApp.service;

import com.tishan.journalApp.entity.JournalEntry;
import com.tishan.journalApp.entity.User;
import com.tishan.journalApp.repository.JournalEntryRepository;
import com.tishan.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserRepository userRepository;

    public void saveEntry(JournalEntry journalEntry, String userName) throws Exception{
        User user = userRepository.findByUserName(userName);
        try {
            journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(journalEntry);
            userRepository.save(user);
        }catch (Exception e){
            journalEntryRepository.deleteById(journalEntry.getId());
            throw new Exception(e.getMessage());
        }
    }

    public List<JournalEntry> getAll(){
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> getEntryById(ObjectId id){
        return journalEntryRepository.findById(id);
    }

    public void deleteEntry(ObjectId id, String userName) throws Exception{
        try {
            User user = userRepository.findByUserName(userName);
            user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            userRepository.save(user);
            journalEntryRepository.deleteById(id);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
}
