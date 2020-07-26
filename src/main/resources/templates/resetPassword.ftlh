<#import "parts/common.ftlh" as c>

<@c.page>

    <form action="/resetPassword" method="post">

        <h1 class="h3 mb-3 font-weight-normal">Введите новый пароль</h1>

        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Email:</label>
            <div class="col-sm-6">
                <input required type="email" name="email" value="<#if user??>${user.email!}</#if>"
                       class="form-control ${(usernameError??)?string('is-invalid', '')}" readonly/>

                <input type="hidden" name="_csrf" value="${_csrf.token}" />
            </div>
        </div>
        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Пароль:</label>
            <div class="col-sm-6">
                <input required type="password" minlength="6" maxlength="15" name="password" id="password"
                       class="form-control ${(passwordConfirmationError??)?string('is-invalid', '')}"
                       placeholder="Пароль"/>
            </div>
        </div>
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
        <button class="btn btn-primary" type="submit">Продолжить</button>
    </form>

</@c.page>




