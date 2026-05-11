# Scamsung SSU Checker

English | [简体中文](#简体中文)

---

## English

### What is SSU?

**SSU (Service Software Unlock)** is Samsung's regional SIM lock bypass mechanism. It is a **temporary unlock** granted by Samsung that allows a device locked to one carrier or region to be used with a SIM card from a supported region.

> ⚠️ **SSU is not a permanent unlock.** Your device can be re-locked at any time — for example after a software update, factory reset, or when Samsung revokes the unlock remotely.

### What does "No SSU" mean?

If the app shows **No SSU**, it means the device either:
- Was never locked to a specific region/carrier, or
- Does not support the SSU mechanism

This is generally a good sign — the device can be used freely with any compatible SIM card.

### Important notes

- SSU only allows usage with SIM cards **from the region/country where the unlock was signed**
- Using a SIM from an unsupported region may still be blocked
- Re-lock can happen silently after an OTA update
- This app reads system properties via `getprop` — **no root or ADB required**

### Requirements

- Android 8.0 (API 26) or higher
- Samsung device (SSU/Knox props may not exist on other brands)

### Build

```bash
# Clean
./gradlew clean

# Debug APK
./gradlew assembleDebug

# Release APK (arm64)
./gradlew assembleRelease -Pandroid.injected.build.abi=arm64-v8a
```

---

## 简体中文

### 什么是 SSU？

**SSU（Service Software Unlock，服务软件解锁）** 是三星的区域 SIM 卡锁临时解锁机制。由三星授权，允许锁定至特定运营商或地区的设备使用受支持地区的 SIM 卡。

> ⚠️ **SSU 不是永久解锁。** 设备随时可能被重新锁定——例如在系统更新、恢复出厂设置之后，或三星远程撤销解锁授权时。

### "No SSU" 是什么意思？

如果 App 显示 **No SSU**，表示该设备：
- 从未被锁定至特定地区或运营商，或
- 不支持 SSU 机制

这通常是好事——设备可以自由使用任何兼容的 SIM 卡。

### 重要说明

- SSU 仅允许使用**解锁时签名所对应地区/国家**的 SIM 卡
- 使用不受支持地区的 SIM 卡仍可能被拒绝
- OTA 更新后可能在用户不知情的情况下被重新锁定
- 本 App 通过 `getprop` 读取系统属性，**无需 Root 或 ADB**

### 系统要求

- Android 8.0（API 26）或更高版本
- 三星设备（其他品牌可能不存在 SSU/Knox 相关属性）

### 编译

```bash
# 清理
./gradlew clean

# Debug APK
./gradlew assembleDebug

# Release APK（arm64）
./gradlew assembleRelease -Pandroid.injected.build.abi=arm64-v8a
```
