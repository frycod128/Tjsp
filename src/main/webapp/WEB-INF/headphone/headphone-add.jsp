<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>添加头戴式耳机</title>
</head>
<body>
<h2>添加头戴式耳机</h2>
<form action="${pageContext.request.contextPath}/headphone/save" method="post">
    <div class="form-group">
        <label>品牌:</label>
        <input type="text" name="brand" required>
    </div>
    <div class="form-group">
        <label>型号:</label>
        <input type="text" name="model" required>
    </div>
    <div class="form-group">
        <label>驱动单元(mm):</label>
        <input type="number" step="0.1" name="driverSize" required>
    </div>
    <div class="form-group">
        <label>阻抗(Ω):</label>
        <input type="number" name="impedance" required>
    </div>
    <div class="form-group">
        <label>灵敏度(dB):</label>
        <input type="number" name="sensitivity" required>
    </div>
    <div class="form-group">
        <label>频响范围:</label>
        <input type="text" name="frequencyResponse" placeholder="例如: 20Hz-20kHz" required>
    </div>
    <div class="form-group">
        <label>价格(¥):</label>
        <input type="number" step="0.01" name="price" required>
    </div>
    <div class="form-group">
        <label>库存:</label>
        <input type="number" name="stock" required>
    </div>
    <div class="form-group">
        <label>类型:</label>
        <select name="wireless">
            <option value="0">有线</option>
            <option value="1">无线</option>
        </select>
    </div>
    <div class="form-group">
        <label>降噪:</label>
        <select name="noiseCancelling">
            <option value="0">非降噪</option>
            <option value="1">降噪</option>
        </select>
    </div>
    <div>
        <button type="submit" class="btn-save">保存</button>
        <a href="${pageContext.request.contextPath}/headphone/list">
            <button type="button" class="btn-back">返回</button>
        </a>
    </div>
</form>
</body>
</html>