package com.tishan.journalApp.controller;

import com.tishan.journalApp.entity.JournalEntry;
import com.tishan.journalApp.service.JournalEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;


    @GetMapping
    public List<JournalEntry> getAll() {
        return journalEntryService.getAll();
    }

    @PostMapping
    public boolean createEntry(@RequestBody JournalEntry journalEntry) {
        journalEntryService.saveEntry(journalEntry);
        return true;
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

    @DeleteMapping("id/{thisId}")
    public boolean deleteJournalEntryById(@PathVariable("thisId") String thisId) {
        try {
            ObjectId objectId = new ObjectId(thisId);
            journalEntryService.deleteEntry(objectId);
            return true;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
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
        journalEntryService.saveEntry(oldEntry);
        return true;
    }

}
