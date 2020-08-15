package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.commons.utils.PropertyChecker;
import com.innopolis.referencestorage.domain.*;
import com.innopolis.referencestorage.repos.*;
import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.enums.AccessLevel;
import com.innopolis.referencestorage.enums.AdditionMethod;
import com.innopolis.referencestorage.enums.ReferenceType;
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
import org.springframework.ui.Model;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;
import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Slf4j
@NoArgsConstructor
@Service
public class ReferenceService {
    private ReferenceDescriptionRepo referenceDescriptionRepo;
    private ReferenceRepo referenceRepo;
    private UserRepo userRepo;
    private TagsRepo tagsRepo;
    private TagsRefsRepo tagsRefsRepo;

    @Autowired
    public ReferenceService(ReferenceRepo referenceRepo, UserRepo userRepo, ReferenceDescriptionRepo referenceDescriptionRepo,
                            TagsRepo tagsRepo, TagsRefsRepo tagsRefsRepo) {
        this.referenceRepo = referenceRepo;
        this.userRepo = userRepo;
        this.referenceDescriptionRepo = referenceDescriptionRepo;
        this.tagsRepo = tagsRepo;
        this.tagsRefsRepo = tagsRefsRepo;
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
    public void addReference(Long userId, ReferenceDescription referenceDescription, String urlText, String tags, Model model) {
        log.info("Получена ссылка на добавление\n userId- {}, \n reference - {}", userId, referenceDescription.toString());
        User user = checkIfUserExists(userId);

        assertNotNull(urlText, "Отсутствует текст url ссылки");

        URL url = null;
        try {
            url = new URL(urlText);
        } catch (MalformedURLException e) {
            log.error("В тексте {} нет ссылки!", urlText, e);
        }

        Reference reference = checkIfReferenceExists(urlText);

        Set<String> allTags = processUserTags(tags);
        allTags.addAll(processReferenceTags(referenceDescription.getName(), url));
        // сохраняем теги итерацией по сету - надо сохранить в две таблицы tags (только name, uid автоматически)
        // и tagsRefs (зависимость от tags и ref_description)

        ReferenceDescription existingReference = referenceDescriptionRepo.findAnyByUidUserAndReference(userId, reference);
        if ((existingReference != null) && userId != 0) {
            log.error("Описание для ссылки уже существует в Home пользователя с uid " + user.getUid());
            Optional.ofNullable(model).ifPresent(m -> m.addAttribute("copyRefError", "Эта ссылка уже есть в Home пользователя!"));
            return;
        }

        referenceDescription.setReference(reference);
        referenceDescription.setUidUser(userId);
        referenceDescription.setUidAdditionMethod(AdditionMethod.SITE.getAdditionMethodUid());
        referenceDescription.setAdditionDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        Optional.ofNullable(url.getHost()).ifPresent(referenceDescription::setSource);

        if (referenceDescription.getName() == null || referenceDescription.getName().equals(""))
            referenceDescription.setName(url.toString());

        referenceDescriptionRepo.save(referenceDescription);

        for (String tag : allTags) {
            Tags tagsToSave = new Tags();
            tagsToSave.setName(tag);
            tagsRepo.save(tagsToSave);
            TagsRefs tagsRefsToSave = new TagsRefs();
            tagsRefsToSave.setUidRefDescription(referenceDescription.getUid());
            tagsRefsToSave.setUidTag(tagsToSave.getUid());
            tagsRefsRepo.save(tagsRefsToSave);
        }
    }

    private Set<String> processReferenceTags(String name, URL url) {
        Set<String> processedTags = new HashSet<>();
        // анализ поля "название"
        if (!name.isEmpty()) {
            name = name.replaceAll("[^a-zA-Zа-яА-Я0-9 ]", ""); // оставляем только нужные символы
            name = " " + name;
            while (name.contains(" ")) {
                name = name.substring(name.indexOf(" ") + 1); // отрезаем часть с передним словом
                String word = name.replaceAll("\\ .*","");
                if (!word.equals("")) {
                    processedTags.add("#" + word);
                }
                if (name.contains(" ")) { // нужно на случай если у нас последний цикл и уже нет #
                    name = name.substring(name.indexOf(" ")); // отрезаем название переднего слова (оставшееся пойдет в след круг цикла)
                } else {
                    break;
                }
            }
        }
        // анализ поля "ссылка"
        String authority = url.getAuthority().replaceFirst("www.", ""); // добавляем название сайта, например youtube
        if (authority.contains(".")) {
            processedTags.add("#" + authority.substring(0, authority.lastIndexOf(".")));
        } else {
            processedTags.add("#" + authority);
        }
        String path = url.getPath();
        path = path.replaceAll("[^a-zA-Zа-яА-Я0-9/ ]", "");
        while (path.contains("/")) {
            path = path.substring(path.indexOf("/") + 1); // отрезаем часть с передним словом
            // вытаскивание слова перед следующем слэше в пути запроса
            String pathTag = path.replaceAll("\\/.*","");
            if (pathTag.length() > 3 && pathTag.length() <= 20) {
                processedTags.add("#" + pathTag);
            }
            if (path.contains("/")) { // нужно на случай если последний цикл и уже нет слэша
                path = path.substring(path.indexOf("/")); // убираем переднее слово (оставшееся пойдет в след круг цикла)
            } else {
                break;
            }
        }
        return processedTags;
    }

    private Set<String> processUserTags(String tags) {
        Set<String> processedTags = new HashSet<>();
        tags = tags.replaceAll("[^a-zA-Zа-яА-Я0-9#]", ""); // оставляем только нужные символы
        if (tags.endsWith("#")) { // если последний тег был пустой
            tags = tags.substring(0, tags.length() - 1);
        }
        while (tags.contains("#")) {
            tags = tags.substring(tags.indexOf("#") + 1); // отрезаем часть с передним тегом
            String tagName = tags.replaceAll("\\#.*","");
            if (!tagName.equals("")) {
                processedTags.add("#" + tagName);
            }
            if (tags.contains("#")) { // нужно на случай если у нас последний цикл и уже нет #
                tags = tags.substring(tags.indexOf("#")); // убираем передний тег (оставшееся пойдет в след круг цикла)
            }
        }
        return processedTags;
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
        // обработка тегов - удаляем те, которых больше нет, прибавляем те, что появились, не трогаем те, что есть и там и там
        Set<Tags> originalTags = item.getTag();
        Set<String> originalTagsString = new HashSet<>();
        for (Tags tag : originalTags) {
            originalTagsString.add(tag.getName());
        }
        Set<String> userInputTags = processUserTags(referenceDescription.getTags());
        if (!originalTagsString.equals(userInputTags)) {
            // обработка тегов
            Set<String> tagsToDelete = elementsThatAreInTheFirstHashSetAndNotInTheSecond(originalTagsString, userInputTags);
            Set<String> tagsToAdd = elementsThatAreInTheFirstHashSetAndNotInTheSecond(userInputTags, originalTagsString);
            System.out.println("fuck off");
            for (String tag : tagsToDelete) {
                Tags tagToDelete = originalTags
                        .stream()
                        .filter(tags -> tags.getName().equals(tag))
                        .findFirst()
                        .get();
                TagsRefs tagsRefToDelete = tagsRefsRepo.findFirstByUidRefDescriptionAndUidTag(item.getUid(), tagToDelete.getUid());
                tagsRefsRepo.deleteByUid(tagsRefToDelete.getUid());
                tagsRepo.deleteByUid(tagToDelete.getUid()); // похоже из за очередности исполнения операций не удаляется
                // TODO нужно позже исправить! чтобы не было засорения таблицы refs (repo.delete не помогает)
                // при этом там где происходит удаление ссылки, проблемы нет
            }
            for (String tag : tagsToAdd) {
                Tags tagsToSave = new Tags();
                tagsToSave.setName(tag);
                tagsRepo.save(tagsToSave);
                TagsRefs tagsRefsToSave = new TagsRefs();
                tagsRefsToSave.setUidRefDescription(item.getUid());
                tagsRefsToSave.setUidTag(tagsToSave.getUid());
                tagsRefsRepo.save(tagsRefsToSave);
            }
        }
        return item;
    }

    private Set<String> elementsThatAreInTheFirstHashSetAndNotInTheSecond(Set<String> firstHashSet, Set<String> secondHashSet) {
        Set<String> result = new HashSet<>();
        for (String element : firstHashSet) {
            if (!secondHashSet.contains(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public ReferenceDescription deleteReference(Long refId) {
        log.info("Получен запрос на удаление ссылки\n refId- {}", refId);

        ReferenceDescription item = checkIfReferenceExists(refId);

        Reference reference = referenceRepo.findByUid(item.getReference().getUid());
        assertNotNull(reference, "Отсутствует ссылка");

        reference.setRating(reference.getRating() - 1);

        // найти все связанные с этим referenceDescription теги в двух таблицах и удалить
        Set<TagsRefs> tagsRefsToDelete = tagsRefsRepo.findAllByUidRefDescription(item.getUid());
        Set<Tags> tagsToDelete = new HashSet<>();
        for (TagsRefs tagsRefs : tagsRefsToDelete) {
            tagsToDelete.add(tagsRepo.findByUid(tagsRefs.getUidTag()));
        }
        tagsRefsRepo.deleteAllByUidRefDescription(item.getUid());
        for (Tags tags : tagsToDelete) {
            tagsRepo.delete(tags);
        }

        referenceRepo.saveAndFlush(reference);
        referenceDescriptionRepo.delete(item);

        if (reference.getRating() == 0) {
            referenceRepo.delete(reference);
        }

        return item;
    }

    public void copyReference(Long refDescriptionUid, User user, ReferenceDescription referenceDescription, Model model) {
        log.info("Получен запрос от пользователя с uid {} на копирование ссылки\n refId- {}", user.getUid(), refDescriptionUid);

        ReferenceDescription sourceRef = checkIfReferenceExists(refDescriptionUid);
        Reference reference = sourceRef.getReference();

        if (referenceDescriptionRepo.findAnyByUidUserAndReference(user.getUid(), reference) != null) {
            log.error("Описание для ссылки уже существует в Home пользователя с uid " + user.getUid());
            model.addAttribute("copyRefError", "Эта ссылка уже есть в Home пользователя!");
            return;
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
        referenceDescription.setUidAdditionMethod(AdditionMethod.SITE.getAdditionMethodUid());
        referenceDescription.setUidAccessLevel(AccessLevel.PUBLIC.getAccessLevelUid());

        referenceDescriptionRepo.save(referenceDescription);

        // копирование тегов
        Set<TagsRefs> sourceTagsRefs = tagsRefsRepo.findAllByUidRefDescription(sourceRef.getUid());
        if (!sourceTagsRefs.isEmpty()) {
            for (TagsRefs tagsRefs : sourceTagsRefs) {
                String tagNameToCopy = tagsRepo.findByUid(tagsRefs.getUidTag()).getName();
                Tags tagsToCopy = new Tags();
                tagsToCopy.setName(tagNameToCopy);
                tagsRepo.save(tagsToCopy);
                TagsRefs tagsRefsToCopy = new TagsRefs();
                tagsRefsToCopy.setUidRefDescription(referenceDescription.getUid());
                tagsRefsToCopy.setUidTag(tagsToCopy.getUid());
                tagsRefsRepo.save(tagsRefsToCopy);
            }
        }
    }

    public ReferenceDescription addReferenceFromTelegram(URL url) {
        log.info("Получена ссылка из Telegram на добавление: \n reference - {}", url.toString());

        Long userId = 0L;
        User user = checkIfUserExists(userId);
        Reference reference = checkIfReferenceExists(url.toString());

        ReferenceDescription referenceDescription = ReferenceDescription.builder()
                .reference(reference)
                .uidUser(userId)
                .uidAdditionMethod(AdditionMethod.TELEGRAM.getAdditionMethodUid())
                .additionDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .source(url.getHost())
                .name(url.toString())
                .uidAccessLevel(AccessLevel.PUBLIC.getAccessLevelUid())
                .uidReferenceType(ReferenceType.TEXT.getRefTypeUid())
                .build();

        referenceDescriptionRepo.save(referenceDescription);
        return referenceDescription;
    }

    private Reference checkIfReferenceExists(String urlText) {
        Reference reference = referenceRepo.findByUrl(urlText);
        if (reference == null) {
            reference = new Reference(urlText, 1);

            reference = referenceRepo.saveAndFlush(reference);
        } else {
            reference.setRating(reference.getRating() + 1);
            referenceRepo.saveAndFlush(reference);
        }
        return reference;
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
    public boolean checkIfReferenceAlreadyExists(Long refDescrId, ReferenceDescription referenceDescription) {
        if (referenceDescription.getUidUser() == referenceDescriptionRepo.findByUid(refDescrId).getUidUser()) {
            return true;
        } else return false;
    }
}
