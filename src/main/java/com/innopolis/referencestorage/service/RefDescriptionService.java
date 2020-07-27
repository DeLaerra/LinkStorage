package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.commons.utils.PropertyChecker;
import com.innopolis.referencestorage.domain.RefDescription;
import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.RefDescriptionRepo;
import com.innopolis.referencestorage.repos.RefRepo;
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
public class RefDescriptionService {
    private RefDescriptionRepo refDescriptionRepo;
    private RefRepo refRepo;
    private UserRepo userRepo;

    private final Short uidAdditionMethod = 0;

    @Autowired
    public RefDescriptionService(RefDescriptionRepo refDescriptionRepo, UserRepo userRepo, RefRepo refRepo) {
        this.refDescriptionRepo = refDescriptionRepo;
        this.userRepo = userRepo;
        this.refRepo = refRepo;
    }

    public List<RefDescription> loadAllUserRefs(User author) {
        return refDescriptionRepo.findByUidUser(author.getUid());
    }

    public Page<RefDescription> loadRefsByUserUid(User author, Pageable pageable) {
        return refDescriptionRepo.findByUidUser(author.getUid(), pageable);
    }

    /**
     *
     * @param userId - for which user reference
     * @param refDescription - reference
     * @return reference
     */
    public RefDescription addReference(Long userId, RefDescription refDescription){
        log.info("Получена ссылка на добавление\n userId- {}, \n reference - {}", userId, refDescription.toString());
        User user = checkIfUserExists(userId);

        assertNotNull(refDescription.getUrl(), "Отсутствует url ссылки");

        Reference reference = refRepo.findByUrl(refDescription.getUrl());
        if(reference == null){
            reference = Reference.builder()
                    .url(refDescription.getUrl())
                    .rating(1)
                    .build();

            reference = refRepo.saveAndFlush(reference);
        }
        else{
            reference.setRating(reference.getRating() + 1);
            refRepo.saveAndFlush(reference);
        }


        refDescription.setReference(reference);
        refDescription.setUidUser(userId);
        refDescription.setUidAdditionMethod(uidAdditionMethod);
        refDescription.setAdditionDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        if(refDescription.getName()== null || refDescription.getName().equals(""))
            refDescription.setName(refDescription.getUrl());

        return refDescriptionRepo.save(refDescription);
    }

    /**
     *
     * @param refId id of reference
     * @param reference reference
     * @return reference
     */
    public RefDescription updateReference(Long refId, RefDescription reference) {
        log.info("Получена ссылка на обновление\n refId - {}, \n Ссылка - {}", reference.getUid(), reference.toString());

        RefDescription item = checkIfReferenceExists(refId);

        BeanUtils.copyProperties(
                reference,
                item,
                PropertyChecker.getNullPropertyNames(reference)
        );

        refDescriptionRepo.save(item);

        return item;
    }

    public RefDescription deleteReference(Long refId) {
        log.info("Получен запрос на удаление ссылки\n refId- {}", refId);

        RefDescription item = checkIfReferenceExists(refId);

        Reference reference = refRepo.findByUrl(item.getUrl());
        assertNotNull(reference, "Отсутствует ссылка");

        reference.setRating(reference.getRating() - 1);
        refRepo.saveAndFlush(reference);

        refDescriptionRepo.delete(item);

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

    private RefDescription checkIfReferenceExists(Long refId) {
        RefDescription data = refDescriptionRepo.findByUid(refId);
        assertNotNull(data, String.format("Указана несуществующая ссылка, refId - %s", refId));

        return data;
    }
}
