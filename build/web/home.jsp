<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!doctype html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>FPT Food</title>

        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"/>
        <link rel="stylesheet" href="css/home.css"/>
    </head>

    <body>

        <div class="container">

            <!-- HEADER -->
            <header>
                <div class="logo">FPT Food</div>

                <div class="auth-buttons">
                    <c:if test="${sessionScope.user == null}">
                        <a href="MainController?action=login" class="btn-auth">
                            <i class="fas fa-sign-in-alt"></i> Đăng nhập
                        </a>
                    </c:if>

                    <c:if test="${sessionScope.user != null}">
                        <a href="MainController?action=logout" class="btn-auth">
                            <i class="fas fa-sign-out-alt"></i> Đăng xuất
                        </a>
                    </c:if>
                </div>
            </header>

            <!-- NAV TABS -->
            <div class="nav-tabs">
                <a href="MainController?action=home&tab=menu"
                   class="tab-btn ${activeTab == 'menu' ? 'active' : ''}">
                    Menu đặt món
                </a>

                <a href="MainController?action=history"
                   class="tab-btn ${activeTab == 'history' ? 'active' : ''}">
                    Lịch sử đơn hàng
                </a>
            </div>

            <!-- ================= MENU ================= -->
            <c:if test="${activeTab == 'menu'}">

                <!-- CATEGORY -->
                <div class="categories">

                    <form action="MainController" method="get" style="display:flex;gap:12px">
                        <input type="hidden" name="action" value="home"/>
                        <input type="hidden" name="tab" value="menu"/>

                        <button class="cat-chip ${empty param.cat ? 'active' : ''}">
                            Tất cả
                        </button>

                        <c:forEach var="c" items="${categories}">

                            <button class="cat-chip ${param.cat == c.name ? 'active' : ''}"
                                    name="cat"
                                    value="${c.name}">
                                ${c.name}
                            </button>

                        </c:forEach>

                    </form>

                </div>

                <!-- FOOD GRID -->
                <c:if test="${empty foods}">
                    <div style="text-align:center;color:#999;margin-top:40px">
                        Chưa có món ăn
                    </div>
                </c:if>

                <div class="food-grid">
                    <c:forEach var="f" items="${foods}">
                        <div class="food-card">
                            <div class="food-img">
                                <img src="${pageContext.request.contextPath}/${f.imageURL}" alt="${f.name}">
                            </div>

                            <div class="card-body">
                                <div>
                                    <h4>${f.name}</h4>
                                    <small>${f.categoryName}</small>
                                </div>

                                <div style="display:flex;justify-content:space-between;align-items:center">
                                    <span class="price">
                                        <fmt:formatNumber value="${f.price}" pattern="#,###"/>đ
                                    </span>

                                    <form action="MainController?action=addToCart" method="post">
                                        <input type="hidden" name="foodId" value="${f.foodID}">
                                        <input type="hidden" name="cat" value="${param.cat}">
                                        <button class="btn-add">+ Thêm</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

            </c:if>


            <!-- ================= HISTORY ================= -->
            <c:if test="${activeTab == 'history'}">

                <h2 style="margin-top:20px;">Lịch sử đơn hàng</h2>

                <c:if test="${orders == null || orders.isEmpty()}">
                    <p>Bạn chưa có đơn hàng nào.</p>
                </c:if>

                <c:forEach var="o" items="${orders}">
                    <div class="order-card">

                        <h4>Đơn #${o.orderID}</h4>

                        <div style="color:#777;font-size:14px;">
                            <fmt:formatDate value="${o.createdTime}"
                                            pattern="dd/MM/yyyy HH:mm"/>
                        </div>

                        <p>Trạng thái: <b>${o.status}</b></p>

                        <ul>
                            <c:forEach var="i" items="${o.orderItem}">
                                <li>${i.foodName} (x${i.quantity})</li>
                                </c:forEach>
                        </ul>

                        <p><b>
                                Tổng:
                                <fmt:formatNumber value="${o.finalPrice}" pattern="#,###"/>đ
                            </b></p>

                    </div>
                </c:forEach>

            </c:if>

        </div>


        <!-- ================= CART (chỉ hiển thị khi tab menu) ================= -->
        <c:if test="${activeTab == 'menu'}">

            <button class="fab-cart" onclick="toggleCart()">
                <i class="fas fa-shopping-cart"></i>
                <span class="cart-count">
                    <c:out value="${totalQty != null ? totalQty : 0}"/>
                </span>
            </button>

            <div class="cart-overlay" onclick="toggleCart()"></div>

            <div class="cart-sidebar">

                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:15px;">
                    <h3>
                        Giỏ hàng (
                        <c:out value="${totalQty != null ? totalQty : 0}"/>
                        )
                    </h3>
                    <span onclick="toggleCart()" style="cursor:pointer;font-size:20px">&times;</span>
                </div>

                <div class="cart-items">

                    <c:if test="${empty cart}">
                        <p style="text-align:center;color:#999;">Giỏ hàng trống</p>
                    </c:if>

                    <c:if test="${not empty cartError}">
                        <p style="color:red; text-align:center;">
                            ${cartError}
                        </p>
                    </c:if>               
                    <c:remove var="cartError" scope="session"/>

                    <c:if test="${not empty orderSuccess}">
                        <p style="color:green; text-align:center;">
                            ${orderSuccess}
                        </p>
                    </c:if>               
                    <c:remove var="orderSuccess" scope="session"/>

                    <c:forEach items="${cart}" var="c">
                        <c:set var="f" value="${foodMap[c.key]}" />

                        <div class="cart-item">
                            <div style="flex:1;">
                                <div style="font-weight:bold">${f.name}</div>

                                <div style="font-size:13px;color:#777;">
                                    <fmt:formatNumber value="${f.price}" pattern="#,###"/>đ
                                </div>

                                <!-- QUANTITY CONTROLS -->
                                <div class="qty-controls">
                                    <form action="MainController?action=updateCart" method="post" style="display:flex;gap:6px">
                                        <input type="hidden" name="foodId" value="${f.foodID}"/>

                                        <button class="btn-qty" name="cartAction" value="minus">−</button>

                                        <span class="qty-text">${c.value}</span>

                                        <button class="btn-qty" name="cartAction" value="plus">+</button>
                                    </form>
                                </div>
                            </div>

                            <div style="font-weight:bold;">
                                <fmt:formatNumber value="${f.price * c.value}" pattern="#,###"/>đ
                            </div>
                        </div>
                    </c:forEach>

                </div>

                <!-- CART FOOTER -->
                <div class="cart-footer">

                    <!-- VOUCHER -->
                    <form action="MainController?action=applyVoucher" method="post">
                        <input type="text" name="code"
                               class="voucher-input"
                               placeholder="Mã giảm giá (VD: SALE10)">
                        <button type="submit" class="btn-apply">Áp dụng</button>
                    </form>

                    <c:if test="${not empty voucherError}">
                        <p style="color:red;text-align:center">
                            ${voucherError}
                        </p>
                    </c:if>

                    <!-- TẠM TÍNH -->
                    <div style="display:flex;justify-content:space-between;margin-bottom:6px;">
                        <span>Tạm tính:</span>
                        <span>
                            <fmt:formatNumber value="${total}" pattern="#,###"/>đ
                        </span>
                    </div>

                    <!-- GIẢM GIÁ -->
                    <div style="display:flex;justify-content:space-between;margin-bottom:10px;color:var(--primary);">
                        <span>Giảm giá:</span>
                        <span>
                            -<fmt:formatNumber value="${discount}" pattern="#,###"/>đ
                        </span>
                    </div>

                    <!-- TỔNG CỘNG -->
                    <div style="display:flex;justify-content:space-between;font-weight:bold;font-size:18px;">
                        <span>Tổng cộng:</span>
                        <span>
                            <fmt:formatNumber value="${finalTotal}" pattern="#,###"/>đ
                        </span>
                    </div>

                    <form action="MainController?action=customerOrder" method="post">
                        <input type="hidden" name="userID" value="${sessionScope.user.userID}">
                        <button type="submit" class="btn-checkout">
                            Đặt món ngay
                        </button>
                    </form>

                </div>

            </div>

        </c:if>

        <script>
            function toggleCart() {
                document.body.classList.toggle("cart-open");
            }
        </script>

    </body>
</html>