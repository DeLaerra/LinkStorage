package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.commons.utils.PropertyChecker;
import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.ReferenceDescriptionRepo;
import com.innopolis.referencestorage.repos.ReferenceRepo;
import com.innopolis.referencestorage.repos.UserRepo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Slf4j
@NoArgsConstructor
@Service
public class ReferenceService {
    private ReferenceDescriptionRepo referenceDescriptionRepo;
    private ReferenceRepo referenceRepo;
    private UserRepo userRepo;

    private final Short uidAdditionMethod = 0;

    @Autowired
    public ReferenceService(ReferenceRepo referenceRepo, UserRepo userRepo, ReferenceDescriptionRepo referenceDescriptionRepo) {
        this.referenceRepo = referenceRepo;
        this.userRepo = userRepo;
        this.referenceDescriptionRepo = referenceDescriptionRepo;
    }

    public List<ReferenceDescription> loadAllUserRefs(User author) {
        return referenceDescriptionRepo.findByUidUser(author.getUid());
    }

    public Page<ReferenceDescription> loadRefsByUserUid(User author, Pageable pageable) {
        return referenceDescriptionRepo.findByUidUser(author.getUid(), pageable);
    }

    /**
     * @param userId               - for which user reference
     * @param referenceDescription - reference
     * @return reference
     */
    public ReferenceDescription addReference(Long userId, ReferenceDescription referenceDescription, String urlText) {
        log.info("Получена ссылка на добавление\n userId- {}, \n reference - {}", userId, referenceDescription.toString());
        User user = checkIfUserExists(userId);

        assertNotNull(urlText, "Отсутствует текст url ссылки");

        URL url = null;
        try {
            url = new URL(urlText);
        } catch (MalformedURLException e) {
            //TODO кастомное исключение!
            log.error("В тексте {} нет ссылки!", urlText, e);
        }

        Reference reference = referenceRepo.findByUrl(urlText);
        if (reference == null) {
            reference = new Reference(urlText, 1);

            reference = referenceRepo.saveAndFlush(reference);
        } else {
            reference.setRating(reference.getRating() + 1);
            referenceRepo.saveAndFlush(reference);
        }

        ReferenceDescription existingReference = referenceDescriptionRepo.findAnyByUidUserAndReference(userId, reference);
        if (existingReference != null)
            assertNotNull(null, "Описание для ссылки уже существует");

        referenceDescription.setReference(reference);
        referenceDescription.setUidUser(userId);
        referenceDescription.setUidAdditionMethod(uidAdditionMethod);
        referenceDescription.setAdditionDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        Optional.ofNullable(url.getHost()).ifPresent(referenceDescription::setSource);

        if (referenceDescription.getName() == null || referenceDescription.getName().equals(""))
            referenceDescription.setName(url.toString());

        return referenceDescriptionRepo.save(referenceDescription);
    }

    /**
     * @param refId                id of reference
     * @param referenceDescription reference
     * @return reference
     */
    public ReferenceDescription updateReference(Long refId, ReferenceDescription referenceDescription, String url) {
        log.info("Получена ссылка на обновление\n refId - {}, \n Ссылка - {}", referenceDescription.getUid(), referenceDescription.toString());

        ReferenceDescription item = checkIfReferenceExists(refId);
        assertNotNull(url, "Отсутствует url ссылки");

        Reference reference = referenceRepo.findByUrl(url);
        if (reference == null) {
            reference = new Reference(url, 0);

            reference = referenceRepo.saveAndFlush(reference);
        }
        if (!reference.getUrl().equals(item.getReference().getUrl())) {
            Reference oldReference = item.getReference();
            oldReference.setRating(oldReference.getRating() - 1);
            reference.setRating(reference.getRating() + 1);
            reference = referenceRepo.saveAndFlush(reference);
        }

        BeanUtils.copyProperties(
                referenceDescription,
                item,
                PropertyChecker.getNullPropertyNames(referenceDescription)
        );
        item.setReference(reference);
        referenceDescriptionRepo.save(item);

        return item;
    }

    public ReferenceDescription deleteReference(Long refId) {
        log.info("Получен запрос на удаление ссылки\n refId- {}", refId);

        ReferenceDescription item = checkIfReferenceExists(refId);

        Reference reference = referenceRepo.findByUid(item.getReference().getUid());
        assertNotNull(reference, "Отсутствует ссылка");

        reference.setRating(reference.getRating() - 1);

        referenceRepo.saveAndFlush(reference);
        referenceDescriptionRepo.delete(item);

        if (reference.getRating() == 0) {
            referenceRepo.delete(reference);
        }

        return item;
    }

    public void copyReference(Long refDescriptionUid, User user, ReferenceDescription referenceDescription) {
        log.info("Получен запрос от пользователя с uid {} на копирование ссылки\n refId- {}", user.getUid(), refDescriptionUid);

        ReferenceDescription sourceRef = checkIfReferenceExists(refDescriptionUid);
        Reference reference = sourceRef.getReference();

        if (referenceDescriptionRepo.findAnyByUidUserAndReference(user.getUid(), reference) != null) {
            log.error("Описание для ссылки уже существует в Home пользователя с uid " + user.getUid());
            //TODO сменить на кастомное
            throw new IllegalArgumentException("Описание для ссылки уже существует в Home пользователя с uid " + user.getUid());
        }

        reference.setRating(reference.getRating() + 1);
        referenceRepo.saveAndFlush(reference);

        referenceDescription.setReference(sourceRef.getReference());
        referenceDescription.setUidUser(user.getUid());
        referenceDescription.setName(sourceRef.getName());
        Optional.ofNullable(sourceRef.getDescription()).ifPresent(referenceDescription::setDescription);
        referenceDescription.setUidReferenceType(sourceRef.getUidReferenceType());
        referenceDescription.setAdditionDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        referenceDescription.setSource(sourceRef.getSource());
        referenceDescription.setUidAdditionMethod(uidAdditionMethod);
        referenceDescription.setUidAccessLevel(0);

        referenceDescriptionRepo.save(referenceDescription);
    }


    /**
     * Проверяем, существует-ли запрашиваемый пользователь
     */
    private User checkIfUserExists(Long userId) {
        User user = userRepo.findByUid(userId);
        assertNotNull(user, String.format("Указан несуществующий userId - %s", userId));
        return user;
    }

    private ReferenceDescription checkIfReferenceExists(Long refId) {
        ReferenceDescription data = referenceDescriptionRepo.findByUid(refId);
        assertNotNull(data, String.format("Указана несуществующая ссылка, refId - %s", refId));

        return data;
    }

}
