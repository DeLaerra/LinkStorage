<#import "parts/common.ftlh" as c>

<@c.page>

    <form action="/passwordChange" method="post">
        <img class="mb-4" src="/image/logo.jpg" alt="" width="72" height="72">
        <h1 class="h3 mb-3 font-weight-normal">Восстановление доступа к странице</h1>
        <h3 class="h6 mb-6 font-weight-normal">Пожалуйста, укажите email, который Вы использовали для входа на сайт.</h3>


        <div class="form-group row">
            <label class="col-sm-2 col-form-label">Email:</label>
            <div class="col-sm-6">
                <input required type="email" name="email" value="<#if user??>${user.email!}</#if>"
                       class="form-control ${(usernameErr??)?string('is-invalid', '')}"
                       placeholder="somemail@email.com"/>
                <#if usernameErr??>
                    <div class="invalid-feedback">
                        ${usernameErr}
                    </div>
                </#if>
                <input type="hidden" name="_csrf" value="${_csrf.token}" />
            </div>
        </div>
        <button class="btn btn-primary" type="submit">Продолжить</button>
    </form>
</@c.page>