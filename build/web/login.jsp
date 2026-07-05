<%-- Document : Login Created on : Jan 20, 2026, 10:57:37 AM Author : AN --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!doctype html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Đăng nhập - FPT Food</title>
        <link
            rel="stylesheet"
            href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"
            />
        <link rel="stylesheet" href="./css/login.css" />
    </head>
    <body>
        <div class="login-card">
            <div class="logo-circle">
                <i class="fas fa-utensils"></i>
            </div>

            <h2>FPT Food</h2>
            <c:if test="${not empty loginError}">
                <p style="color:red; text-align:center;">
                    ${loginError}
                </p>
            </c:if>
            <c:remove var="loginError" scope="session"/>
            

            <form action="MainController" method="post">
                <input type="hidden" name="action" value="login" />

                <div class="form-group">
                    <label>Tên đăng nhập</label>
                    <input
                        type="text"
                        name="username"
                        placeholder="Nhập tên đăng nhập"
                        required
                        />
                </div>

                <div class="form-group">
                    <label>Mật khẩu</label>
                    <input
                        type="password"
                        name="password"
                        placeholder="Nhập mật khẩu"
                        required
                        />
                </div>

                <button type="submit" class="btn btn-login">Đăng nhập</button>
            </form>

            <c:if test="${not empty message}">
                <span class="error-message">${message}</span>
            </c:if>

            <!-- FORM RIÊNG cho Register -->
            <form action="MainController" method="get">
                <input type="hidden" name="action" value="register" />
                <button type="submit" class="btn btn-register">
                    Đăng ký tài khoản
                </button>
            </form>
        </div>
    </body>
</html>
