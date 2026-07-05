<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.text.DecimalFormat" %>
<!doctype html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>FPT Food - Dashboard</title>
        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
        <link rel="stylesheet" href="./css/dashboard.css"/>
    </head>


    <body>

        <header>
            <div class="brand">
                <h1>FPT Food - Dashboard</h1>
                <p>Xin chào, Quản lý</p>
            </div>
            <a href="MainController?action=logout" class="btn-logout">
                <i class="fas fa-sign-out-alt"></i> Đăng xuất
            </a>
        </header>

        <div class="container">

            <div class="nav-tabs">
                <a href="dashboardController?tab=tables"
                   class="tab-btn ${activeTab == 'tables' ? 'active' : ''}">
                    <i class="fas fa-chair"></i> Quản lý bàn
                </a>

                <a href="dashboardController?tab=categories"
                   class="tab-btn ${activeTab == 'categories' ? 'active' : ''}">
                    <i class="fas fa-tags"></i> Danh mục
                </a>

                <a href="dashboardController?tab=food"
                   class="tab-btn ${activeTab == 'food' ? 'active' : ''}">
                    <i class="fas fa-utensils"></i> Món ăn
                </a>
                <a href="MainController?action=getInventory&from=dashboard" class="tab-btn ${activeTab == 'inventory' ? 'active' : ''}"><i class="fas fa-boxes"></i> Quản lý kho</a>
                <a href="MainController?action=getAccounts" class="tab-btn ${activeTab == 'accounts' ? 'active' : ''}"><i class="fas fa-users"></i> Tài khoản</a> 
                <a href="MainController?action=bills" class="tab-btn ${activeTab == 'bills' ? 'active' : ''}"><i class="fas fa-file-invoice-dollar"></i> Hóa đơn</a>
            </div>

            <!-- ================= QUẢN LÝ BÀN ================= -->
            <c:if test="${activeTab == 'tables'}">
                <section class="section-content active">
                    <div class="table-grid">

                        <c:forEach var="t" items="${tables}">
                            <div class="table-card">

                                <div class="table-header">
                                    <b>${t.tableName}</b>
                                    <span class="badge ${t.status}">
                                        ${t.status}
                                    </span>
                                </div>

                                <!-- Dropdown -->
                                <form action="dashboardController" method="POST">
<!--                                    <input type="hidden" name="action" value="${updateTable}">-->
                                    <input type="hidden" name="action" value="updateTable">
                                    <input type="hidden" name="tableId" value="${t.tableID}">

                                    <div class="status-control">
                                        <select name="status"
                                                class="status-select">
                                            <option value="empty" ${t.status == 'empty' ? 'selected' : ''}>Trống</option>
                                            <option value="booked" ${t.status == 'booked' ? 'selected' : ''}>Đã đặt</option>
                                            <option value="busy" ${t.status == 'busy' ? 'selected' : ''}>Đang dùng</option>
                                        </select>


                                        <button type="submit" class="btn-save">
                                            <i class="fas fa-save"></i>
                                        </button>

                                    </div>
                                </form>

                                <!-- Order Summary phải nằm ngoài status-control -->
                                <c:if test="${t.status == 'busy'}">
                                    <div class="order-summary">
                                        Đang phục vụ: ${t.servingCount} món
                                        <span class="total-price">
                                            Tổng:
                                            <fmt:formatNumber value="${t.currentTotal}" type="number" groupingUsed="true"/>đ
                                        </span>
                                    </div>
                                </c:if>

                            </div>
                        </c:forEach>

                    </div>
                </section>
            </c:if>
            <!-- ================= DANH MỤC ================= -->
            <c:if test="${activeTab == 'categories'}">

                <section class="section-content active">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
                        <h3 style="margin: 0;">Danh sách danh mục</h3>

                        <form action="dashboardController?action=uploadCategoryCsv" method="POST" enctype="multipart/form-data" style="margin: 0;">
                            <label for="csvCategoryFile" class="btn-upload-csv" title="Nhập danh mục từ CSV">
                                <i class="fas fa-file-csv"></i>
                            </label>
                            <input type="file" id="csvCategoryFile" name="csvCategoryFile" accept=".csv" style="display: none;" onchange="this.form.submit()">
                        </form>
                    </div>

                    <c:if test="${not empty sessionScope.mesgCategory}">
                        <div style="color: #28a745; font-weight: bold; margin-bottom: 15px; font-size: 15px;">
                            ${sessionScope.mesgCategory}
                            <% session.removeAttribute("mesgCategory"); // Xóa thông báo sau khi đã hiển thị để tránh hiện mãi %>
                        </div>
                    </c:if>

                    <table class="data-table">

                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên danh mục</th>
                                <th>Mô tả</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:forEach var="c" items="${categories}">
                                <tr>

                                    <td>${c.categoryID}</td>

                                    <td>
                                        <input type="text"
                                               form="update-${c.categoryID}"
                                               name="name"
                                               value="${c.name}"
                                               class="form-control">
                                    </td>

                                    <td>
                                        <input type="text"
                                               form="update-${c.categoryID}"
                                               name="description"
                                               value="${c.description}"
                                               class="form-control">
                                    </td>

                                    <td style="display:flex; gap:10px;">

                                        <form id="update-${c.categoryID}"
                                              action="dashboardController"
                                              method="post">

                                            <input type="hidden" name="action" value="updateCategory">
                                            <input type="hidden" name="id" value="${c.categoryID}">

                                            <button type="submit"
                                                    class="action-btn"
                                                    style="color:green;">
                                                <i class="fas fa-save"></i>
                                            </button>

                                        </form>

                                        <form action="dashboardController"
                                              method="post"
                                              onsubmit="return confirm('Xóa danh mục này?')">

                                            <input type="hidden" name="action" value="deleteCategory">
                                            <input type="hidden" name="id" value="${c.categoryID}">

                                            <button type="submit"
                                                    class="action-btn"
                                                    style="color:red;">
                                                <i class="fas fa-trash"></i>
                                            </button>

                                        </form>

                                    </td>

                                </tr>
                            </c:forEach>

                        </tbody>

                    </table>

                    <h3 style="margin-top:40px;">Thêm danh mục</h3>

                    <form action="dashboardController" method="post">

                        <input type="hidden" name="action" value="insertCategory">

                        <input type="text"
                               name="name"
                               class="form-control"
                               placeholder="Tên danh mục"
                               required>

                        <input type="text"
                               name="description"
                               class="form-control"
                               placeholder="Mô tả">

                        <button type="submit" class="btn-add">
                            + Thêm danh mục
                        </button>

                    </form>

                </section>

            </c:if>
            <!-- ================= MÓN ĂN ================= -->
            <c:if test="${activeTab == 'food'}">

                <section class="section-content active">

                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
                        <h3 style="margin: 0;">Danh sách món ăn</h3>

                        <div style="display: flex; gap: 10px; align-items: center;">
                            
                            <form action="dashboardController?action=uploadFoodCsvDashboard" method="POST" enctype="multipart/form-data" style="margin: 0;">
                                <label for="csvFoodFile" class="btn-upload-csv" title="Nhập danh sách món ăn từ CSV">
                                    <i class="fas fa-file-csv"></i>
                                </label>
                                <input type="file" id="csvFoodFile" name="csvFoodFile" accept=".csv" style="display: none;" onchange="this.form.submit()">
                            </form>
                            
                            <button type="button" 
                                    class="btn-upload-csv" 
                                    title="Tải lên thư mục hình ảnh món ăn" 
                                    onclick="document.getElementById('uploadImageModal').classList.add('active')" 
                                    style="border: none; outline: none; background-color: #fff5f0;">
                                <i class="fas fa-images"></i>
                            </button>
                            
                        </div>
                    </div>

                    <c:if test="${not empty sessionScope.mesgFood}">
                        <div style="color: #28a745; font-weight: bold; margin-bottom: 15px; font-size: 15px;">
                            ${sessionScope.mesgFood}
                            <% session.removeAttribute("mesgFood"); // Xóa thông báo ngay sau khi hiển thị %>
                        </div>
                    </c:if>

                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Tên món</th>
                                <th>Danh mục</th>
                                <th>Giá</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:forEach var="f" items="${foods}">
                                <tr>

                                    <td>
                                        <input type="text"
                                               form="update-${f.foodID}"
                                               name="name"
                                               value="${f.name}"
                                               class="form-control"
                                               required>
                                    </td>

                                    <td>
                                        <select form="update-${f.foodID}"
                                                name="categoryID"
                                                class="form-control">

                                            <c:forEach var="c" items="${categories}">
                                                <option value="${c.categoryID}"
                                                        ${c.categoryID == f.categoryID ? 'selected' : ''}>
                                                    ${c.name}
                                                </option>
                                            </c:forEach>

                                        </select>
                                    </td>

                                    <td>
                                        <input type="number"
                                               form="update-${f.foodID}"
                                               name="price"
                                               value="${f.price}"
                                               class="form-control"
                                               required>
                                    </td>

                                    <td>
                                        <select form="update-${f.foodID}"
                                                name="status"
                                                class="form-control">

                                            <option value="available"
                                                    ${f.status == 'available' ? 'selected' : ''}>
                                                Sẵn sàng
                                            </option>

                                            <option value="unavailable"
                                                    ${f.status == 'unavailable' ? 'selected' : ''}>
                                                Ngừng bán
                                            </option>

                                        </select>
                                    </td>

                                    <td style="display:flex; gap:10px;">

                                        <form id="update-${f.foodID}"
                                              action="dashboardController"
                                              method="post">

                                            <input type="hidden" name="action" value="updateFood">
                                            <input type="hidden" name="foodID" value="${f.foodID}">

                                            <button type="submit"
                                                    class="action-btn"
                                                    style="color:green;">
                                                <i class="fas fa-save"></i>
                                            </button>

                                        </form>

                                        <form action="dashboardController"
                                              method="post"
                                              onsubmit="return confirm('Xóa món này?')">

                                            <input type="hidden" name="action" value="deleteFood">
                                            <input type="hidden" name="foodID" value="${f.foodID}">

                                            <button type="submit"
                                                    class="action-btn"
                                                    style="color:red;">
                                                <i class="fas fa-trash"></i>
                                            </button>

                                        </form>

                                    </td>

                                </tr>
                            </c:forEach>

                        </tbody>
                    </table>

                    <!-- ADD NEW FOOD -->
                    <h3 style="margin-top:40px;">Thêm món mới</h3>

                    <form action="dashboardController" method="post">

                        <input type="hidden" name="action" value="addFood">

                        <input type="text"
                               name="name"
                               class="form-control"
                               placeholder="Tên món"
                               required>

                        <input type="number"
                               name="price"
                               class="form-control"
                               placeholder="Giá"
                               required>

                        <select name="categoryID"
                                class="form-control"
                                required>

                            <c:forEach var="c" items="${categories}">
                                <option value="${c.categoryID}">
                                    ${c.name}
                                </option>
                            </c:forEach>

                        </select>

                        <select name="status"
                                class="form-control">

                            <option value="available">Sẵn sàng</option>
                            <option value="unavailable">Ngừng bán</option>

                        </select>

                        <button type="submit" class="btn-add">
                            + Thêm món
                        </button>

                    </form>


                </section>

            </c:if>

            <!-- ================= KHO (GIỮ NGUYÊN) ================= -->
            <section id="inventory" class="section-content ${activeTab == 'inventory' ? 'active' : ''}">
                <div style="display:flex; justify-content:space-between; align-items: center; margin-bottom: 15px;">
                    <div style="display:flex; align-items: center; gap: 15px;">
                        <h3 style="margin: 0;">Kho nguyên liệu</h3>

                        <form action="MainController?action=uploadIngredientCsv&from=dashboard" method="POST" enctype="multipart/form-data" style="margin: 0;">
                            <label for="csvIngredientFile" class="btn-upload-csv" title="Nhập kho hàng loạt từ CSV">
                                <i class="fas fa-file-csv"></i>
                            </label>
                            <input type="file" id="csvIngredientFile" name="csvIngredientFile" accept=".csv" style="display: none;" onchange="this.form.submit()">
                        </form>
                    </div>
                    <button class="btn-add" style="background:#28a745" onclick="document.getElementById('inventoryModal').classList.add('active')">+ Nhập kho</button>
                </div>
                <table class="data-table">
                    <thead><tr><th>Tên</th><th>Đơn vị</th><th>Số lượng</th><th>Thao tác</th></tr></thead>
                    <tbody>
                        <c:forEach items="${requestScope.listIngredient}" var="i">
                            <tr>
                                <td>${i.name}</td><td>${i.unit}</td><td>${i.quantityInStock}</td>
                                <td>
                                    <form action="MainController?action=deleteIngredient&&from=dashboard" method="POST" style="display:inline;" onsubmit="return confirm('Xóa?')">
                                        <input type="hidden" name="id" value="${i.ingredientID}">
                                        <input type="hidden" name="currentTab" value="inventory"> <button type="submit" class="action-btn" style="color:red"><i class="fas fa-trash"></i></button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </section>

            <section id="accounts" class="section-content <c:if test="${requestScope.activeTab == 'accounts'}">active</c:if>">
                    <div style="display:flex; justify-content:space-between;">
                        <div style="display: flex; align-items: center; gap: 15px;">
                            <h3 style="margin: 0;">Danh sách tài khoản</h3>

                            <form action="MainController?action=uploadUserCsv" method="POST" enctype="multipart/form-data" style="margin: 0;">
                                <label for="csvUserFile" class="btn-upload-csv" title="Nhập tài khoản từ CSV">
                                    <i class="fas fa-file-csv"></i>
                                </label>
                                <input type="file" id="csvUserFile" name="csvUserFile" accept=".csv" style="display: none;" onchange="this.form.submit()">
                            </form>
                        </div>
                        <button class="btn-add" onclick="openAddAccountModal()">+ Tạo tài khoản</button>
                    </div>
                    <table class="data-table">
                        <thead>
                            <tr><th>ID</th><th>Tên đăng nhập</th><th>Vai trò (Role)</th><th>Thao tác</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${requestScope.listUser}" var="u">
                            <tr>
                                <td>${u.userID}</td>
                                <td><b>${u.username}</b></td>
                                <td><span>${u.role}</span></td>
                                <td>
                                    <form action="MainController?action=deleteUser" method="POST" style="display:inline;" onsubmit="return confirm('Xóa tài khoản này?')">
                                        <input type="hidden" name="id" value="${u.userID}">
                                        <input type="hidden" name="currentTab" value="accounts"> <button type="submit" class="action-btn" style="color:red"><i class="fas fa-trash"></i></button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </section>

            <section id="bills" class="section-content ${activeTab == 'bills' ? 'active' : ''}">
                <h3>Hóa đơn cần thanh toán</h3>
                <div class="table-grid">
                    <c:forEach items="${listBills}" var="b">
                        <c:if test="${b.key.status == 'busy'}">
                            <div class="table-card" style="border-left: 5px solid var(--danger);">
                                <div style="display:flex; justify-content:space-between; margin-bottom:10px;"><b>${b.key.tableName}</b><span class="badge busy">Chưa thanh toán</span></div>
                                <p>Tổng tiền tạm tính: <b>${b.value}đ</b></p>
                                <form action="MainController?action=pay" method="POST" style="margin-top:15px;">
                                    <input type="hidden" name="tableId" value="${b.key.tableID}">
                                    <input type="hidden" name="currentTab" value="bills"> <button type="submit" class="btn-add" style="width:100%; background:#28a745;">Xác nhận Thanh toán</button>
                                </form>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </section>
        </div>

        <div class="modal" id="accountModal">
            <div class="modal-content">
                <h3>Tạo tài khoản mới</h3>
                <form action="MainController?action=addUser" method="POST">
                    <input type="hidden" name="currentTab" value="accounts"> <label>Tên đăng nhập:</label>
                    <input type="text" name="username" id="accUsername" class="form-control" required>
                    <label>Mật khẩu:</label>
                    <input type="text" name="password" id="accPassword" class="form-control" required>
                    <label>Vai trò:</label>
                    <select name="role" id="accRole" class="form-control">
                        <option value="worker">Worker (Nhân viên)</option>
                        <option value="manager">Admin (Quản lý)</option>
                    </select>
                    <div class="modal-footer">
                        <button type="button" class="action-btn" onclick="document.getElementById('accountModal').classList.remove('active')">Hủy</button>
                        <button type="submit" class="btn-add">Tạo mới</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="modal" id="categoryModal">
            <div class="modal-content">
                <h3>Thông tin danh mục</h3>
                <form action="SaveCategoryServlet" method="POST">
                    <input type="hidden" name="currentTab" value="categories"> <input type="hidden" name="id" id="modalCatId">
                    <label>Tên danh mục:</label><input type="text" name="name" id="modalCatName" class="form-control" required>
                    <label>Mô tả:</label><input type="text" name="desc" id="modalCatDesc" class="form-control">
                    <div class="modal-footer"><button type="button" class="action-btn" onclick="document.getElementById('categoryModal').classList.remove('active')">Hủy</button><button type="submit" class="btn-add">Lưu lại</button></div>
                </form>
            </div>
        </div>

        <div class="modal" id="foodModal">
            <div class="modal-content">
                <h3>Thông tin món ăn</h3>
                <form action="SaveFoodServlet" method="POST">
                    <input type="hidden" name="currentTab" value="food"> <input type="hidden" name="id" id="modalFoodId">
                    <label>Tên món:</label><input type="text" name="name" id="modalFoodName" class="form-control" required>
                    <label>Giá bán:</label><input type="number" name="price" id="modalFoodPrice" class="form-control" required>
                    <div class="modal-footer"><button type="button" class="action-btn" onclick="document.getElementById('foodModal').classList.remove('active')">Hủy</button><button type="submit" class="btn-add">Lưu lại</button></div>
                </form>
            </div>
        </div>

        <div class="modal" id="inventoryModal">
            <div class="modal-content">
                <h3>Nhập kho mới</h3>
                <form action="MainController?action=addIngredient&from=dashboard" method="POST">
                    <input type="hidden" name="currentTab" value="inventory"> <label>Tên nguyên liệu:</label><input type="text" name="name" class="form-control" required>
                    <label>Số lượng:</label><input type="number" name="qty" class="form-control" required>
                    <div class="modal-footer"><button type="button" class="action-btn" onclick="document.getElementById('inventoryModal').classList.remove('active')">Hủy</button><button type="submit" class="btn-add">Lưu</button></div>
                </form>
            </div>
        </div>
        <div class="modal" id="uploadImageModal">
            <div class="modal-content">
                <h3>Upload hình ảnh món ăn (Thư mục)</h3>
                <form action="MainController?action=uploadImgFood" method="POST" enctype="multipart/form-data">
                    <p style="margin-bottom: 15px; font-size: 14px; color: #666; line-height: 1.5;">
                        <i>* Vui lòng chọn một <b>Thư mục (Folder)</b> chứa các ảnh. Tên file ảnh cần khớp với tên trong Database (VD: pho-bo.png). File sẽ được lưu thẳng vào hệ thống.</i>
                    </p>

                    <input type="file" 
                           name="folderImages" 
                           class="form-control" 
                           multiple 
                           required>

                    <div class="modal-footer">
                        <button type="button" class="action-btn" onclick="document.getElementById('uploadImageModal').classList.remove('active')">Hủy</button>
                        <button type="submit" class="btn-add"><i class="fas fa-cloud-upload-alt"></i> Tải lên Server</button>
                    </div>
                </form>
            </div>
        </div>
        <script>
            function switchTab(tabId, btn) {
                document.querySelectorAll(".section-content").forEach(el => el.classList.remove("active"));
                document.querySelectorAll(".tab-btn").forEach(el => el.classList.remove("active"));
                document.getElementById(tabId).classList.add("active");
                btn.classList.add("active");
            }

            // JS cho Tài khoản
            function openAddAccountModal() {
                document.getElementById('accUsername').value = "";
                document.getElementById('accPassword').value = "";
                document.getElementById('accRole').value = "worker"; // Default
                document.getElementById('accountModal').classList.add('active');
            }

            // Các hàm cũ giữ nguyên
            function openAddCategoryModal() {
                document.getElementById('modalCatId').value = "";
                document.getElementById('modalCatName').value = "";
                document.getElementById('modalCatDesc').value = "";
                document.getElementById('categoryModal').classList.add('active');
            }
            function openEditCategoryModal(id, name, desc) {
                document.getElementById('modalCatId').value = id;
                document.getElementById('modalCatName').value = name;
                document.getElementById('modalCatDesc').value = desc;
                document.getElementById('categoryModal').classList.add('active');
            }
            function openAddFoodModal() {
                document.getElementById('modalFoodId').value = "";
                document.getElementById('modalFoodName').value = "";
                document.getElementById('modalFoodPrice').value = "";
                document.getElementById('foodModal').classList.add('active');
            }
            function openEditFoodModal(id, name, price) {
                document.getElementById('modalFoodId').value = id;
                document.getElementById('modalFoodName').value = name;
                document.getElementById('modalFoodPrice').value = price;
                document.getElementById('foodModal').classList.add('active');
            }
        </script>

    </body>
</html>