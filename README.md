# Scamsung SSU Checker

English | [简体中文](#简体中文)

---

## English

### What is SSU?

**SSU (Service Software Unlock)** is Samsung's regional SIM lock bypass mechanism. It is a **temporary unlock** granted by Samsung that allows a device locked to one carrier or region to be used with a SIM card from a supported region.

> ⚠️ **SSU is not a permanent unlock.** Your device can be re-locked at any time — for example after a software update, factory reset, or when Samsung revokes the unlock remotely.

### What does "No SSU / Global Unlocked" mean?

If the app shows **Global Unlocked**, it means the device either:
- Has a Carrier ID that is globally unlocked by default (XAA, CHM, CHN, CHC), or
- Has no SSU lock mechanism present on the device

The device can be used freely with any compatible SIM card.

### ATT (AT&T) Devices — S25 Series and Earlier

For Samsung devices with **Carrier ID = ATT** on models before the S26 series (S931, S936, S937, S938, F766, F966 prefix models such as SM-S931U), **SSU does not apply**. These devices use a different carrier lock mechanism that predates SSU.

If you see **🤔 ATT Not Recognized**, you need to check your unlock status manually through AT&T or Samsung's official channels.

### Chip Accuracy Notice

| Chip | SSU Detection |
|------|--------------|
| SM8650 (Snapdragon 8 Gen 4) and newer | ✅ Accurate |
| SM8550, SM8450, SM8350 | ⚠️ Not supported — insert foreign SIM to verify manually |
| Exynos / MediaTek | ⚠️ Results may be inaccurate — warning shown in app |

**SSU detection is confirmed accurate for SM8650 (Snapdragon 8 Gen 4) and newer chips.** For older chips and non-Qualcomm platforms, the app will display a warning.

### Important notes

- SSU only allows usage with SIM cards **from the region/country where the unlock was signed**
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
./gradlew assembleRelease
```

---

## 简体中文

### 什么是 SSU？

**SSU（Service Software Unlock，服务软件解锁）** 是三星的区域 SIM 卡锁临时解锁机制。由三星授权，允许锁定至特定运营商或地区的设备使用受支持地区的 SIM 卡。

> ⚠️ **SSU 不是永久解锁。** 设备随时可能被重新锁定——例如在系统更新、恢复出厂设置之后，或三星远程撤销解锁授权时。

### "No SSU / 全球无锁"是什么意思？

如果 App 显示 **全球无锁**，表示该设备：
- 运营商 ID 属于默认全球无锁型号（XAA、CHM、CHN、CHC），或
- 设备上不存在 SSU 锁定机制

设备可自由使用任何兼容的 SIM 卡。

### ATT（AT&T）机型 — S25 系列及更早

对于**运营商 ID = ATT**、型号在 S26 系列之前的三星设备（S931、S936、S937、S938、F766、F966 开头的型号，如 SM-S931U），**SSU 机制不适用**。这些设备使用早于 SSU 的不同运营商锁定方式。

如果显示 **🤔 ATT 无法识别**，请通过 AT&T 或三星官方渠道手动查询解锁状态。

### 芯片准确性说明

| 芯片 | SSU 检测 |
|------|----------|
| SM8650（骁龙 8 Gen 4）及更新 | ✅ 准确 |
| SM8550、SM8450、SM8350 | ⚠️ 不支持 — 请自行插入外国 SIM 卡验证 |
| Exynos / 联发科 MTK | ⚠️ 结果可能不准确 — App 内会显示警告 |

**SM8650（骁龙 8 Gen 4）及更新芯片的 SSU 检测已确认准确。** 旧芯片及非高通平台将显示警告提示。

### 重要说明

- SSU 仅允许使用**解锁时签名所对应地区/国家**的 SIM 卡
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
./gradlew assembleRelease
```
