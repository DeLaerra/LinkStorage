<#import "parts/common.ftlh" as c>

<@c.page>
    <h5 xmlns="http://www.w3.org/1999/html">Личный кабинет</h5>
    <form action="/myAccount" method="post">
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
                <input required type="text" minlength="1" maxlength="15" name="surname" value="${(userInfo.surname)!''}"
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
                <input required type="text" minlength="1" maxlength="15" name="age" value="${(userInfo.age)!''}"
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
                <input required type="date" minlength="1" maxlength="10" name="birthDateStr" value="${(birthDateStr)!''}"
                       class="form-control ${(userInfoBirthDateError??)?string('is-invalid', '')}"
                       placeholder="${(birthDateStr)!''}"/>
                <#if userInfoBirthDateError??>
                    <div class="invalid-feedback">
                        ${userInfoBirthDateError}
                    </div>
                </#if>
            </div>
        </div>
        <input type="hidden" name="_csrf" value="${_csrf.token}" />
        <input type="submit" value="Изменить" />
    </form>
</@c.page>