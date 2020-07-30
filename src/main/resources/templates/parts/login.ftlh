<#macro login path isRegisterForm>
    <script src="https://code.iconify.design/1/1.0.7/iconify.min.js"></script>
    <form action="${path}" method="post">
        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Логин:</label>
            <div class="col-sm-6">
                <input required type="text" minlength="3" maxlength="30" name="username" value="<#if user??>${user.username}</#if>"
                       class="form-control ${(usernameError??)?string('is-invalid', '')}"
                       placeholder="Никнейм"/>
                <#if usernameError??>
                    <div class="invalid-feedback">
                        ${usernameError}
                    </div>
                </#if>
            </div>
        </div>
        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Пароль:</label>
            <div class="col-sm-6">
                <input required type="password" minlength="6" maxlength="15" name="password" id="password"
                       class="form-control ${(passwordError??)?string('is-invalid', '')}"
                       placeholder="Пароль"/>
                <#if passwordError??>
                    <div class="invalid-feedback">
                        ${passwordError}
                    </div>
                </#if>
            </div>
        </div>
        <#if isRegisterForm>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Подтверждение:</label>
                <div class="col-sm-6">
                    <input required type="password" minlength="6" maxlength="15" id="passwordConfirmation" name="passwordConfirmation"
                           class="form-control ${(passwordConfirmationError??)?string('is-invalid', '')}"
                           placeholder="Подтверждение пароля"/>
                    <#if passwordConfirmationError??>
                        <div class="invalid-feedback">
                            ${passwordConfirmationError}
                        </div>
                    </#if>
                </div>
            </div>
            <div class="form-group row">
                <label class="col-sm-2 col-form-label">Email:</label>
                <div class="col-sm-6">
                    <input required type="email" name="email" value="<#if user??>${user.email}</#if>"
                           class="form-control ${(emailError??)?string('is-invalid', '')}"
                           placeholder="somemail@email.com"/>
                    <#if emailError??>
                        <div class="invalid-feedback">
                            ${emailError}
                        </div>
                    </#if>
                </div>
            </div>
        </#if>
        <input type="hidden" name="_csrf" value="${_csrf.token}" />
        <#if !isRegisterForm>
            <a href="/passwordChange">Забыли пароль?</a><br>
            <br>
            <div class="d-flex justify-content-start social_icon">
            <span class="iconify" data-icon="entypo-social:vk-with-circle" data-inline="false"
                  style="font-size: 40px"></span>
                <span class="iconify" data-icon="ant-design:google-circle-filled" data-inline="false"
                      style="font-size: 40px"></span>
            </div>
            <br>
        </#if>
        <button class="btn btn-primary" type="submit"><#if isRegisterForm>Регистрация<#else>Вход</#if></button>
        <#if !isRegisterForm>
            <a href="/registration" class="btn btn-primary" target="_blank">Регистрация</a>
        </#if>
    </form>
</#macro>

<#macro logout>
    <form action="/logout" method="post">
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
        <button class="btn btn-primary" type="submit">Выход</button>
    </form>
</#macro>

