package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.commons.utils.PropertyChecker;
import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.ReferenceRepo;
import com.innopolis.referencestorage.repos.UserRepo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Slf4j
@NoArgsConstructor
@Service
public class ReferenceService {
    private ReferenceRepo referenceRepo;
    private UserRepo userRepo;

    private final Short uidAdditionMethod = 0;

    @Autowired
    public ReferenceService(ReferenceRepo referenceRepo, UserRepo userRepo) {
        this.referenceRepo = referenceRepo;
        this.userRepo = userRepo;
    }

    public List<ReferenceDescription> loadAllUserRefs(User author) {
        return referenceRepo.findByUidUser(author.getUid());
    }

    public Page<ReferenceDescription> loadRefsByUserUid(User author, Pageable pageable) {
        return referenceRepo.findByUidUser(author.getUid(), pageable);
    }

    /**
     *
     * @param userId - for which user reference
     * @param reference - reference
     * @return reference
     */
    public ReferenceDescription addReference(Long userId, ReferenceDescription reference, String url){
        log.info("Получена ссылка на добавление\n userId- {}, \n reference - {}", userId, reference.toString());
        User user = checkIfUserExists(userId);

        reference.setUidUser(userId);
        reference.setReference(new Reference(url, 1));
        reference.setUidAdditionMethod(uidAdditionMethod);
        reference.setAdditionDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        if(reference.getName()== null || reference.getName().equals(""))
            reference.setName(reference.getReference().getUrl());

        referenceRepo.save(reference);

        return reference;
    }

    /**
     *
     * @param refId id of reference
     * @param reference reference
     * @return reference
     */
    public ReferenceDescription updateReference(Long refId, ReferenceDescription reference, String url) {
        log.info("Получена ссылка на обновление\n refId - {}, \n Ссылка - {}", reference.getUid(), url);

        ReferenceDescription item = checkIfReferenceExists(refId);

        BeanUtils.copyProperties(
                reference,
                item,
                PropertyChecker.getNullPropertyNames(reference)
        );

        referenceRepo.save(item);

        return item;
    }

    public ReferenceDescription deleteReference(Long refId) {
        log.info("Получен запрос на удаление ссылки\n refId- {}", refId);

        ReferenceDescription item = checkIfReferenceExists(refId);

        referenceRepo.delete(item);

        return item;
    }

    /**
     * Проверяем, существует-ли запрашиваемый пользователь
     * */
    private User checkIfUserExists(Long userId) {
        User user = userRepo.findByUid(userId);
        assertNotNull(user, String.format("Указан несуществующий userId - %s", userId));
        return user;
    }

    private ReferenceDescription checkIfReferenceExists(Long refId) {
        ReferenceDescription data = referenceRepo.findByUid(refId);
        assertNotNull(data, String.format("Указана несуществующая ссылка, refId - %s", refId));

        return data;
    }
}
