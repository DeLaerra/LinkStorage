<#import "parts/common.ftlh" as c>
<#import "parts/pager.ftlh" as p>
<#include "parts/security.ftlh">

<@c.page>
    <div class="container">
        <form name="search" action="/searchResult" method="GET">
            <div class="row">
                <div class="col-sm">
                    <input class="form-control" type="text" name="search" id="search" placeholder="Поиск ссылок">
                    <div class="form-group form-check">
                        <input type="checkbox" class="form-check-input" id="area" name="area" value="all">
                        <label class="form-check-label" for="area">искать по ссылкам всех пользователей</label>
                    </div>
                </div>
                <div class="col-sm">
                    <button type="submit" class="btn btn-primary">Найти</button>
                </div>
                <div class="col-sm">
                </div>
            </div>
        </form>
        <div class="row">
            <div class="col-sm">
                <!-- Button trigger modal Add Добавить...-->
                <button type="button" class="btn btn-success btn-sm" data-toggle="modal" data-target="#add_user_form">
                    Добавить
                    ссылку
                </button>
                <!-- Modal Add-->
                <div class="modal fade" id="add_user_form" tabindex="-1" role="dialog"
                     aria-labelledby="add_Label" aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
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
                                        <input type="text" name="url" class="form-control" id="add_urlInput"
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
                                        <select class="form-control" name="uidReferenceType" id="add_refTypeInput">
                                            <option value="0" selected>текст</option>
                                            <option value="1">видео</option>
                                            <option value="2">файл</option>
                                            <option value="3">картинка</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="add_tagInput">Теги:</label>
                                        <input type="text" name="tag" class="form-control" id="add_tagInput"
                                               placeholder="Тэг">
                                    </div>
                                    <div class="form-group">
                                        <label for="add_sourceInput">Источник:</label>
                                        <input type="text" name="source" class="form-control" id="add_sourceInput"
                                               placeholder="Источник">
                                    </div>
                                    <div class="form-group">
                                        <label for="add_accessLevelInput">Уровень доступа:</label>
                                        <select class="form-control" name="uidAccessLevel" id="add_accessLevelInput">
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
            </div>
            <div class="col-sm">
            </div>
            <div class="col-sm">
            </div>
        </div>
    </div>
    <#if page??>
        <div class="container">
            <table class="table table-striped">
                <thead>
                <tr>
                    <#--                Шапка таблицы-->
                    <div class="row">
                        <form name="sorting" action="/searchResult" method="GET">
                            <div class="col-sm">
                                <th scope="col">
                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy"
                                            value="nameDesc">
                                        <span class="iconify" data-icon="gg:sort-az" data-inline="false"></span>
                                    </button>
                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy"
                                            value="nameAsc">
                                        <span class="iconify" data-icon="gg:sort-za" data-inline="false"></span>
                                    </button>
                                    Имя
                                </th>
                            </div>
                            <div class="col-sm">
                                <th scope="col">
                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy" value="sourceDesc">
                                        <span class="iconify" data-icon="gg:sort-az" data-inline="false"></span>
                                    </button>
                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy" value="sourceAsc">
                                        <span class="iconify" data-icon="gg:sort-za" data-inline="false"></span>
                                    </button>
                                    Источник
                                </th>
                            </div>
                            <div class="col-sm">
                                <th scope="col">
                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy" value="ratingDesc">
                                        <span class="iconify" data-icon="gg:sort-az" data-inline="false"></span>
                                    </button>
                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy" value="ratingAsc">
                                        <span class="iconify" data-icon="gg:sort-za" data-inline="false"></span>
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
                                <td> <#if userRef.name??><a href="${userRef.reference.url}">${userRef.name}</a><br><#else><a
                                        href="${userRef.reference.url}">${userRef.reference.url}<br></a></#if>
                                    <#if userRef.tag??>Теги: ${userRef.tag}<br></#if>

                                    <div class="btn-toolbar mr-4" role="toolbar"
                                         aria-label="Toolbar with button groups">
                                        <#if userUid == userRef.uidUser>
                                            <div class="btn-group mr-2" role="group" aria-label="Edit">
                                                <!-- Button trigger modal Edit Изменить...-->

                                                <button type="submit" class="btn btn-success btn-sm" data-toggle="modal"
                                                        data-target="#edit_${userRef.uid}">
                                                <span class="iconify" data-icon="mdi:circle-edit-outline"
                                                      data-inline="false"></span></button>
                                            </div>
                                            <div class="btn-group mr-2" role="group" aria-label="Delete">
                                                <!-- Button Delete Удалить...-->
                                                <form method="get" action="reference/delete/${userRef.uid}">
                                                    <button class="btn btn btn-danger btn-sm" type="submit"
                                                            name="Delete"
                                                            value="Удалить">
                                                    <span class="iconify" data-icon="mdi:delete-circle-outline"
                                                          data-inline="false"></span>
                                                    </button>
                                                </form>
                                            </div>
                                        </#if>
                                        <#if userUid == userRef.uidUser><#else>
                                            <div class="btn-group mr-2" role="group" aria-label="Share">
                                                <!-- Button Get Reference Добавить ссылку...-->
                                                <button type="button" class="btn btn-info btn-sm"><span class="iconify"
                                                                                                        data-icon="el:download"
                                                                                                        data-inline="false"></span>
                                                </button>
                                            </div>
                                        </#if>
                                        <div class="btn-group mr-2" role="group" aria-label="Properties">
                                            <!-- Button trigger collapse Properties Подробнее...-->
                                            <button type="button" class="btn btn-light btn-sm" data-toggle="collapse"
                                                    data-target="#${userRef.uid}"><span class="iconify"
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
                                                <#if userRef.tag??><b>Теги</b>: ${userRef.tag}<br></#if>
                                                <#if userRef.source??><b>Источник</b>: <a
                                                        href="${userRef.source}">${userRef.source}</a><br></#if>
                                                <#if userRef.additionDate??><b>Дата
                                                    добавления</b>: ${userRef.additionDate}
                                                    <br></#if>
                                                <#if userRef.description??><b>Описание: </b>${userRef.description}
                                                    <br></#if>
                                                <b>Уровень доступа: </b> <#if userRef.uidAccessLevel == 0>публичная
                                                    <br><#else>личная
                                                    <br></#if>
                                                <b>Тип: </b>
                                                <#if userRef.uidReferenceType == 0>текст
                                                    <br><#elseif userRef.uidReferenceType == 1>видео
                                                    <br>
                                                <#elseif userRef.uidReferenceType == 2>файл
                                                    <br><#elseif userRef.uidReferenceType == 3>картинка
                                                    <br></#if>
                                                <#if userRef.rating??><b>Рейтинг: </b>${userRef.rating}</#if>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- Modal Edit-->
                                    <div class="modal fade" id="edit_${userRef.uid}" tabindex="-1" role="dialog"
                                         aria-labelledby="${userRef.uid}Label" aria-hidden="true">
                                        <div class="modal-dialog" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title"
                                                        id="${userRef.uid}Label"><#if userRef.name??><a
                                                            href="${userRef.reference.url}">${userRef.name}</a><br><#else><a
                                                            href="${userRef.reference.url}">${userRef.reference.url}<br></a></#if></h5>
                                                    <button type="button" class="close" data-dismiss="modal"
                                                            aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    <form method="post" name="userRef"
                                                          action="reference/update/${userRef.uid}">
                                                        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                                                        <input type="hidden" name="uidUser" value="${userRef.uidUser}"/>
                                                        <a href="${userRef.reference.url}">${userRef.reference.url}</a><br>
                                                        <div class="form-group">
                                                            <label for="${userRef.uid}nameInput">Название:</label>
                                                            <input type="text" name="name" class="form-control"
                                                                   id="${userRef.uid}nameInput" placeholder="Название"
                                                                   value="<#if userRef.name??>${userRef.name}</#if>">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="${userRef.uid}urlInput">Ссылка:</label>
                                                            <input type="text" name="url" class="form-control"
                                                                   id="${userRef.uid}urlInput" placeholder="Ссылка"
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
                                                            <select class="form-control" name="uidReferenceType"
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
                                                        <#if userRef.tag??>
                                                            <div class="form-group">
                                                                <label for="${userRef.uid}tagInput">Теги:</label>
                                                                <input type="text" name="tag" class="form-control"
                                                                       id="${userRef.uid}tagInput" placeholder="Тэг"
                                                                       value="<#if userRef.tag??>${userRef.tag}</#if>">
                                                            </div>
                                                        </#if>
                                                        <div class="form-group">
                                                            <label for="${userRef.uid}sourceInput">Источник:</label>
                                                            <input type="text" name="source" class="form-control"
                                                                   id="${userRef.uid}sourceInput" placeholder="Источник"
                                                                   value="<#if userRef.source??>${userRef.source}</#if>">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="${userRef.uid}accessLevelInput">Уровень
                                                                доступа:</label>
                                                            <select class="form-control" name="uidAccessLevel"
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
                                </td>
                            </div>
                            <div class="col-sm">
                                <td><#if userRef.source??><a href="${userRef.source}">${userRef.source}</a></#if></td>
                            </div>
                            <div class="col-sm">
                                <td><#if userRef.rating??>${userRef.rating}</#if></td>
                            </div>
                        </div>
                    </tr>
                </#list>
            </table>
        </div>
    </#if>
    <@p.pager url page />
</@c.page>