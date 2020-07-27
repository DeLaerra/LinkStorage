<#import "parts/common.ftlh" as c>

<@c.page>
    <div class="container">
        <form action="/myAccount" method="post" enctype="multipart/form-data">
            <#if userInfo.avatar??>
                <div class="form-group row" style="text-align: center">
                    <div class="col-sm-6" style="height: 300px">
                        <img alt="My image" src="${avatarImage}"
                             style="max-width: 100%; max-height: 100%; display: block; margin: auto"/>
                    </div>
                </div>
            </#if>
            <#if userInfoAvatarError??>
                <div class="text-danger">
                    ${userInfoAvatarError}
                </div>
            </#if>
            <div class="form-group row" style="display: inline-block">
                <div class="col-sm-10">
                    <label for="file" class="btn btn-primary btn-sm mb-2">Выберите файл</label>
                    <input type="file" class="form-control-file" id="file" name="file" style="display: none">
                </div>
            </div>
            <div class="form-group row" style="display: inline-block">
                <div class="col-sm-6">
                    <button class="btn btn-primary btn-sm mb-2" type="submit" name="Upload"
                            value="Upload">Загрузить аватар
                    </button>
                </div>
            </div>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Имя:</label>
                <div class="col-sm-6">
                    <input required type="text" minlength="1" maxlength="15" name="name" value="${(userInfo.name)!''}"
                           class="form-control ${(userInfoNameError??)?string('is-invalid', '')}"
                           placeholder="${(userInfo.name)!''}"/>
                    <#if userInfoNameError??>
                        <div class="invalid-feedback">
                            ${userInfoNameError}
                        </div>
                    </#if>
                </div>
            </div>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Фамилия:</label>
                <div class="col-sm-6">
                    <input required type="text" minlength="1" maxlength="15" name="surname"
                           value="${(userInfo.surname)!''}"
                           class="form-control ${(userInfoSurnameError??)?string('is-invalid', '')}"
                           placeholder="${(userInfo.surname)!''}"/>
                    <#if userInfoSurnameError??>
                        <div class="invalid-feedback">
                            ${userInfoSurnameError}
                        </div>
                    </#if>
                </div>
            </div>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Возраст:</label>
                <div class="col-sm-6">
                    <input required type="number" minlength="1" maxlength="15" name="age" value="${(userInfo.age)!''}"
                           class="form-control ${(userInfoAgeError??)?string('is-invalid', '')}"
                           placeholder="${(userInfo.age)!''}"/>
                    <#if userInfoAgeError??>
                        <div class="invalid-feedback">
                            ${userInfoAgeError}
                        </div>
                    </#if>
                </div>
            </div>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Пол:</label>
                <div class="col-sm-6">
                    <#if "${userInfo.sex}" = "1">
                        <input type="radio" id="male" name="sex" value="1" checked>
                        <label for="male">Мужской</label>
                        <input type="radio" id="female" name="sex" value="2">
                        <label for="female">Женский</label>
                    <#elseif "${userInfo.sex}" = "2">
                        <input type="radio" id="male" name="sex" value="1">
                        <label for="male">Мужской</label>
                        <input type="radio" id="female" name="sex" value="2" checked>
                        <label for="female">Женский</label>
                    <#else>
                        <input type="radio" id="male" name="sex" value="1">
                        <label for="male">Мужской</label>
                        <input type="radio" id="female" name="sex" value="2">
                        <label for="female">Женский</label>
                    </#if>
                    <#if userInfoSexError??>
                        <div class="invalid-feedback">
                            ${userInfoSexError}
                        </div>
                    </#if>
                </div>
            </div>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Дата рождения:</label>
                <div class="col-sm-6">
                    <input required type="date" minlength="1" maxlength="10" name="birthDateStr"
                           value="${(birthDateStr)!''}"
                           class="form-control ${(userInfoBirthDateError??)?string('is-invalid', '')}"
                           placeholder="${(birthDateStr)!''}"/>
                    <#if userInfoBirthDateError??>
                        <div class="invalid-feedback">
                            ${userInfoBirthDateError}
                        </div>
                    </#if>
                </div>
            </div>
            <div class="form-actions">
                <input type="hidden" name="_csrf" value="${_csrf.token}"/>
                <button class="btn btn-primary btn-sm mb-2" type="submit" name="Edit"
                        value="Изменить">Изменить
                </button>
            </div>
        </form>
    </div>
</@c.page>