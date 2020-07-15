package com.innopolis.referencestorage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innopolis.referencestorage.commons.utils.PropertyChecker;
import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.ReferenceRepo;
import com.innopolis.referencestorage.repos.UserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Slf4j
@AllArgsConstructor
@Service
public class ReferenceService {
    @Autowired
    private ReferenceRepo referenceRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ObjectMapper objectMapper;


    public List<Reference> loadAllUserRefs(User author) {
        return referenceRepo.findByUidUser(author.getUid());
    }

    public Page<Reference> loadSortedUserRefs(User author, Pageable pageable) {
        return referenceRepo.findByUidUser(author.getUid(), pageable);
    }

    /**
     *
     * @param userId - for which user reference
     * @param detail - reference json
     * @return json with id or exception
     */
    public JsonNode addReference(Long userId, JsonNode detail) throws JsonProcessingException {
        log.info("Получена сущность на добавление\n ид пользователя- {}, \n сущность - {}", userId, detail);
        User user = checkIfUserExists(userId);

        Reference item = convertItemToReference(detail, user);

        referenceRepo.save(item);

        return objectMapper.valueToTree(item);
    }

    public JsonNode getRef(Long refId) {
        log.info("Получена ссыдка на отправку \n ид - {}", refId);

        Reference item = referenceRepo.findByUid(refId);

        return objectMapper.valueToTree(item);
    }

    public JsonNode updateRef(Long refId, JsonNode detail) throws JsonProcessingException {
        log.info("Получена ссылка на обновление\n ид - {}, \n Ссылка - {}", refId, detail);

        Reference item = checkIfReferenceExists(refId);

        Reference detailItem = objectMapper.treeToValue(detail, Reference.class);

        BeanUtils.copyProperties(
                detailItem,
                item,
                PropertyChecker.getNullPropertyNames(detailItem)
        );

        referenceRepo.save(item);

        return objectMapper.valueToTree(item);
    }

    public JsonNode deleteRef(Long refId) {
        log.info("Получен запрос на удаление ссылки'\n Ид элемента- {}", refId);

        Reference item = checkIfReferenceExists(refId);

        referenceRepo.delete(item);

        return objectMapper.valueToTree(item);
    }

    /**
     * Проверяем, существует-ли запрашиваемый пользователь
     * */
    private User checkIfUserExists(Long userId) {
        User user = userRepo.findByUid(userId);
        assertNotNull(user, String.format("Указан не существующий идентификатор пользователя - %s", userId));
        return user;
    }

    private Reference checkIfReferenceExists(Long refId) {
        Reference data = referenceRepo.findByUid(refId);
        assertNotNull(data, String.format("Указана не существующая ссылка, ид - %s", refId));

        return data;
    }

    /**
     * Преобразуем документ из *detail формата в поджо,
     * для оохранения в бд
     * */
    private Reference convertItemToReference(JsonNode detail, User user) throws JsonProcessingException {

        Reference dataItem = objectMapper.treeToValue(detail, Reference.class);
        dataItem.setUidUser(user.getUid());

        return dataItem;
    }
}
