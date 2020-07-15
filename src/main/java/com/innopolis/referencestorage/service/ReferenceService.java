package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.ReferenceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReferenceService {
    private ReferenceRepo referenceRepo;

    @Autowired
    public ReferenceService(ReferenceRepo referenceRepo) {
        this.referenceRepo = referenceRepo;
    }

    public Page<Reference> loadRefsByUserUid(User author, Pageable pageable) {
        return referenceRepo.findByUidUser(author.getUid(), pageable);
    }
}
