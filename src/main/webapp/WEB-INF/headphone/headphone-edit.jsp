<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>编辑头戴式耳机</title>
</head>
<body>
<h2>编辑头戴式耳机</h2>
<form action="${pageContext.request.contextPath}/headphone/update" method="post">
    <input type="hidden" name="id" value="${headphone.id}">
    <div class="form-group">
        <label>品牌:</label>
        <input type="text" name="brand" value="${headphone.brand}" required>
    </div>
    <div class="form-group">
        <label>型号:</label>
        <input type="text" name="model" value="${headphone.model}" required>
    </div>
    <div class="form-group">
        <label>驱动单元(mm):</label>
        <input type="number" step="0.1" name="driverSize" value="${headphone.driverSize}" required>
    </div>
    <div class="form-group">
        <label>阻抗(Ω):</label>
        <input type="number" name="impedance" value="${headphone.impedance}" required>
    </div>
    <div class="form-group">
        <label>灵敏度(dB):</label>
        <input type="number" name="sensitivity" value="${headphone.sensitivity}" required>
    </div>
    <div class="form-group">
        <label>频响范围:</label>
        <input type="text" name="frequencyResponse" value="${headphone.frequencyResponse}" required>
    </div>
    <div class="form-group">
        <label>价格(¥):</label>
        <input type="number" step="0.01" name="price" value="${headphone.price}" required>
    </div>
    <div class="form-group">
        <label>库存:</label>
        <input type="number" name="stock" value="${headphone.stock}" required>
    </div>
    <div class="form-group">
        <label>类型:</label>
        <select name="wireless">
            <option value="0" ${!headphone.wireless ? 'selected' : ''}>有线</option>
            <option value="1" ${headphone.wireless ? 'selected' : ''}>无线</option>
        </select>
    </div>
    <div class="form-group">
        <label>降噪:</label>
        <select name="noiseCancelling">
            <option value="0" ${!headphone.noiseCancelling ? 'selected' : ''}>非降噪</option>
            <option value="1" ${headphone.noiseCancelling ? 'selected' : ''}>降噪</option>
        </select>
    </div>
    <div>
        <button type="submit" class="btn-save">更新</button>
        <a href="${pageContext.request.contextPath}/headphone/list">
            <button type="button" class="btn-back">返回</button>
        </a>
    </div>
</form>
</body>
</html>