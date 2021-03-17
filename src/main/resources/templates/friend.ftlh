<#import "parts/common.ftlh" as c>
<#import "parts/pager.ftlh" as p>

<@c.page>
    <div class="container">
        <#if friendName??>
            <div class="row">
                <#if friend.userInfo.avatar??>
                    <div class="col-md-1">
                        <img alt="My image" src="${friend.userInfo.getAvatarBase64()}"
                             style="width: 50px; height: 50px; display: block; margin: auto; border-radius: 100px;"/>
                    </div>
                </#if>
                <div class="col-md-4">
                    <#if friend.userInfo.getName() != "Name">
                        <h5 xmlns="http://www.w3.org/1999/html"
                            align="left">${friend.userInfo.getName()} ${friend.userInfo.getSurname()}</h5>
                    </#if>
                    <h6 xmlns="http://www.w3.org/1999/html" align="left">${friendName}</h6>
                </div>
            </div>
        </#if>
        <#if notAddedFriend??>
            <#if requestAcceptionAllow??>
                <form name="addFriend" action="/sendRequest/${friend.uid}" method="get">
                    <input class="form-control" type="hidden" name="addFriend" id="addFriend" value="${friend.uid}">
                    <div class="row">
                        <div class="col-sm">
                            <th scope="col">
                                <button type="submit" class="btn btn-success btn-sm">
                                    <span class="iconify" data-icon="mdi:check-box-outline" data-inline="false"></span>
                                </button>
                                ${requestAcceptionAllow}
                            </th>
                        </div>
                    </div>
                </form>
            <#else>
                <#if requestSent??>
                    <form name="addFriend" action="/sendRequest/${friend.uid}" method="get">
                        <input class="form-control" type="hidden" name="addFriend" id="addFriend" value="${friend.uid}">
                        <div class="row">
                            <div class="col-sm">
                                <th scope="col">
                                    <button type="submit" class="btn btn-success btn-sm" disabled>
                                        <span class="iconify" data-icon="mdi:check-box-outline"
                                              data-inline="false"></span></button>
                                    ${requestSent}
                                </th>
                            </div>
                        </div>
                    </form>
                <#else>
                    <form name="addFriend" action="/sendRequest/${friend.uid}" method="get">
                        <input class="form-control" type="hidden" name="addFriend" id="addFriend" value="${friend.uid}">
                        <div class="row">
                            <div class="col-sm">
                                <th scope="col">
                                    <button type="submit" class="btn btn-success btn-sm">
                                        <span class="iconify" data-icon="mdi:account-plus" data-inline="false"></span>
                                    </button>

                                </th>
                            </div>
                        </div>
                        <#--        <a href="/friendHome">FriendHome</a>-->
                    </form>
                </#if>
            </#if>
        <#else>
            <div class="row">
                <div class="col-md-8">
                    <form name="deleteFriend" action="/deleteFriend/${friend.uid}" method="get">
                        <th scope="col">
                            <button type="submit" class="btn btn-danger btn-sm">
                                <span class="iconify" data-icon="mdi:account-minus" data-inline="false"></span>
                            </button>
                        </th>
                    </form>
                </div>
            </div>
        </#if>
    </div>

    <div class="container">
        <div class="row">

            <div class="col-10">
                <#if page??>
                    <#if page.content?size !=0 >
                        <div class="container">
                            <table class="table table-striped">
                                <thead>
                                <tr>
                                    <#--                Шапка таблицы-->
                                    <div class="row">
                                        <form name="sorting" action="/friend/${friendUid}" method="GET">
                                            <div class="col-sm">
                                                <th scope="col">
                                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy"
                                                            value="nameDesc">
                                                    <span class="iconify" data-icon="gg:sort-az"
                                                          data-inline="false"></span>
                                                    </button>
                                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy"
                                                            value="nameAsc">
                                                    <span class="iconify" data-icon="gg:sort-za"
                                                          data-inline="false"></span>
                                                    </button>
                                                    Имя
                                                </th>
                                            </div>
                                            <div class="col-sm">
                                                <th scope="col">
                                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy"
                                                            value="sourceDesc">
                                                    <span class="iconify" data-icon="gg:sort-az"
                                                          data-inline="false"></span>
                                                    </button>
                                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy"
                                                            value="sourceAsc">
                                                    <span class="iconify" data-icon="gg:sort-za"
                                                          data-inline="false"></span>
                                                    </button>
                                                    Источник
                                                </th>
                                            </div>
                                            <div class="col-sm">
                                                <th scope="col">
                                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy"
                                                            value="ratingDesc">
                                                    <span class="iconify" data-icon="gg:sort-az"
                                                          data-inline="false"></span>
                                                    </button>
                                                    <button class="btn btn-light btn-sm" type="submit" name="sortBy"
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
                                    <#if userRef.uidAccessLevel == 0>
                                        <tr>
                                            <#--                            Строки таблицы-->
                                            <div class="row">
                                                <div class="col-sm">
                                                    <td> <#if userRef.name??><a
                                                            href="${userRef.reference.url}">${userRef.name}</a>
                                                            <br><#else><a
                                                            href="${userRef.reference.url}">${userRef.reference.url}<br>
                                                            </a></#if>
                                                        <#if userRef.tag??>Теги: <#list userRef.tag as userRefTag><a class="text-"
                                                                                                                     href="../searchResult?search=%23${userRefTag.name}&area=notAll">#${userRefTag.name} </a></#list><br></#if>

                                                        <div class="btn-toolbar mr-4" role="toolbar"
                                                             aria-label="Toolbar with button groups">
                                                            <div class="btn-group mr-2" role="group" aria-label="Share">
                                                                <form name="addFriendsReference"
                                                                      action="/addFriendsReference/${friend.uid}/${userRef.uid}"
                                                                      method="get">


                                                                    <#if userRef.isExistAtFriend == 1>
                                                                        <button class="btn btn btn-success btn-sm"
                                                                                type="submit"
                                                                                title="Добавлено" disabled>
                                                                            <span class="iconify"
                                                                                  data-icon="mdi:check-box-outline"
                                                                                  data-inline="false"></span>
                                                                        </button>
                                                                    <#else>

                                                                        <button class="btn btn btn-success btn-sm"
                                                                                type="submit"
                                                                                title="Добавить к себе">
                                                                            <span class="iconify" data-icon="mdi:plus"
                                                                                  data-inline="false"></span>
                                                                        </button>
                                                                    </#if>
                                                                </form>

                                                            </div>
                                                            <div class="btn-group mr-2" role="group"
                                                                 aria-label="Properties">
                                                                <!-- Button trigger collapse Properties Подробнее...-->
                                                                <button type="button" class="btn btn-light btn-sm"
                                                                        data-toggle="collapse"
                                                                        data-target="#${userRef.uid}"><span
                                                                            class="iconify"
                                                                            data-icon="ion:ellipsis-horizontal-circle"
                                                                            data-inline="false"></span>
                                                                </button>
                                                            </div>
                                                        </div>
                                                        <!-- Collapse Подробнее...-->
                                                        <div class="collapse" id="${userRef.uid}" tabindex="-1"
                                                             role="dialog"
                                                             aria-labelledby="${userRef.uid}Label" aria-hidden="true">
                                                            <div class="card card-body">
                                                                <div class="body">
                                                                    <a href="${userRef.reference.url}">${userRef.reference.url}</a><br>
                                                                    <#if userRef.tag??><b>Теги</b>: <#list userRef.tag as userRefTag><a class="text-"
                                                                                                                                        href="../searchResult?search=%23${userRefTag.name}&area=notAll">#${userRefTag.name} </a></#list>
                                                                        <br></#if>
                                                                    <#if userRef.source??><b>Источник</b>: <a
                                                                            href="http://${userRef.source}">${userRef.source}</a>
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
                                                                    <div class="modal-header">
                                                                        <h5 class="modal-title"
                                                                            id="${userRef.uid}Label"><#if userRef.name??>
                                                                                <a
                                                                                href="${userRef.reference.url}">${userRef.name}</a>
                                                                                <br><#else><a
                                                                                href="${userRef.reference.url}">${userRef.reference.url}
                                                                                <br></a></#if></h5>
                                                                        <button type="button" class="close"
                                                                                data-dismiss="modal"
                                                                                aria-label="Close">
                                                                            <span aria-hidden="true">&times;</span>
                                                                        </button>
                                                                    </div>
                                                                    <div class="modal-body">
                                                                        <form method="post" name="userRef"
                                                                              action="reference/update/${userRef.uid}">
                                                                            <input type="hidden" name="_csrf"
                                                                                   value="${_csrf.token}"/>
                                                                            <input type="hidden" name="uidUser"
                                                                                   value="${userRef.uidUser}"/>
                                                                            <a href="${userRef.reference.url}">${userRef.reference.url}</a><br>
                                                                            <div class="form-group">
                                                                                <label for="${userRef.uid}nameInput">Название:</label>
                                                                                <input type="text" name="name"
                                                                                       class="form-control"
                                                                                       id="${userRef.uid}nameInput"
                                                                                       placeholder="Название"
                                                                                       value="<#if userRef.name??>${userRef.name}</#if>">
                                                                            </div>
                                                                            <div class="form-group">
                                                                                <label for="${userRef.uid}urlInput">Ссылка:</label>
                                                                                <input type="text" name="url"
                                                                                       class="form-control"
                                                                                       id="${userRef.uid}urlInput"
                                                                                       placeholder="Ссылка"
                                                                                       value="<#if userRef.reference.url??>${userRef.reference.url}</#if>">
                                                                            </div>
                                                                            <#if userRef.description??>
                                                                                <div class="form-group">
                                                                                    <label for="${userRef.uid}descriptionInput">Описание:</label>
                                                                                    <input type="text"
                                                                                           name="description"
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
                                                                            <#if userRef.tag??>
                                                                                <div class="form-group">
                                                                                    <label for="${userRef.uid}tagInput">Теги:</label>
                                                                                    <input type="text" name="tag"
                                                                                           class="form-control"
                                                                                           id="${userRef.uid}tagInput"
                                                                                           placeholder="Тэг"
                                                                                           value="<#if userRef.tags??>${userRef.tags}</#if>">
                                                                                </div>
                                                                            </#if>
                                                                            <div class="form-group">
                                                                                <label for="${userRef.uid}sourceInput">Источник:</label>
                                                                                <input type="text" name="source"
                                                                                       class="form-control"
                                                                                       id="${userRef.uid}sourceInput"
                                                                                       placeholder="Источник"
                                                                                       value="<#if userRef.source??>${userRef.source}</#if>">
                                                                            </div>
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
                                                                            <input type="submit"
                                                                                   class="btn btn-success btn-sm"
                                                                                   value="Сохранить">
                                                                        </form>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </td>
                                                </div>
                                                <div class="col-sm">
                                                    <td><#if userRef.source??><a
                                                            href="http://${userRef.source}">${userRef.source}</a></#if></td>
                                                </div>
                                                <div class="col-sm">
                                                    <td><#if userRef.reference.rating??>${userRef.reference.rating}</#if></td>
                                                </div>
                                            </div>
                                        </tr>
                                    </#if>
                                </#list>
                            </table>
                        </div>
                        <@p.pager url page />
                    </#if>
                </#if>
            </div>
        </div>
    </div>
</@c.page>