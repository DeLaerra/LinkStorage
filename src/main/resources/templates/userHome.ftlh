<#import "parts/common.ftlh" as c>
<#import "parts/pager.ftlh" as p>
<#include "parts/security.ftlh">

<@c.page>
    <div class="container">
        <div class="row">
            <div class="col">
                <form name="searchFriends" action="/userHome" method="get">
                    <div class="input-group">
                        <input class="form-control" type="search" name="searchFriends" id="searchFriends"
                               placeholder="Поиск...">
                        <span class="input-group-append">
                                        <button type="submit" title="Найти друга" class="btn btn-outline-secondary">
                                            <span class="iconify" data-icon="ic:round-person-search"
                                                  data-inline="false"></span>
                                        </button>
                                    </span>
                    </div>
                </form>
                <#if searchFriends??>
                    <#list searchFriends as searchFriend>
                        <a href="../friend/${searchFriend.uid}">
                            <div class="modal-content">
                                <table>
                                    <tr>
                                        <td>
                                            <#if searchFriend.userInfo.avatar??>
                                                <img alt="My image" src="${searchFriend.userInfo.getAvatarBase64()}"
                                                     style="width: 50px; height: 50px; display: block; margin: auto; border-radius: 100px;"/>
                                            </#if>
                                        </td>
                                    </tr>
                                    <#if searchFriend.userInfo.getName() != "Name">
                                        <tr>
                                            <td align="center">
                                                ${searchFriend.userInfo.getName()} ${searchFriend.userInfo.getSurname()}
                                            </td>
                                        </tr>
                                    </#if>
                                    <tr>
                                        <td align="center">
                                            ${searchFriend.username}
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </a>
                    </#list>
                </#if>
                <#if listFriends??>
                    <h5 xmlns="http://www.w3.org/1999/html" align="center">Друзья</h5>
                    <#list listFriends as friend>
                        <a href="../friend/${friend.uid}">
                            <div class="modal-content">
                                <table>
                                    <tr>
                                        <td>
                                            <#if friend.userInfo.avatar??>
                                                <img alt="My image" src="${friend.userInfo.getAvatarBase64()}"
                                                     style="width: 50px; height: 50px; display: block; margin: auto; border-radius: 100px;"/>
                                            </#if>
                                        </td>
                                    </tr>
                                    <#if friend.userInfo.getName() != "Name">
                                        <tr>
                                            <td align="center">
                                                ${friend.userInfo.getName()} ${friend.userInfo.getSurname()}
                                            </td>
                                        </tr>
                                    </#if>
                                    <tr>
                                        <td align="center">
                                            ${friend.username}
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </a>
                    </#list>
                </#if>
            </div>
            <div class="col-10">
                <form name="search" action="/searchResult" method="GET">
                    <div class="row">
                        <div class="col-sm">
                            <div class="input-group">
                                <input class="form-control" type="search" name="search" id="search"
                                       placeholder="Поиск ссылок...">
                                <span class="input-group-append">
                                        <button type="submit" title="Найти ссылку" class="btn btn-outline-secondary">
                                            <span class="iconify" data-icon="mdi:search-web" data-inline="false"></span>
                                        </button>
                                    </span>
                            </div>
                            <div class="form-group form-check">
                                <input type="checkbox" class="form-check-input" id="area" name="area" value="all">
                                <label class="form-check-label" for="area">искать по ссылкам всех пользователей</label>
                            </div>
                        </div>
                    </div>
                </form>
                <#if myTags??>
                    <div class="row">
                        <div class="col-12">
                            <#list mySearchTags as myTag>
                                <a href="../searchResult?search=%23${myTag}&area=all" class="btn btn-sm btn-link"
                                   target="_blank">#${myTag}</a>
                            </#list>
                        </div>
                    </div>
                </#if>
                <p></p>
                <div class="row">
                    <div class="col-sm">
                        <!-- Button trigger modal Add Добавить...-->
                        <button type="button" title="Добавить ссылку" class="btn btn-success btn-sm" data-toggle="modal"
                                data-target="#add_user_form">
                            Добавить новую ссылку
                        </button>
                        <#if refAddError??>
                            <div class="invalid-feedback">
                                ${refAddError}
                            </div>
                        </#if>
                        <!-- Modal Add-->
                        <div class="modal fade" id="add_user_form" tabindex="-1" role="dialog"
                             aria-labelledby="add_Label" aria-hidden="true">
                            <div class="modal-dialog" role="document">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <b>Добавление ссылки</b>
                                        <button type="button" class="close" title="Закрыть" data-dismiss="modal"
                                                aria-label="Close">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                    </div>
                                    <div class="modal-body">
                                        <form method="post" name="userRef" action="reference/add/${user.uid}">
                                            <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                                            <div class="form-group">
                                                <label for="add_nameInput">Название:</label>
                                                <input type="text" name="name" class="form-control" id="add_nameInput"
                                                       placeholder="Название">
                                            </div>
                                            <div class="form-group">
                                                <label for="add_urlInput">Ссылка:</label>
                                                <input type="url" name="url" required class="form-control"
                                                       id="add_urlInput"
                                                       placeholder="Ссылка">
                                            </div>
                                            <div class="form-group">
                                                <label for="add_descriptionInput">Описание:</label>
                                                <input type="text" name="description" class="form-control"
                                                       id="add_descriptionInput"
                                                       placeholder="Описание">
                                            </div>
                                            <div class="form-group">
                                                <label for="add_refTypeInput">Тип:</label>
                                                <select class="form-control" name="uidReferenceType"
                                                        id="add_refTypeInput">
                                                    <option value="0" selected>текст</option>
                                                    <option value="1">видео</option>
                                                    <option value="2">файл</option>
                                                    <option value="3">картинка</option>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label for="add_tagInput">Теги:</label>
                                                <input type="text" name="tagsFromUser" class="form-control"
                                                       id="add_tagInput"
                                                       placeholder="Тэг">
                                            </div>
                                            <div class="form-group">
                                                <label for="add_accessLevelInput">Уровень доступа:</label>
                                                <select class="form-control" name="uidAccessLevel"
                                                        id="add_accessLevelInput">
                                                    <option value="0" selected>публичная</option>
                                                    <option value="1">личная</option>
                                                </select>
                                            </div>
                                            <input type="submit" class="btn btn-success btn-sm" value="Добавить">
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Button trigger modal SendToFriend Отправить другу...-->
                        <button type="button" title="Отправить ссылку" class="btn btn-info btn-sm"
                                data-toggle="modal"
                                data-target="#send_user_form">
                            Отправить новую ссылку другу
                        </button>

                        <!-- Modal SendToFriend -->
                        <div class="modal fade" id="send_user_form" tabindex="-1" role="dialog"
                             aria-labelledby="add_Label" aria-hidden="true">
                            <div class="modal-dialog" role="document">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <b>Отправить ссылку</b>
                                        <button type="button" class="close" title="Закрыть" data-dismiss="modal"
                                                aria-label="Close">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                    </div>
                                    <div class="modal-body">
                                        <form method="post" name="userRef" action="reference/send-new">
                                            <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                                            <div class="form-group">
                                                <label for="${user.uid}ReceiverInput">Кому:</label>
                                                <select class="form-control"
                                                        name="receiver"
                                                        id="${user.uid}receiver">
                                                    <#if listFriends??>
                                                        <#list listFriends as friend>
                                                            <option value="${friend.username}"
                                                            <a href="../friend?idFriend=${friend.uid}">
                                                                ${friend.username}
                                                            </a>
                                                            </option>
                                                        </#list>
                                                    </#if>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label for="${user.uid}descriptionInput">Комментарий:</label>
                                                <input type="text" name="text"
                                                       class="form-control"
                                                       id="${user.uid}textInput"
                                                       placeholder="Комментарий">
                                            </div>
                                            <div class="form-group">
                                                <label for="add_nameInput">Название:</label>
                                                <input type="text" name="name" class="form-control"
                                                       id="add_nameInput"
                                                       placeholder="Название">
                                            </div>
                                            <div class="form-group">
                                                <label for="add_urlInput">Ссылка:</label>
                                                <input type="url" name="url" class="form-control"
                                                       id="add_urlInput"
                                                       placeholder="Ссылка">
                                            </div>
                                            <div class="form-group">
                                                <label for="add_refTypeInput">Тип:</label>
                                                <select class="form-control" name="uidReferenceType"
                                                        id="add_refTypeInput">
                                                    <option value="0" selected>текст</option>
                                                    <option value="1">видео</option>
                                                    <option value="2">файл</option>
                                                    <option value="3">картинка</option>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label for="add_tagInput">Теги:</label>
                                                <input type="text" name="tagsFromUser" class="form-control"
                                                       id="add_tagInput"
                                                       placeholder="Тэг">
                                            </div>
                                            <input type="submit" class="btn btn-info btn-sm" value="Отправить">
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <#if pmDuplicateError??>
                    <br>
                    <div class="alert alert-danger">
                        <strong>Не отправлено!</strong> Отправка сообщения c повторной ссылкой тому же адресату
                        запрещена!
                    </div>
                </#if>
                <#if copyRefError??>
                    <br>
                    <div class="alert alert-danger">
                        <strong>Не добавлено!</strong> Эта ссылка уже есть в Home пользователя!
                    </div>
                </#if>
                <#if page??>
                    <#if page.content?size !=0 >
                    <#--                    <div class="container">-->
                        <table class="table table-striped">
                            <thead>
                            <tr>
                                <#--                Шапка таблицы-->
                                <div class="row">
                                    <form name="sorting" action="/userHome" method="GET">
                                        <div class="col-sm">
                                            <th scope="col">
                                                <button class="btn btn-light btn-sm" title="Сортировать по-убыванию"
                                                        type="submit" name="sortBy"
                                                        value="nameDesc">
                                                    <span class="iconify" data-icon="gg:sort-az"
                                                          data-inline="false"></span>
                                                </button>
                                                <button class="btn btn-light btn-sm" title="Сортировать по-возрастанию"
                                                        type="submit" name="sortBy"
                                                        value="nameAsc">
                                                    <span class="iconify" data-icon="gg:sort-za"
                                                          data-inline="false"></span>
                                                </button>
                                                Имя
                                            </th>
                                        </div>
                                        <div class="col-sm">
                                            <th scope="col">
                                                <button class="btn btn-light btn-sm" title="Сортировать по-убыванию"
                                                        type="submit" name="sortBy"
                                                        value="sourceDesc">
                                                    <span class="iconify" data-icon="gg:sort-az"
                                                          data-inline="false"></span>
                                                </button>
                                                <button class="btn btn-light btn-sm" title="Сортировать по-возрастанию"
                                                        type="submit" name="sortBy"
                                                        value="sourceAsc">
                                                    <span class="iconify" data-icon="gg:sort-za"
                                                          data-inline="false"></span>
                                                </button>
                                                Источник
                                            </th>
                                        </div>
                                        <div class="col-sm">
                                            <th scope="col">
                                                <button class="btn btn-light btn-sm" title="Сортировать по-убыванию"
                                                        type="submit" name="sortBy"
                                                        value="ratingDesc">
                                                    <span class="iconify" data-icon="gg:sort-az"
                                                          data-inline="false"></span>
                                                </button>
                                                <button class="btn btn-light btn-sm" title="Сортировать по-возрастанию"
                                                        type="submit" name="sortBy"
                                                        value="ratingAsc">
                                                    <span class="iconify" data-icon="gg:sort-za"
                                                          data-inline="false"></span>
                                                </button>
                                                Рейтинг
                                            </th>
                                        </div>
                                    </form>
                                </div>
                            </tr>
                            </thead>
                            <#list page.content as userRef>
                                <tr>
                                    <#--                            Строки таблицы-->
                                    <div class="row">
                                        <div class="col-sm">
                                            <td> <#if userRef.name??><a
                                                    href="${userRef.reference.url}">${userRef.name}</a>
                                                    <br><#else><a
                                                    href="${userRef.reference.url}">${userRef.reference.url}<br>
                                                    </a></#if>
                                                <#if userRef.tag??>
                                                    Теги: <#list userRef.tag as userRefTag><a class="text-"
                                                                                              href="../searchResult?search=${userRefTag.name}">${userRefTag.name} </a></#list>
                                                    <br>
                                                </#if>

                                                <div class="btn-toolbar mr-4" role="toolbar"
                                                     aria-label="Toolbar with button groups">

                                                    <!-- Button trigger modal Edit Изменить...-->
                                                    <div class="btn-group mr-2" role="group" aria-label="Edit">
                                                        <button type="submit" class="btn btn-success btn-sm"
                                                                title="Изменить ссылку"
                                                                data-toggle="modal"
                                                                data-target="#edit_${userRef.uid}">
                                                                    <span class="iconify"
                                                                          data-icon="mdi:circle-edit-outline"
                                                                          data-inline="false"></span>
                                                        </button>
                                                    </div>

                                                    <!-- Button Delete Удалить...-->
                                                    <div class="btn-group mr-2" role="group" aria-label="Delete">
                                                        <form method="get" action="reference/delete/${userRef.uid}">
                                                            <button class="btn btn btn-danger btn-sm" type="submit"
                                                                    name="Delete"
                                                                    title="Удалить ссылку"
                                                                    value="Удалить">
                                                                        <span class="iconify"
                                                                              data-icon="mdi:delete-circle-outline"
                                                                              data-inline="false"></span>
                                                            </button>
                                                        </form>
                                                    </div>

                                                    <!-- Button trigger modal Send Отправить...-->
                                                    <div class="btn-group mr-2" role="group" aria-label="Send">
                                                        <button type="submit" class="btn btn-info btn-sm"
                                                                title="Отправить ссылку другу"
                                                                data-toggle="modal"
                                                                data-target="#send_${userRef.uid}">
                                                                    <span class="iconify"
                                                                          data-icon="mdi:share-circle"
                                                                          data-inline="false"></span>
                                                        </button>
                                                    </div>

                                                    <!-- Button trigger collapse Properties Подробнее...-->
                                                    <div class="btn-group mr-2" role="group" aria-label="Properties">
                                                        <button type="button" class="btn btn-light btn-sm"
                                                                title="Подробнее"
                                                                data-toggle="collapse"
                                                                data-target="#${userRef.uid}">
                                                                    <span class="iconify"
                                                                          data-icon="ion:ellipsis-horizontal-circle"
                                                                          data-inline="false"></span>
                                                        </button>
                                                    </div>
                                                </div>
                                                <!-- Collapse Подробнее...-->
                                                <div class="collapse" id="${userRef.uid}" tabindex="-1" role="dialog"
                                                     aria-labelledby="${userRef.uid}Label" aria-hidden="true">
                                                    <div class="card card-body">
                                                        <div class="body">
                                                            <a href="${userRef.reference.url}">${userRef.reference.url}</a><br>
                                                            <#if userRef.tags??><b>Теги</b>: ${userRef.tags}<br></#if>
                                                            <#if userRef.source??><b>Источник</b>: <a
                                                                    href="${userRef.source}">${userRef.source}</a>
                                                                <br></#if>
                                                            <#if userRef.additionDate??><b>Дата
                                                                добавления</b>: ${userRef.additionDate}
                                                                <br></#if>
                                                            <#if userRef.description??>
                                                                <b>Описание: </b>${userRef.description}
                                                                <br></#if>
                                                            <b>Уровень
                                                                доступа: </b> <#if userRef.uidAccessLevel == 0>публичная
                                                                <br><#else>личная
                                                                <br></#if>
                                                            <b>Тип: </b>
                                                            <#if userRef.uidReferenceType == 0>текст
                                                                <br><#elseif userRef.uidReferenceType == 1>видео
                                                                <br>
                                                            <#elseif userRef.uidReferenceType == 2>файл
                                                                <br><#elseif userRef.uidReferenceType == 3>картинка
                                                                <br></#if>
                                                            <#if userRef.reference.rating??>
                                                                <b>Рейтинг: </b>${userRef.reference.rating}</#if>
                                                        </div>
                                                    </div>
                                                </div>
                                                <!-- Modal Edit-->
                                                <div class="modal fade" id="edit_${userRef.uid}" tabindex="-1"
                                                     role="dialog"
                                                     aria-labelledby="${userRef.uid}Label" aria-hidden="true">
                                                    <div class="modal-dialog" role="document">
                                                        <div class="modal-content">
                                                            <div class="modal-header text-truncate">
                                                                <h5 class="modal-title"
                                                                    id="${userRef.uid}Label"><#if userRef.name??><a
                                                                        href="${userRef.reference.url}">${userRef.name}</a>
                                                                        <br><#else><a
                                                                        href="${userRef.reference.url}">${userRef.reference.url}
                                                                        <br></a></#if></h5>
                                                                <button type="button" class="close" data-dismiss="modal"
                                                                        title="Закрыть"
                                                                        aria-label="Close">
                                                                    <span aria-hidden="true">&times;</span>
                                                                </button>
                                                            </div>
                                                            <div class="modal-body text-truncate">
                                                                <form method="post" name="userRef"
                                                                      action="reference/update/${userRef.uid}">
                                                                    <input type="hidden" name="_csrf"
                                                                           value="${_csrf.token}"/>
                                                                    <input type="hidden" name="uidUser"
                                                                           value="${userRef.uidUser}"/>
                                                                    <a href="${userRef.reference.url}">${userRef.reference.url}</a><br>
                                                                    <div class="form-group">
                                                                        <label for="${userRef.uid}nameInput">Название:</label>
                                                                        <input type="text" name="name" required
                                                                               class="form-control"
                                                                               id="${userRef.uid}nameInput"
                                                                               placeholder="Название"
                                                                               value="<#if userRef.name??>${userRef.name}</#if>">
                                                                    </div>
                                                                    <div class="form-group">
                                                                        <label for="${userRef.uid}urlInput">Ссылка:</label>
                                                                        <input type="text" name="url" required
                                                                               class="form-control"
                                                                               id="${userRef.uid}urlInput"
                                                                               placeholder="Ссылка"
                                                                               value="<#if userRef.reference.url??>${userRef.reference.url}</#if>">
                                                                    </div>
                                                                    <#if userRef.description??>
                                                                        <div class="form-group">
                                                                            <label for="${userRef.uid}descriptionInput">Описание:</label>
                                                                            <input type="text" name="description"
                                                                                   class="form-control"
                                                                                   id="${userRef.uid}descriptionInput"
                                                                                   placeholder="Описание"
                                                                                   value="<#if userRef.description??>${userRef.description}</#if>">
                                                                        </div>
                                                                    </#if>
                                                                    <div class="form-group">
                                                                        <label for="${userRef.uid}refTypeInput">Тип:</label>
                                                                        <select class="form-control"
                                                                                name="uidReferenceType"
                                                                                id="{userRef.uid}refTypeInput">
                                                                            <option value="0"
                                                                                    <#if userRef.uidReferenceType == 0>selected</#if>>
                                                                                текст
                                                                            </option>
                                                                            <option value="1"
                                                                                    <#if userRef.uidReferenceType == 1>selected</#if>>
                                                                                видео
                                                                            </option>
                                                                            <option value="2"
                                                                                    <#if userRef.uidReferenceType == 2>selected</#if>>
                                                                                файл
                                                                            </option>
                                                                            <option value="3"
                                                                                    <#if userRef.uidReferenceType == 3>selected</#if>>
                                                                                картинка
                                                                            </option>
                                                                        </select>
                                                                    </div>
                                                                    <#--<#if userRef.tags??>
                                                                        <div class="form-group">
                                                                            <label for="${userRef.uid}tagInput">Теги:</label>
                                                                            <input type="text" name="tag"
                                                                                   class="form-control"
                                                                                   id="${userRef.uid}tagInput"
                                                                                   placeholder="Тэг"
                                                                                   value="<#if userRef.tags??>${userRef.tags}</#if>">
                                                                        </div>
                                                                    </#if>-->
                                                                    <#if userRef.tag??>
                                                                        <div class="form-group">
                                                                            <label for="${userRef.uid}tagInput">Теги:</label>
                                                                            <input type="text" name="tags"
                                                                                   class="form-control"
                                                                                   id="${userRef.uid}tagInput"
                                                                                   placeholder="Тэг"
                                                                                   value="${userRef.tags}">
                                                                        </div>
                                                                    </#if>
                                                                    <div class="form-group">
                                                                        <label for="${userRef.uid}accessLevelInput">Уровень
                                                                            доступа:</label>
                                                                        <select class="form-control"
                                                                                name="uidAccessLevel"
                                                                                id="{userRef.uid}accessLevelInput">
                                                                            <option value="0"
                                                                                    <#if userRef.uidAccessLevel == 0>selected</#if>>
                                                                                публичная
                                                                            </option>
                                                                            <option value="1"
                                                                                    <#if userRef.uidAccessLevel == 1>selected</#if>>
                                                                                личная
                                                                            </option>
                                                                        </select>
                                                                    </div>
                                                                    <input type="submit" class="btn btn-success btn-sm"
                                                                           value="Сохранить">
                                                                </form>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Modal Send-->
                                                <div class="modal fade" id="send_${userRef.uid}" tabindex="-1"
                                                     role="dialog"
                                                     aria-labelledby="${userRef.uid}Label" aria-hidden="true">
                                                    <div class="modal-dialog" role="document">
                                                        <div class="modal-content">
                                                            <div class="modal-header">
                                                                <b>Отправить ссылку</b>
                                                                <button type="button" class="close" data-dismiss="modal"
                                                                        title="Закрыть"
                                                                        aria-label="Close">
                                                                    <span aria-hidden="true">&times;</span>
                                                                </button>
                                                            </div>
                                                            <div class="modal-body">
                                                                <form method="post" name="userRef"
                                                                      action="reference/send/${userRef.uid}">
                                                                    <input type="hidden" name="_csrf"
                                                                           value="${_csrf.token}"/>
                                                                    <div class="form-group">
                                                                        <label for="${userRef.uid}ReceiverInput">Кому:</label>
                                                                        <select class="form-control"
                                                                                name="receiver"
                                                                                id="${userRef.uid}receiver">
                                                                            <#if listFriends??>
                                                                                <#list listFriends as friend>
                                                                                    <option value="${friend.username}"
                                                                                    <a href="../friend?idFriend=${friend.uid}">
                                                                                        ${friend.username}
                                                                                    </a>
                                                                                    </option>
                                                                                </#list>
                                                                            </#if>
                                                                        </select>
                                                                    </div>
                                                                    <div class="form-group">
                                                                        <label for="${userRef.uid}descriptionInput">Комментарий:</label>
                                                                        <input type="text" name="text"
                                                                               class="form-control"
                                                                               id="${userRef.uid}textInput"
                                                                               placeholder="Комментарий">
                                                                    </div>
                                                                    <div class="form-group">
                                                                        <label for="${userRef.uid}nameInput">Название:</label>
                                                                        <input readonly type="text" name="name"
                                                                               class="form-control"
                                                                               id="${userRef.uid}nameInput"
                                                                               placeholder="Название"
                                                                               value="<#if userRef.name??>${userRef.name}</#if>">
                                                                    </div>
                                                                    <div class="form-group">
                                                                        <label for="${userRef.uid}urlInput">Ссылка:</label>
                                                                        <input readonly type="text" name="url"
                                                                               class="form-control"
                                                                               id="${userRef.uid}urlInput"
                                                                               placeholder="Ссылка"
                                                                               value="<#if userRef.reference.url??>${userRef.reference.url}</#if>">
                                                                    </div>
                                                                    <#if userRef.description??>
                                                                        <div class="form-group">
                                                                            <label for="${userRef.uid}descriptionInput">Описание:</label>
                                                                            <input readonly type="text"
                                                                                   name="description"
                                                                                   class="form-control"
                                                                                   id="${userRef.uid}descriptionInput"
                                                                                   value="<#if userRef.description??>${userRef.description}</#if>">
                                                                        </div>
                                                                    </#if>
                                                                    <div class="form-group">
                                                                        <label for="${userRef.uid}refTypeInput">Тип:</label>
                                                                        <input readonly type="text"
                                                                               name="uidReferenceType"
                                                                               class="form-control"
                                                                               id="${userRef.uid}refTypeInput"
                                                                               value="<#if userRef.uidReferenceType == 0>текст
                                                                                        <#elseif userRef.uidReferenceType == 1>видео
                                                                                        <#elseif userRef.uidReferenceType == 2>файл
                                                                                        <#elseif userRef.uidReferenceType == 3>картинка</#if>">
                                                                    </div>
                                                                    <#if userRef.tags??>
                                                                        <div class="form-group">
                                                                            <label for="${userRef.uid}tagInput">Теги:</label>
                                                                            <input readonly type="text" name="tag"
                                                                                   class="form-control"
                                                                                   id="${userRef.uid}tagInput"
                                                                                   value="<#if userRef.tags??>${userRef.tags}</#if>">
                                                                        </div>
                                                                    </#if>
                                                                    <div class="form-group">
                                                                        <label for="${userRef.uid}sourceInput">Источник:</label>
                                                                        <input readonly type="text" name="source"
                                                                               class="form-control"
                                                                               id="${userRef.uid}sourceInput"
                                                                               value="<#if userRef.source??>${userRef.source}</#if>">
                                                                    </div>
                                                                    <div class="form-group">
                                                                        <label for="${userRef.uid}accessLevelInput">Уровень
                                                                            доступа:</label>
                                                                        <input readonly type="text"
                                                                               name="uidAccessLevel"
                                                                               class="form-control"
                                                                               id="${userRef.uid}accessLevelInput"
                                                                               value="<#if userRef.uidAccessLevel == 0>публичная<#else>личная</#if>">
                                                                    </div>
                                                                    <input type="submit" class="btn btn-success btn-sm"
                                                                           value="Отправить">
                                                                </form>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                            </td>
                                        </div>
                                        <div class="col-sm">
                                            <td><#if userRef.source??><a
                                                    href="${userRef.source}">${userRef.source}</a></#if></td>
                                        </div>
                                        <div class="col-sm">
                                            <td><#if userRef.reference.rating??>${userRef.reference.rating}</#if></td>
                                        </div>
                                    </div>
                                </tr>
                            </#list>
                        </table>
                    <#--                    </div>-->
                        <@p.pager url page />
                    </#if>
                </#if>
            </div>
        </div>
    </div>
</@c.page>