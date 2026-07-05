<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${sessionScope.user == null}">
    <c:redirect url="login.jsp"></c:redirect>
</c:if>
<c:if test="${sessionScope.listOrders == null}">
    <c:redirect url="orderController"></c:redirect>
</c:if>

<!doctype html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <title>FPT Food - Bếp</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
        <link rel="stylesheet" href="./css/kitchen.css"/>
    </head>
    <body>

        <header>
            <div class="brand"><h1>FPT Food - Bếp</h1></div>
            <a href="MainController?action=logout" style="text-decoration:none; color:#333;"><i class="fas fa-sign-out-alt"></i> Đăng xuất</a>
        </header>

        <div class="container">
            <div class="main-nav">
                <a href="MainController?action=getOrder" style="text-decoration: none; color: black" class="nav-pill <c:if test="${requestScope.activeSection == 'orders'}">active</c:if>" onclick="switchSection('orders', this)">
                        <i class="fas fa-fire"></i> Đơn hàng
                    </a>
                    <a href="MainController?action=getRecipe" style="text-decoration: none; color: black" class="nav-pill <c:if test="${requestScope.activeSection == 'recipes'}">active</c:if>" onclick="switchSection('recipes', this)">
                        <i class="fas fa-book-open"></i> Công thức
                    </a>
                    <a href="MainController?action=getIngredient&from=kitchen" style="text-decoration: none; color: black" class="nav-pill <c:if test="${requestScope.activeSection == 'inventory'}">active</c:if>" onclick="switchSection('inventory', this)">
                        <i class="fas fa-boxes"></i> Kiểm tra kho
                    </a>
                </div>

                <div id="section-orders" class="section-content <c:if test="${requestScope.activeSection == 'orders'}">active</c:if>">

                    <div class="tabs-container">
                        <button class="sub-tab-btn active" onclick="filterOrders('pending', this)">Chờ xử lý</button>
                        <button class="sub-tab-btn" onclick="filterOrders('cooking', this)">Đang nấu</button>
                        <button class="sub-tab-btn" onclick="filterOrders('all', this)">Tất cả</button>
                    </div>

                    <div class="order-grid">
                    <c:if test="${not empty requestScope.mesg}">
                        <span style="color: red; font:bolders ">${requestScope.mesg}</span>
                    </c:if>
                    <c:if test="${empty requestScope.mesg}">
                        <c:forEach items="${sessionScope.listOrders}" var="o">
                            <div class="order-card status-${o.status}">
                                <div style="display:flex; justify-content:space-between; margin-bottom:10px;">
                                    <b>${o.tableID}</b> <span style="color:#777"><fmt:formatDate value="${o.createdTime}" pattern="HH:mm"/></span>
                                </div>
                                <div style="margin-bottom:15px;">
                                    <c:forEach items="${o.orderItem}" var="oi">
                                        <div><b style="color:var(--primary)">x${oi.quantity}</b> ${oi.foodName}</div>
                                    </c:forEach>
                                </div>

                                <form action="MainController?action=updateOrder" method="POST">
                                    <input type="hidden" name="orderId" value="${o.orderID}">
                                    <c:if test="${o.status == 'pending'}">
                                        <input type="hidden" name="status" value="pending">
                                        <button class="btn-action btn-receive">Nhận đơn & Nấu</button>
                                    </c:if>
                                    <c:if test="${o.status == 'cooking'}">
                                        <input type="hidden" name="status" value="cooking">
                                        <button class="btn-action btn-done">Xong món</button>
                                    </c:if>
                                    <c:if test="${o.status == 'done'}">
                                        <div style="text-align:center; color:green;"><b>Đã xong</b></div>
                                    </c:if>
                                </form>
                            </div>
                        </c:forEach>
                    </c:if>
                </div>

                <div id="empty-msg" style="text-align:center; color:#999; margin-top:30px; display:none;">
                    Không có đơn hàng nào ở trạng thái này.
                </div>
            </div>

            <div id="section-recipes" class="section-content <c:if test="${requestScope.activeSection == 'recipes'}">active</c:if>">
                    <div class="recipe-layout">
                        <div class="sidebar">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                                <h4 style="margin: 0;">Menu món ăn</h4> <form action="MainController?action=uploadFoodCsv" method="POST" enctype="multipart/form-data" style="margin: 0;">
                                    <label for="csvFile" class="btn-upload-csv" title="Nhập danh sách món ăn từ CSV">
                                        <i class="fas fa-file-csv"></i>
                                    </label>
                                    <input type="file" id="csvFile" name="csvFile" accept=".csv" style="display: none;" onchange="this.form.submit()">
                                </form>
                            </div>
                        <c:if test="${not empty requestScope.mesg}">
                            <span>${requestScope.mesg}</span>
                        </c:if>
                        <c:if test="${empty requestScope.mesg}">
                            <c:forEach items="${requestScope.listFood}" var="f">
                                <div class="food-item <c:if test="${not empty selectedFood and f.foodID == selectedFood.foodID}">active</c:if>"
                                     onclick="window.location.href = 'MainController?action=viewRecipesDetail&foodId=${f.foodID}'">
                                    <span>${f.name}</span>
                                    <form action="MainController?action=updateStatusFood" method="POST" style="margin:0" onclick="event.stopPropagation()">
                                        <input type="hidden" name="foodId" value="${f.foodID}">
                                        <input type="hidden" name="status" value="${f.status}">
                                        <label class="toggle-switch">
                                            <input type="checkbox" <c:if test="${f.status == 'available'}">checked</c:if>  onchange="this.form.submit()">
                                                <span class="slider"></span>
                                            </label>
                                        </form>
                                    </div>
                            </c:forEach>
                        </c:if>   
                    </div>

                    <div class="content">
                        <c:if test="${not empty requestScope.selectedFood}">
                            <div style="display:flex; justify-content:space-between; align-items:center;">
                                <h2 style="color:var(--primary); margin:0;">${selectedFood.name}</h2>
                                <button onclick="openModal()" style="background:var(--primary); color:white; border:none; padding:8px 15px; border-radius:5px; cursor:pointer;">+ Thêm nguyên liệu</button>
                            </div>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Nguyên liệu</th>
                                        <th>Cần dùng</th>
                                        <th>Tồn kho</th>
                                        <th>Đơn vị</th>
                                        <th style="width: 100px; text-align: center;">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${requestScope.listRecipe}" var="r">
                                        <c:if test="${r.foodID == selectedFood.foodID}">
                                            <c:forEach items="${listIngredient}" var="i">
                                                <c:if test="${r.ingredientID == i.ingredientID}">
                                                    <tr>
                                                        <td>
                                                            <c:if test="${i.name != null}">${i.name}</c:if>
                                                            </td>
                                                            <td>${r.amountNeeded}</td>
                                                        <td style="color: ${i.quantityInStock > r.amountNeeded ? 'green' : 'red'}">
                                                            <c:if test="${i.quantityInStock != null}">${i.quantityInStock}</c:if>
                                                            </td>
                                                            <td>
                                                            <c:if test="${i.unit != null}">${i.unit}</c:if>
                                                            </td>

                                                            <td style="text-align: center;">
                                                                <button class="btn-icon edit" title="Cập nhật lượng dùng"
                                                                        onclick="openUpdateModal('${selectedFood.foodID}', '${i.ingredientID}', '${r.amountNeeded}', '${i.name}')">
                                                                <i class="fas fa-edit"></i>
                                                            </button>

                                                            <a href="MainController?action=deletetRecipe&foodId=${selectedFood.foodID}&ingId=${i.ingredientID}" 
                                                               class="btn-icon delete" title="Xóa khỏi công thức"
                                                               onclick="return confirm('Bạn chắc chắn muốn xóa nguyên liệu ${i.name} khỏi công thức này?');">
                                                                <i class="fas fa-trash"></i>
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </c:if>
                                            </c:forEach>
                                        </c:if>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                        <c:if test="${empty requestScope.selectedFood}">
                            <p style="text-align:center; color:#999; margin-top:50px;">Chọn một món ăn bên trái để xem</p>
                        </c:if>
                    </div>
                </div>
            </div>

            <div id="section-inventory" class="section-content <c:if test="${activeSection == 'inventory'}">active</c:if>">
                    <h3>Tình trạng kho</h3>
                <c:if test="${not empty requestScope.mesg}">
                    <span style="color: red; font:bolder ">${requestScope.mesg}</span>
                </c:if>
                <c:if test="${empty requestScope.mesg}">
                    <c:forEach items="${requestScope.listIngredient}" var="i">
                        <div class="inv-card">
                            <b>${i.name}</b><br>
                            <span style="font-size:20px">${i.quantityInStock} ${i.unit}</span>
                        </div>
                    </c:forEach>
                </c:if>
            </div>
        </div>

        <div class="modal" id="ingredientModal">
            <div class="modal-content">
                <h3 style="margin-top:0; color:var(--primary)">Thêm nguyên liệu</h3>
                <form action="MainController?action=addRecipe" method="POST">
                    <input type="hidden" name="foodId" value="${selectedFood.foodID}">
                    <div class="form-group">
                        <label>Chọn nguyên liệu</label>
                        <select name="ingId" class="form-control">
                            <c:forEach items="${requestScope.listIngredient}" var="i">
                                <option value="${i.ingredientID}">${i.name}</option> 
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group"><label>Số lượng cần</label><input type="number" name="amountNeeded" step="0.1" class="form-control" required></div>
                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeModal()">Hủy</button>
                        <button type="submit" class="btn-confirm">Lưu lại</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="modal" id="updateIngredientModal">
            <div class="modal-content">
                <h3 style="margin-top:0; color:var(--primary)">Cập nhật lượng dùng</h3>
                <p id="updateIngNameDisplay" style="color: #666; font-style: italic;"></p>

                <form action="MainController?action=updateRecipe" method="POST">
                    <input type="hidden" name="foodId" id="updateFoodId">
                    <input type="hidden" name="ingId" id="updateIngId">

                    <div class="form-group">
                        <label>Số lượng cần mới</label>
                        <input type="number" name="amountNeeded" id="updateAmountNeeded" step="0.01" class="form-control" required>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeUpdateModal()">Hủy</button>
                        <button type="submit" class="btn-confirm">Cập nhật</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            // 1. JS EVENT: CHUYỂN MAIN TAB
            function switchSection(sectionId, btn) {
                document.querySelectorAll('.section-content').forEach(el => el.classList.remove('active'));
                document.querySelectorAll('.nav-pill').forEach(el => el.classList.remove('active'));
                document.getElementById('section-' + sectionId).classList.add('active');
                btn.classList.add('active');
            }

            // 2. JS EVENT: LỌC ĐƠN HÀNG (SUB-NAV)
            function filterOrders(status, btn) {
                // Active nút bấm
                document.querySelectorAll('.sub-tab-btn').forEach(el => el.classList.remove('active'));
                btn.classList.add('active');

                // Lọc Order Card
                const cards = document.querySelectorAll('.order-card');
                let hasItem = false;

                cards.forEach(card => {
                    // Kiểm tra class (status-pending, status-cooking...)
                    if (status === 'all' || card.classList.contains('status-' + status)) {
                        card.style.display = 'block';
                        hasItem = true;
                    } else {
                        card.style.display = 'none';
                    }
                });

                // Hiển thị thông báo nếu rỗng
                document.getElementById('empty-msg').style.display = hasItem ? 'none' : 'block';
            }

            // 3. JS EVENT: POPUP THÊM (Cũ)
            function openModal() {
                document.getElementById('ingredientModal').classList.add('active');
            }
            function closeModal() {
                document.getElementById('ingredientModal').classList.remove('active');
            }

            // 4. MỚI: JS EVENT CHO POPUP CẬP NHẬT
            function openUpdateModal(foodId, ingId, currentAmount, ingName) {
                // Gán giá trị vào các input trong form modal
                document.getElementById('updateFoodId').value = foodId;
                document.getElementById('updateIngId').value = ingId;
                document.getElementById('updateAmountNeeded').value = currentAmount;
                document.getElementById('updateIngNameDisplay').innerText = "Nguyên liệu: " + ingName;

                // Hiển thị modal
                document.getElementById('updateIngredientModal').classList.add('active');
            }

            function closeUpdateModal() {
                document.getElementById('updateIngredientModal').classList.remove('active');
            }

            // Đóng modal khi click ra ngoài vùng content
            window.onclick = function (event) {
                if (event.target == document.getElementById('ingredientModal')) {
                    closeModal();
                }
                if (event.target == document.getElementById('updateIngredientModal')) {
                    closeUpdateModal();
                }
            }

            // Mặc định chạy lọc Pending khi mới vào trang
            window.onload = function () {
                // Tìm nút đang active ở sub-tab (nếu có) và click nó để kích hoạt bộ lọc
                const activeSubBtn = document.querySelector('.sub-tab-btn.active');
                if (activeSubBtn)
                    filterOrders('pending', activeSubBtn);
            }
        </script>
    </body>
</html>