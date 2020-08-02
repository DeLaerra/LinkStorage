<#import "parts/common.ftlh" as c>
<#include "parts/security.ftlh">

<@c.page>
    <ul class="nav nav-tabs">
        <li class="nav-item">
            <a class="nav-link active" data-toggle="tab" href="#inbox">Входящие</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-toggle="tab" href="#sent">Исходящие</a>
        </li>
    </ul>
    <div class="tab-content">
        <#--        Вкладка Входящие-->
        <div class="tab-pane fade show active" id="inbox">
            <div class="container">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <#--                Шапка таблицы-->
                        <div class="row">
                            <div class="col-sm">
                                <th scope="col">
                                    От кого
                                </th>
                            </div>
                            <div class="col-sm">
                                <th scope="col">
                                    Ссылка
                                </th>
                            </div>
                            <div class="col-sm">
                                <th scope="col">
                                    Время отправки
                                </th>
                            </div>
                            <div class="col-sm">
                                <th scope="col">
                                    Статус
                                </th>
                            </div>
                        </div>
                    </tr>
                    </thead>
                    <#list pmInbox as pm>
                        <tr>
                            <#--                            Строки таблицы-->
                            <div class="row">
                                <div class="col-sm">
                                    <td>
                                        ${pm.sender.username}
                                    </td>
                                </div>
                                <div class="col-sm">
                                    <td>
                                        <#if pm.text??>${pm.text}</#if>
                                        <#if pm.referenceDescription.name??><a
                                            href="${pm.referenceDescription.reference.url}">${pm.referenceDescription.name}</a>
                                            <br><#else><a
                                            href="${pm.referenceDescription.reference.url}">${pm.referenceDescription.reference.url}
                                            <br></a></#if>
                                        <div class="btn-toolbar mr-4" role="toolbar"
                                             aria-label="Toolbar with button groups">

                                            <#if pm.acceptionStatus == 3><#else >
                                                <!-- Button Get Reference Добавить ссылку...-->
                                                <div class="btn-group mr-2" role="group" aria-label="Share">
                                                    <form method="post" name="userRefAddToHome"
                                                          action="reference/copy/${pm.referenceDescription.uid}">
                                                        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                                                        <button type="submit" class="btn btn-success btn-sm"
                                                                title="Добавить ссылку">
                                                    <span class="iconify" data-icon="emojione-v1:white-heavy-check-mark"
                                                          data-inline="false"></span>
                                                        </button>
                                                    </form>
                                                </div>

                                                <!-- Button Reject Отклонить...-->
                                                <div class="btn-group mr-2" role="group" aria-label="Cancel">
                                                    <form method="get" action="messages/reject/${pm.uid}">
                                                        <button class="btn btn btn-danger btn-sm" type="submit"
                                                                title="Отклонить добавление ссылки">
                                                    <span class="iconify" data-icon="si-glyph:circle-error"
                                                          data-inline="false"></span>
                                                        </button>
                                                    </form>
                                                </div>
                                            </#if>

                                            <!-- Button trigger collapse Properties Подробнее...-->
                                            <div class="btn-group mr-2" role="group" aria-label="Properties">
                                                <button type="button" class="btn btn-light btn-sm"
                                                        title="Подробнее"
                                                        data-toggle="collapse"
                                                        data-target="#${pm.uid}"><span class="iconify"
                                                                                       data-icon="ion:ellipsis-horizontal-circle"
                                                                                       data-inline="false"></span>
                                                </button>
                                            </div>

                                            <!-- Button Delete Удалить...-->
                                            <div class="btn-group mr-2" role="group" aria-label="Delete">
                                                <form method="get" action="messages/delete/${pm.uid}">
                                                    <button class="btn btn btn-danger btn-sm" type="submit"
                                                            title="Удалить сообщение">
                                                            <span class="iconify"
                                                                  data-icon="mdi:delete-circle-outline"
                                                                  data-inline="false"></span>
                                                    </button>
                                                </form>
                                            </div>
                                        </div>
                                        <!-- Collapse Подробнее...-->
                                        <div class="collapse" id="${pm.uid}" tabindex="-1" role="dialog"
                                             aria-labelledby="${pm.uid}Label" aria-hidden="true">
                                            <div class="card card-body">
                                                <div class="body">
                                                    <a href="${pm.referenceDescription.reference.url}">${pm.referenceDescription.reference.url}</a><br>
                                                    <#if pm.referenceDescription.tag??>
                                                        <b>Теги</b>: ${pm.referenceDescription.tag}<br></#if>
                                                    <#if pm.referenceDescription.source??><b>Источник</b>: <a
                                                            href="${pm.referenceDescription.source}">${pm.referenceDescription.source}</a>
                                                        <br></#if>
                                                    <#if pm.referenceDescription.additionDate??><b>Дата
                                                        добавления</b>: ${pm.referenceDescription.additionDate}
                                                        <br></#if>
                                                    <#if pm.referenceDescription.description??>
                                                        <b>Описание: </b>${pm.referenceDescription.description}
                                                        <br></#if>
                                                    <b>Уровень
                                                        доступа: </b> <#if pm.referenceDescription.uidAccessLevel == 0>публичная
                                                        <br><#else>личная
                                                        <br></#if>
                                                    <b>Тип: </b>
                                                    <#if pm.referenceDescription.uidReferenceType == 0>текст
                                                        <br><#elseif pm.referenceDescription.uidReferenceType == 1>видео
                                                        <br>
                                                    <#elseif pm.referenceDescription.uidReferenceType == 2>файл
                                                        <br><#elseif pm.referenceDescription.uidReferenceType == 3>картинка
                                                        <br></#if>
                                                    <#if pm.referenceDescription.reference.rating??>
                                                        <b>Рейтинг: </b>${pm.referenceDescription.reference.rating}</#if>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </div>
                                <div class="col-sm">
                                    <td>${(pm.sendingTime).format('yyyy-MM-dd HH:mm:ss')}</td>
                                </div>
                                <div class="col-sm">
                                    <td>
                                        <#if pm.acceptionStatus == 0>Не рассмотрено
                                        <#elseif pm.acceptionStatus == 1>Добавлено
                                        <#elseif pm.acceptionStatus == 2>Отклонено
                                        <#elseif pm.acceptionStatus == 3>Дубликат
                                        </#if>
                                    </td>
                                </div>
                            </div>
                        </tr>
                    </#list>
                </table>
            </div>
        </div>

        <#--        Вкладка Исходящие-->
        <div class="tab-pane fade" id="sent">
            <div class="container">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <#--                Шапка таблицы-->
                        <div class="row">
                            <div class="col-sm">
                                <th scope="col">
                                    Кому
                                </th>
                            </div>
                            <div class="col-sm">
                                <th scope="col">
                                    Ссылка
                                </th>
                            </div>
                            <div class="col-sm">
                                <th scope="col">
                                    Время отправки
                                </th>
                            </div>
                            <div class="col-sm">
                                <th scope="col">
                                    Статус
                                </th>
                            </div>
                        </div>
                    </tr>
                    </thead>
                    <#list pmSent as pm>
                        <tr>
                            <#--                            Строки таблицы-->
                            <div class="row">
                                <div class="col-sm">
                                    <td>
                                        ${pm.recipient.username}
                                    </td>
                                </div>
                                <div class="col-sm">
                                    <td>
                                        <#if pm.text??>${pm.text}<#else></#if>
                                        <#if pm.referenceDescription.name??><a
                                            href="${pm.referenceDescription.reference.url}">${pm.referenceDescription.name}</a>
                                            <br><#else><a
                                            href="${pm.referenceDescription.reference.url}">${pm.referenceDescription.reference.url}
                                            <br></a></#if>

                                        <div class="btn-toolbar mr-4" role="toolbar"
                                             aria-label="Toolbar with button groups">

                                            <!-- Button Delete Удалить...-->
                                            <div class="btn-group mr-2" role="group" aria-label="Delete">
                                                <form method="get" action="messages/delete/${pm.uid}">
                                                    <button class="btn btn btn-danger btn-sm" type="submit"
                                                            title="Удалить сообщение">
                                                            <span class="iconify"
                                                                  data-icon="mdi:delete-circle-outline"
                                                                  data-inline="false"></span>
                                                    </button>
                                                </form>
                                            </div>

                                            <!-- Button trigger collapse Properties Подробнее...-->
                                            <div class="btn-group mr-2" role="group" aria-label="Properties">
                                                <button type="button" class="btn btn-light btn-sm"
                                                        title="Подробнее"
                                                        data-toggle="collapse"
                                                        data-target="#${pm.uid}"><span class="iconify"
                                                                                       data-icon="ion:ellipsis-horizontal-circle"
                                                                                       data-inline="false"></span>
                                                </button>
                                            </div>
                                        </div>
                                        <!-- Collapse Подробнее...-->
                                        <div class="collapse" id="${pm.uid}" tabindex="-1" role="dialog"
                                             aria-labelledby="${pm.uid}Label" aria-hidden="true">
                                            <div class="card card-body">
                                                <div class="body">
                                                    <a href="${pm.referenceDescription.reference.url}">${pm.referenceDescription.reference.url}</a><br>
                                                    <#if pm.referenceDescription.tag??>
                                                        <b>Теги</b>: ${pm.referenceDescription.tag}<br></#if>
                                                    <#if pm.referenceDescription.source??><b>Источник</b>: <a
                                                            href="${pm.referenceDescription.source}">${pm.referenceDescription.source}</a>
                                                        <br></#if>
                                                    <#if pm.referenceDescription.additionDate??><b>Дата
                                                        добавления</b>: ${pm.referenceDescription.additionDate}
                                                        <br></#if>
                                                    <#if pm.referenceDescription.description??>
                                                        <b>Описание: </b>${pm.referenceDescription.description}
                                                        <br></#if>
                                                    <b>Уровень
                                                        доступа: </b> <#if pm.referenceDescription.uidAccessLevel == 0>публичная
                                                        <br><#else>личная
                                                        <br></#if>
                                                    <b>Тип: </b>
                                                    <#if pm.referenceDescription.uidReferenceType == 0>текст
                                                        <br><#elseif pm.referenceDescription.uidReferenceType == 1>видео
                                                        <br>
                                                    <#elseif pm.referenceDescription.uidReferenceType == 2>файл
                                                        <br><#elseif pm.referenceDescription.uidReferenceType == 3>картинка
                                                        <br></#if>
                                                    <#if pm.referenceDescription.reference.rating??>
                                                        <b>Рейтинг: </b>${pm.referenceDescription.reference.rating}</#if>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </div>
                                <div class="col-sm">
                                    <td>${(pm.sendingTime).format('yyyy-MM-dd HH:mm:ss')}</td>
                                </div>
                                <div class="col-sm">
                                    <td>
                                        <#if pm.acceptionStatus == 0>Не рассмотрено
                                        <#elseif pm.acceptionStatus == 1>Добавлено
                                        <#elseif pm.acceptionStatus == 2>Отклонено
                                        <#elseif pm.acceptionStatus == 3>Не рассмотрено</#if>
                                    </td>
                                </div>
                            </div>
                        </tr>
                    </#list>
                </table>
            </div>
        </div>
    </div>
</@c.page>

