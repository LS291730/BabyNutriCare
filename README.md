# BabyNutriCare - 低月龄宝宝饮食营养搭配APP

专为0-3岁婴幼儿设计的智能饮食营养搭配工具，基于宝宝过往饮食记录、现有食材，智能推算营养缺口，动态推荐科学、均衡、适配月龄的辅食/正餐搭配方案。

## 产品特点

- **智能配餐**：基于宝宝月龄、过敏史、饮食禁忌，智能生成配餐方案
- **周维度续配餐**：记录周一至周二饮食，自动推荐周三及后续剩余天数的专属饮食
- **日维度精准配餐**：记录当日早餐，自动推荐午餐、晚餐的搭配方案
- **营养分析**：实时分析营养摄入情况，精准推荐营养补充方案
- **离线使用**：所有数据本地存储，无需联网即可使用核心功能

## 技术栈

- **语言**：Kotlin 1.9.20
- **架构**：MVVM + Clean Architecture
- **UI框架**：Jetpack Compose
- **依赖注入**：Hilt
- **本地数据库**：Room 2.6+
- **协程**：Coroutines + Flow
- **导航**：Navigation Compose
- **图片加载**：Coil
- **图表**：Vico

## 项目结构

```
BabyNutriCare/
├── app/                          # 主应用模块
├── core/                         # 核心模块
├── feature/                      # 功能模块
│   ├── diet/                    # 饮食记录功能
│   ├── mealplan/                # 配餐功能
│   ├── nutrition/               # 营养分析功能
│   ├── ingredient/              # 食材管理功能
│   ├── baby/                    # 宝宝信息功能
│   └── knowledge/               # 知识库功能
└── gradle/                       # Gradle配置
```

## 开发环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17
- Android SDK API 30+

## 构建项目

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd BabyNutriCare

# 使用Android Studio打开项目并同步Gradle
# 首次打开时Android Studio会自动下载Gradle Wrapper和依赖
```

> **注意**：项目使用JDK 17构建，请确保本地JDK版本为17+。
> `gradle-wrapper.jar` 在首次Android Studio同步时会自动生成，也可通过
> `gradle wrapper` 命令手动生成。

## 运行测试

```bash
# 运行core模块单元测试（配餐算法、营养计算）
gradlew :core:testDebugUnitTest
```

## 云端构建 APK（推荐）

项目已配置 **GitHub Actions 自动构建**，无需本地安装 Android 开发环境即可生成可安装的 APK。

### 操作步骤

1. **创建 GitHub 仓库**
   - 访问 https://github.com/new 创建一个新仓库（如 `BabyNutriCare`）

2. **推送项目到 GitHub**
   ```bash
   cd E:\work\tao
   git init
   git add .
   git commit -m "init: 宝宝营养家APP"
   git branch -M main
   git remote add origin https://github.com/<你的用户名>/BabyNutriCare.git
   git push -u origin main
   ```

3. **自动触发云端构建**
   - 推送后 GitHub Actions 会自动开始构建（无需任何配置）
   - 也可以在仓库页面 **Actions → Build APK → Run workflow** 手动触发

4. **下载 APK**
   - 构建完成后进入仓库 **Actions** 页面，点击最新一次构建
   - 在页面底部的 **Artifacts** 区域下载 `BabyNutriCare-debug-apk`
   - 解压后得到 `app-debug.apk`，可直接安装到 Android 10+ 手机

> 构建日志和测试报告也会作为 artifact 上传，构建失败时可下载排查。
> 每次 `push` 到 `main`/`master` 分支都会自动重新构建最新版本。

## 功能模块

### 1. 饮食记录模块
- 添加饮食记录（早餐/午餐/晚餐/加餐）
- 饮食日历查看
- 饮食统计
- 饮食模板管理

### 2. 智能配餐模块
- 现有食材智能配餐
- 周维度配餐（动态续配餐）
- 日维度配餐
- 配餐方案详情

### 3. 营养分析模块
- 营养摄入概览
- 营养缺口分析
- 营养趋势图
- 月龄适配筛选

### 4. 食材管理模块
- 食材库管理
- 食材分类
- 食材收藏
- 食材营养信息

### 5. 宝宝信息模块
- 宝宝信息录入
- 月龄自动计算
- 过敏史管理
- 饮食禁忌管理

### 6. 知识库模块
- 辅食添加指南
- 营养知识科普
- 过敏源知识
- 食材禁忌

## 营养标准

APP内置0-3岁各月龄的营养标准，包括：
- 蛋白质、脂肪、碳水化合物
- 钙、铁、锌
- 维生素A、维生素C、维生素D、维生素E
- 维生素B1、维生素B2、叶酸

## 许可证

Copyright © 2024 BabyNutriCare. All rights reserved.