<%-- 
    Document   : register
    Created on : Feb 6, 2026, 2:46:02 PM
    Author     : Nitro 5 Tiger
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Register - FPT FOOD</title>
        <link
            rel="stylesheet"
            href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"
            />
        <link rel="stylesheet" href="./css/login.css" />
    </head>
    <body>

        <div class="login-card">

            <h2>Đăng ký tài khoản</h2>

            <form action="MainController" method="post">

                <input type="hidden" name="action" value="register"/>

                <div class="form-group">
                    <label>Tên đăng nhập</label>
                    <input
                        type="text"
                        name="username"
                        placeholder="Nhập tên đăng nhập"
                        value="${username}"
                        required/>
                </div>

                <div class="form-group">
                    <label>Mật khẩu</label>
                    <input
                        type="password"
                        name="password"
                        placeholder="Nhập mật khẩu"
                        required/>
                </div>

                <div class="form-group">
                    <label>Họ và tên</label>
                    <input
                        type="text"
                        name="fullname"
                        placeholder="Nhập họ và tên"
                        value="${fullname}"
                        required/>
                </div>

                <div class="form-group">
                    <label>Số điện thoại</label>
                    <input
                        type="text"
                        name="phone"
                        placeholder="Nhập số điện thoại"
                        value="${phone}"
                        required/>
                </div>
                        
                <div class="form-group">
                    <label>Email</label>
                    <input
                        type="text"
                        name="email"
                        placeholder="Nhập email"
                        value="${email}"
                        required/>
                </div>

                <button type="submit" class="btn btn-login">
                    Đăng ký
                </button>

                <c:if test="${not empty message}">
                    <span class="error-message">${message}</span>
                </c:if>

            </form>



        </div>

    </body>
</html>
