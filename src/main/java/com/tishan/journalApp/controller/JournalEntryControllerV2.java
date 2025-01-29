package com.tishan.journalApp.controller;

import com.tishan.journalApp.entity.JournalEntry;
import com.tishan.journalApp.entity.User;
import com.tishan.journalApp.service.JournalEntryService;
import com.tishan.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;


    @GetMapping("/{userName}")
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUser(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.OK).body(user.getJournalEntries());
    }

    @PostMapping("/{userName}")
    public ResponseEntity<?> createJournalEntryForUser(@RequestBody JournalEntry journalEntry, @PathVariable String userName) {
        try {
            journalEntryService.saveEntry(journalEntry, userName);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @GetMapping("id/{thisId}")
    public JournalEntry getJournalEntryById(@PathVariable("thisId") String thisId) {
        try {
            ObjectId objectId = new ObjectId(thisId);
            return journalEntryService.getEntryById(objectId).orElse(null);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid ObjectId format: " + thisId);
        }
    }

    @DeleteMapping("id/{thisId}/{userName}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable("thisId") String thisId, @PathVariable("userName") String userName) {
        try {
            ObjectId objectId = new ObjectId(thisId);
            journalEntryService.deleteEntry(objectId, userName);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("id/{thisId}")
    public boolean updateJournalEntry(@PathVariable("thisId") String thisId, @RequestBody JournalEntry journalEntry){
        JournalEntry oldEntry = getJournalEntryById(thisId);
        JournalEntry newEntry = journalEntry;
        if (oldEntry.equals(newEntry)){
            return false;
        }else {
            oldEntry.setId(new ObjectId(thisId));
            if (!newEntry.getTitle().isEmpty() && newEntry.getTitle() != null && !newEntry.getTitle().equals(oldEntry.getTitle())){
                oldEntry.setTitle(newEntry.getTitle());
            }
            if (!newEntry.getContent().isEmpty() && newEntry.getContent() != null && !newEntry.getContent().equals(oldEntry.getContent())){
                oldEntry.setContent(newEntry.getContent());
            }
        }
        /*journalEntryService.saveEntry(oldEntry, userName);*/
        return true;
    }

}
